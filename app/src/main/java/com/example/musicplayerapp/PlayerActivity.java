package com.example.musicplayerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicplayerapp.Services.OnClearFromRecentService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.example.musicplayerapp.MainActivity.controlMusicPlayerFromMain;
import static com.example.musicplayerapp.MainActivity.musicFiles;
import static com.example.musicplayerapp.MainActivity.play_pause_main;
import static com.example.musicplayerapp.MainActivity.song_artist_main;
import static com.example.musicplayerapp.MainActivity.song_name_main;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView song_name, song_artist, durationPlayed, durationTotal;
    ImageView back_btn, menu_btn, id_shuffer, id_prev, id_next, id_repeat, cover_art;
    FloatingActionButton play_pause;
    SeekBar seekBar;

    static int position;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    static boolean shufferBoolean = false, repeatBoolean = false;

    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;

    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("music_music"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

        continuePlayingMusic();

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

        mediaPlayer.setOnCompletionListener(this);
        runOnUiThread();

        id_shuffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!shufferBoolean) {
                    id_shuffer.setImageResource(R.drawable.ic_baseline_shuffle_on);
                    shufferBoolean = true;
                } else {
                    id_shuffer.setImageResource(R.drawable.ic_baseline_shuffle);
                    shufferBoolean = false;
                }
            }
        });

        id_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!repeatBoolean) {
                    id_repeat.setImageResource(R.drawable.ic_baseline_repeat_on);
                    repeatBoolean = true;
                } else {
                    id_repeat.setImageResource(R.drawable.ic_baseline_repeat);
                    repeatBoolean = false;
                }
            }
        });

        showNotification();
        backToMain();
    }

    private void showNotification() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                CreateNotification.createNotification(getApplicationContext(), R.drawable.ic_baseline_pause, listSongs.get(position));
            } else {
                CreateNotification.createNotification(getApplicationContext(), R.drawable.ic_baseline_play_arrow, listSongs.get(position));
            }
        } else {
            CreateNotification.createNotification(getApplicationContext(), R.drawable.ic_baseline_pause, listSongs.get(position));
        }
    }

    private void continuePlayingMusic() {
        if (position == getIntent().getIntExtra("position", -1)) {
            if (mediaPlayer != null) {
                uri = Uri.parse(listSongs.get(position).getPath());
                metaData(uri);
                if (mediaPlayer.isPlaying()) {
                    play_pause.setImageResource(R.drawable.ic_baseline_pause);
                } else {
                    play_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
                }
            } else {
                getIntentMethod();
            }
        } else {
            getIntentMethod();
        }
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

    private void initViews() {
        song_name = findViewById(R.id.song_name);
        song_artist = findViewById(R.id.song_artist);
        durationPlayed = findViewById(R.id.durationPlayed);
        durationTotal = findViewById(R.id.durationTotal);

        back_btn = findViewById(R.id.back_btn);
        menu_btn = findViewById(R.id.menu_btn);

        id_shuffer = findViewById(R.id.id_shuffer);
        id_prev = findViewById(R.id.id_prev);
        id_next = findViewById(R.id.id_next);
        id_repeat = findViewById(R.id.id_repeat);
        cover_art = findViewById(R.id.cover_art);

        play_pause = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        listSongs = musicFiles;
        if (listSongs != null) {
            play_pause.setImageResource(R.drawable.ic_baseline_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        } else {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }

        repeatBoolean = false;
        id_repeat.setImageResource(R.drawable.ic_baseline_repeat);

        metaData(uri);
    }

    private String formatTime(int mCurrentPosition) {
        String minutes = String.valueOf(mCurrentPosition / 60);
        String second = String.valueOf(mCurrentPosition % 60);

        if (second.length() == 1) {
            return minutes + ":0" + second;
        }

        return minutes + ":" + second;
    }

    private void metaData(Uri uri) {
        song_name.setText(listSongs.get(position).getTitle());
        song_artist.setText(listSongs.get(position).getArtist());
        showNotification();
        mediaPlayer.setOnCompletionListener(this);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());

        byte[] art = retriever.getEmbeddedPicture();

        if (art != null) {
            Glide.with(this).asBitmap()
                    .load(art)
                    .into(cover_art);
        } else {
            Glide.with(this)
                    .load(R.drawable.pepe_the_frog)
                    .into(cover_art);
        }

        int totalTime = mediaPlayer.getDuration() / 1000;

        seekBar.setMax(totalTime);
        durationTotal.setText(formatTime(totalTime));

        if (!repeatBoolean) {
            id_repeat.setImageResource(R.drawable.ic_baseline_repeat);
        } else {
            id_repeat.setImageResource(R.drawable.ic_baseline_repeat_on);
        }
    }

    @Override
    protected void onResume() {
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();

        super.onResume();
    }

    private void prevThreadBtn() {
        prevThread = new Thread() {
            public void run() {
                super.run();
                id_prev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        repeatBoolean = false;
                        id_repeat.setImageResource(R.drawable.ic_baseline_repeat);
                        id_prevClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            public void run() {
                super.run();
                play_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        play_pauseClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void nextThreadBtn() {
        nextThread = new Thread() {
            public void run() {
                super.run();
                id_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        repeatBoolean = false;
                        id_repeat.setImageResource(R.drawable.ic_baseline_repeat);
                        id_nextClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void id_prevClicked() {
        mediaPlayer.stop();
        mediaPlayer.release();

        position = (listSongs.size() + position - 1) % listSongs.size();
        uri = Uri.parse(listSongs.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        runOnUiThread();

        play_pause.setImageResource(R.drawable.ic_baseline_pause);
        mediaPlayer.start();

        if (song_artist_main != null && song_name_main != null) {
            controlMusicPlayerFromMain(getApplicationContext());
        }


        metaData(uri);
    }

    private void play_pauseClicked() {
        if (mediaPlayer.isPlaying()) {
            CreateNotification.createNotification(PlayerActivity.this, R.drawable.ic_baseline_play_arrow, listSongs.get(position));
            if (play_pause_main != null) {
                play_pause_main.setImageResource(R.drawable.ic_baseline_play_arrow);
            }
            play_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
            mediaPlayer.pause();

            runOnUiThread();
        } else {
            CreateNotification.createNotification(PlayerActivity.this, R.drawable.ic_baseline_pause, listSongs.get(position));
            if (play_pause_main != null) {
                play_pause_main.setImageResource(R.drawable.ic_baseline_pause);
            }
            play_pause.setImageResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();

            runOnUiThread();
        }
    }

    private void id_nextClicked() {
        mediaPlayer.stop();
        mediaPlayer.release();

        if (!repeatBoolean) {
            position = (position + 1) % listSongs.size();
        }

        uri = Uri.parse(listSongs.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        runOnUiThread();

        play_pause.setImageResource(R.drawable.ic_baseline_pause);
        CreateNotification.createNotification(PlayerActivity.this, R.drawable.ic_baseline_pause, listSongs.get(position));
        mediaPlayer.start();

        if (song_artist_main != null && song_name_main != null) {
            controlMusicPlayerFromMain(getApplicationContext());
        }

        metaData(uri);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        id_nextClicked();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action) {
                case CreateNotification.ACTION_PREVIOUS:
                    id_prevClicked();
                    break;
                case CreateNotification.ACTION_PLAY:
                    play_pauseClicked();
                    break;
                case CreateNotification.ACTION_NEXT:
                    id_nextClicked();
                    break;
                case CreateNotification.CLOSE_NOTIFICATION:
                    notificationManager.cancelAll();
                    play_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
                    play_pause_main.setImageResource(R.drawable.ic_baseline_play_arrow);
                    mediaPlayer.pause();

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }

        unregisterReceiver(broadcastReceiver);
    }

    public void runOnUiThread() {
        PlayerActivity.this.runOnUiThread(new Runnable() {
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
}