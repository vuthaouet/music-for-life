package com.example.musicplayerapp.PlayMusic;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.example.musicplayerapp.Config;
import com.example.musicplayerapp.Database.DownloadFile;
import com.example.musicplayerapp.Entity.Comments;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.MainActivity;
import com.example.musicplayerapp.R;
import com.example.musicplayerapp.CreateNotification;
import com.example.musicplayerapp.Random.RandomString;
import com.example.musicplayerapp.Services.OnClearFromRecentService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.musicplayerapp.Database.Firestore.musicFilesOnline;
import static com.example.musicplayerapp.Database.SearchFiles.lookForSongsByTitle;
import static com.example.musicplayerapp.PlayMusic.PlayerActivity.mediaPlayer;

public class PlayerActivityOnline extends PlayerMusic implements MediaPlayer.OnCompletionListener {

    /*public static int Config.songIndex = 0;
    public static ArrayList<MusicFiles> Config.currentListSong;*/

    private RecyclerView recyclerViewComment;
    private EditText commentContent;
    private ImageButton postComment;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    private boolean favoriteSong = false;

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

        Config.playOnline = true;

        continuePlayingMusic();

        firebaseAuth = FirebaseAuth.getInstance();
        /*for (MusicFiles musicFiles : musicFilesOnline) {
            Config.currentListSong.add(musicFiles);
        }*/
        //getIntentMethod();
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

