package com.example.musicplayerapp.PlayMusic;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayerapp.Adapter.CommentAdapter;
import com.example.musicplayerapp.Database.DownloadFile;
import com.example.musicplayerapp.Entity.Comments;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.Entity.Users;
import com.example.musicplayerapp.MainActivity;
import com.example.musicplayerapp.R;
import com.example.musicplayerapp.CreateNotification;
import com.example.musicplayerapp.Services.OnClearFromRecentService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.musicplayerapp.Database.Firestore.musicFilesOnline;
import static com.example.musicplayerapp.MainActivity.playOnline;
import static com.example.musicplayerapp.PlayMusic.PlayerActivity.mediaPlayer;

public class PlayerActivityOnline extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    public static int positionOnl = 0;

    public static TextView song_name, song_artist, durationPlayed, durationTotal;
    public static ImageView back_btn, menu_btn, id_shuffer, id_prev, id_next, id_repeat, cover_art;
    public static FloatingActionButton play_pause;

    private RecyclerView recyclerViewComment;
    private EditText commentContent;
    private ImageButton postComment;

    SeekBar seekBar;

    NotificationManager notificationManager;

    private Thread playThread, prevThread, nextThread;
    private ArrayList<MusicFiles> listSongsOnline;

    private Handler handler = new Handler();
    private boolean readyToListen = true;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    static void setImage(String url, Context context, ImageView imageView) {
        if (url != null) {
            Glide.with(context).asBitmap()
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(R.drawable.pepe_the_frog)
                    .into(imageView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews();

        playOnline = true;

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        /*for (MusicFiles musicFiles : musicFilesOnline) {
            listSongsOnline.add(musicFiles);
        }*/
        listSongsOnline = musicFilesOnline;

        positionOnl = getIntent().getIntExtra("songIndexOnl", -1);

        runningMusic();
        mediaPlayer.setOnCompletionListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null && b) {
                    mediaPlayer.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        menuAction();
        runOnUiThread();
        metaData();

        listOfComments();

        backToMain();
    }

    private void menuAction() {
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(PlayerActivityOnline.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.delete:
                                Toast.makeText(PlayerActivityOnline.this, "Delete Clicked", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.download:
                                String name = listSongsOnline.get(positionOnl).getTitle();
                                String link = listSongsOnline.get(positionOnl).getLink();
                                DownloadFile.downloadByLink(name, link, PlayerActivityOnline.this);
                                break;
                            case R.id.comment:
                                postComment();
                                break;
                        }
                        return true;
                    }
                }));
            }
        });
    }

    private void listOfComments() {
        Query query = firebaseFirestore.collection("Music")
                .document(listSongsOnline.get(positionOnl).getId())
                .collection("Comment");

        if (query != null) {
            query.limit(5).orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    List<Comments> commentsList = value.toObjects(Comments.class);
                    //Log.d("cmt", "onEvent: " + commentsList.get(0).getDate());
                    CommentAdapter commentAdapter = new CommentAdapter(PlayerActivityOnline.this, commentsList);

                    recyclerViewComment.setHasFixedSize(true);
                    recyclerViewComment.setLayoutManager(new LinearLayoutManager(PlayerActivityOnline.this));
                    recyclerViewComment.setAdapter(commentAdapter);


                    /*for (Comments comments : commentsList) {
                        Task<DocumentSnapshot> users = firebaseFirestore.collection("Users").document(comments.getUId()).get();
                        users.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    //Users userInfo = task.getResult().toObject(Users.class);

                                }
                            }
                        });
                    }*/
                }
            });
        }
    }

    private void postComment() {
        Date date = new Date(); // This object contains the current date value
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String content = commentContent.getText().toString();

        CollectionReference collectionReference = firebaseFirestore.collection("Music")
                .document(listSongsOnline.get(positionOnl).getId())
                .collection("Comment");

        Map<String, Object> comment = new HashMap<>();

        comment.put("uId", firebaseAuth.getCurrentUser().getUid());
        comment.put("uName", firebaseAuth.getCurrentUser().getDisplayName());
        comment.put("date", formatter.format(date));
        comment.put("content", content);

        collectionReference.add(comment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Log.d("cmt", "onComplete: Done");
                commentContent.setText("");
            }
        });
    }

    protected void onResume() {
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();

        super.onResume();

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("music_musiconl"));
            startService(new Intent(this, OnClearFromRecentService.class));
        }
    }

    private void nextThreadBtn() {
        nextThread = new Thread() {
            public void run() {
                super.run();
                id_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        id_repeat.setImageResource(R.drawable.ic_baseline_repeat);
                        id_nextClickedOnl();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void id_nextClickedOnl() {
        positionOnl = (positionOnl + 1) % listSongsOnline.size();

        runningMusic();
        runOnUiThread();
        metaData();
    }

    private void prevThreadBtn() {
        prevThread = new Thread() {
            public void run() {
                super.run();
                id_prev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        id_repeat.setImageResource(R.drawable.ic_baseline_repeat);
                        id_prevClickedOnl();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void id_prevClickedOnl() {
        positionOnl = (listSongsOnline.size() + positionOnl - 1) % listSongsOnline.size();

        runningMusic();
        runOnUiThread();
        metaData();
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            public void run() {
                super.run();
                play_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        play_pauseClickedOnl();
                    }
                });
            }
        };
        playThread.start();
    }

    private void play_pauseClickedOnl() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            play_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
        } else {
            mediaPlayer.start();
            play_pause.setImageResource(R.drawable.ic_baseline_pause);
        }
    }

    public void runOnUiThread() {
        PlayerActivityOnline.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    durationPlayed.setText(formatTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void metaData() {
        song_name.setText(listSongsOnline.get(positionOnl).getTitle());
        song_artist.setText(listSongsOnline.get(positionOnl).getArtist());

        if (!listSongsOnline.get(positionOnl).getImage().isEmpty()) {
            setImage(listSongsOnline.get(positionOnl).getImage(), this, cover_art);
        }

        play_pause.setImageResource(R.drawable.ic_baseline_pause);

        showNotification();

        //mediaPlayerOnline.setOnCompletionListener(this);

        int totalTime = mediaPlayer.getDuration() / 1000;

        seekBar.setMax(totalTime);
        durationTotal.setText(formatTime(totalTime));

        /*if (!repeatBoolean) {
            id_repeat.setImageResource(R.drawable.ic_baseline_repeat);
        } else {
            id_repeat.setImageResource(R.drawable.ic_baseline_repeat_on);
        }*/

        //tempSongName = listSongs.get(position).getTitle();
    }

    private void runningMusic() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }

            Log.d("firebase", "onClick: " + listSongsOnline.get(positionOnl).getId());

            mediaPlayer.setDataSource(listSongsOnline.get(positionOnl).getLink());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    readyToListen = true;
                }
            });
            mediaPlayer.prepare();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void initViews() {
        song_name = findViewById(R.id.song_name);
        song_artist = findViewById(R.id.song_artist);
        durationPlayed = findViewById(R.id.durationPlayed);
        durationTotal = findViewById(R.id.durationTotal);

        back_btn = findViewById(R.id.back_btn);
        menu_btn = findViewById(R.id.menu_btn);

        id_shuffer = findViewById(R.id.id_shuffle);
        id_prev = findViewById(R.id.id_prev);
        id_next = findViewById(R.id.id_next);
        id_repeat = findViewById(R.id.id_repeat);
        cover_art = findViewById(R.id.cover_art);

        play_pause = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);

        recyclerViewComment = findViewById(R.id.recyclerViewComment);
        postComment = findViewById(R.id.post_comment_btn);
        commentContent = findViewById(R.id.comment_input);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private String formatTime(int mCurrentPosition) {
        String minutes = String.valueOf(mCurrentPosition / 60);
        String second = String.valueOf(mCurrentPosition % 60);

        if (second.length() == 1) {
            return minutes + ":0" + second;
        }

        return minutes + ":" + second;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        id_nextClickedOnl();
    }

    private void showNotification() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                CreateNotification.createNotification(getApplicationContext(), R.drawable.ic_baseline_pause, listSongsOnline.get(positionOnl));
            } else {
                CreateNotification.createNotification(getApplicationContext(), R.drawable.ic_baseline_play_arrow, listSongsOnline.get(positionOnl));
            }
        } else {
            CreateNotification.createNotification(getApplicationContext(), R.drawable.ic_baseline_pause, listSongsOnline.get(positionOnl));
        }

        //CreateNotification.createNotification(getBaseContext(), R.drawable.ic_baseline_pause, listSongsOnline.get(positionOnl));
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID, "Music", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void backToMain() {
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }

        unregisterReceiver(broadcastReceiver);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = "";
            if (readyToListen) {
                action = intent.getExtras().getString("actionnameonl");
                readyToListen = false;
            }

            switch (action) {
                case CreateNotification.ACTION_PREVIOUS:
                    id_prevClickedOnl();
                    break;
                case CreateNotification.ACTION_PLAY:
                    play_pauseClickedOnl();
                    break;
                case CreateNotification.ACTION_NEXT:
                    id_nextClickedOnl();
                    break;
                case CreateNotification.CLOSE_NOTIFICATION:
                    notificationManager.cancelAll();
                    play_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
                    readyToListen = true;
                    mediaPlayer.pause();
                default:
                    break;
                /*case CreateNotification.CLOSE_NOTIFICATION:
                    notificationManager.cancelAll();
                    play_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
                    if (play_pause_main != null) {
                        play_pause_main.setImageResource(R.drawable.ic_baseline_play_arrow);
                    }
                    mediaPlayerOnline.pause();*/
            }
        }
    };
}