        listOfComments();
        backToMain();
    }

    private void listOfComments() {
        Query query = firebaseFirestore.collection("Music")
                .document(Config.currentListSong.get(Config.songIndex).getId())
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

    private void postComment(String content) {
        Date date = new Date(); // This object contains the current date value
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        CollectionReference collectionReference = firebaseFirestore.collection("Music")
                .document(Config.currentListSong.get(Config.songIndex).getId())
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
        super.onResume();

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = commentContent.getText().toString();
                if (!content.isEmpty()) {
                    postComment(content);
                } else {
                    commentContent.setError("Type your comment");
                }
            }
        });
    }

    private void isLiked() {
        Log.d("checkLike", "isLiked: Running");
        String idSong = Config.currentListSong.get(Config.songIndex).getId();

        favorite_song.setImageResource(R.drawable.ic_baseline_favorite_border_24);

        firebaseFirestore.collection("Users")
                .whereArrayContains("likes", idSong)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.d("checkLike", "onComplete: Before check");
                                if (documentSnapshot.getId().equals(firebaseUser.getUid())) {
                                    Log.d("checkLike", "onComplete: Checked");
                                    favorite_song.setImageResource(R.drawable.ic_baseline_favorite_border_24_on);
                                    return;
                                }
                            }
                        }
                    }
                });
    }

    @Override
    protected void addMyFavoriteSong() {
        //Log.d("lovesong", "addMyFavoriteSong: OK");
        String idSong = Config.currentListSong.get(Config.songIndex).getId();

        if (!favoriteSong) {
            addFavorList(idSong);
        } else {
            removeFavorList(idSong);
        }
    }

    protected void id_nextClicked() {

        if (!Config.repeatBoolean) {
            Config.songIndex = (Config.songIndex + 1) % Config.currentListSong.size();
        }

        if (Config.shufferBoolean) {
            Config.songIndex = RandomString.getNumber(Config.currentListSong.size(), Config.songIndex);
        }

        runningMusic();
        runOnUiThread();
        metaData();
    }

    protected void id_prevClicked() {
        if (!Config.repeatBoolean) {
            Config.songIndex = (Config.currentListSong.size() + Config.songIndex - 1) % Config.currentListSong.size();
        }

        if (Config.shufferBoolean) {
            Config.songIndex = RandomString.getNumber(Config.currentListSong.size(), Config.songIndex);
        }

        runningMusic();
        runOnUiThread();
        metaData();
    }

    @Override
    protected void continuePlayingMusic() {
        String currentSong = getIntent().getStringExtra("idSongOnl");
        if (currentSong != null) {
            if (currentSong.equals(Config.currentIdSong)) {
                if (mediaPlayer != null) {
                    metaData();
                    if (mediaPlayer.isPlaying()) {
                        play_pause.setImageResource(R.drawable.ic_baseline_pause);
                    } else {
                        play_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
                    }
                    if (Config.repeatBoolean) {
                        setRepeatState(true);
                    }
                    if (Config.shufferBoolean) {
                        setShufferState(true);
                    }
                    return;
                }
            }
        }
        getIntentMethod();
    }

    @Override
    protected void getIntentMethod() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        setRepeatState(false);
        setShufferState(false);
        Config.songIndex = getIntent().getIntExtra("songIndexOnl", -1);

        if (getIntent().getAction() != null) {
            switch (getIntent().getAction()) {
                case "searchSongListener":
                    Config.currentListSong = new ArrayList<>();
                    Config.currentListSong.add(lookForSongsByTitle.get(Config.songIndex));
                    Config.songIndex = 0;
                    break;
                default:
                    break;
            }
        } else {
            Config.currentListSong = musicFilesOnline;
        }
        runningMusic();

        metaData();
    }

    protected void play_pauseClicked() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            play_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
        } else {
            mediaPlayer.start();
            play_pause.setImageResource(R.drawable.ic_baseline_pause);
        }
    }

    private void metaData() {
        song_name.setText(Config.currentListSong.get(Config.songIndex).getTitle());
        song_artist.setText(Config.currentListSong.get(Config.songIndex).getArtist());

        if (!Config.currentListSong.get(Config.songIndex).getImage().isEmpty()) {
            setImage(Config.currentListSong.get(Config.songIndex).getImage(), this, cover_art);
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
        Config.currentIdSong = Config.currentListSong.get(Config.songIndex).getId();
        isLiked();
    }

    private void runningMusic() {
        try {
            //Log.d("firebase", "onClick: " + Config.currentListSong.get(Config.songIndex).getId());
            Config.currentIdSong = Config.currentListSong.get(Config.songIndex).getId();

            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }

            mediaPlayer.setDataSource(Config.currentListSong.get(Config.songIndex).getLink());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepare();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    protected void initViews() {
        super.initViews();

        recyclerViewComment = findViewById(R.id.recyclerViewComment);
        postComment = findViewById(R.id.post_comment_btn);
        commentContent = findViewById(R.id.comment_input);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        id_nextClicked();
    }

    private void showNotification() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                CreateNotification.createNotification(getApplicationContext(), R.drawable.ic_baseline_pause, Config.currentListSong.get(Config.songIndex));
            } else {
                CreateNotification.createNotification(getApplicationContext(), R.drawable.ic_baseline_play_arrow, Config.currentListSong.get(Config.songIndex));
            }
        } else {
            CreateNotification.createNotification(getApplicationContext(), R.drawable.ic_baseline_pause, Config.currentListSong.get(Config.songIndex));
        }

        //CreateNotification.createNotification(getBaseContext(), R.drawable.ic_baseline_pause, Config.currentListSong.get(Config.songIndex));
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

    private void addFavorList(String idSong) {
        Map<String, Object> likedSong = new HashMap<>();
        likedSong.put("likes", FieldValue.arrayUnion(idSong));

        firebaseFirestore.collection("Users")
                .document(firebaseUser.getUid())
                .set(likedSong, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            favorite_song.setImageResource(R.drawable.ic_baseline_favorite_border_24_on);
                            favoriteSong = true;
                        }
                    }
                });
    }

    private void removeFavorList(String idSong) {
        Map<String, Object> likedSong = new HashMap<>();
        likedSong.put("likes", FieldValue.arrayRemove(idSong));

        firebaseFirestore.collection("Users")
                .document(firebaseUser.getUid())
                .set(likedSong, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            favorite_song.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                            favoriteSong = false;
                        }
                    }
                });
    }
}