package com.example.musicplayerapp;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.Services.OnClearFromRecentService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.example.musicplayerapp.MainActivity.albumFiles;
import static com.example.musicplayerapp.MainActivity.controlMusicPlayerFromMain;
import static com.example.musicplayerapp.MainActivity.musicFiles;
import static com.example.musicplayerapp.MainActivity.playOnline;
import static com.example.musicplayerapp.MainActivity.play_pause_main;
import static com.example.musicplayerapp.MainActivity.song_artist_main;
import static com.example.musicplayerapp.MainActivity.song_name_main;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    static int position = -1;
    static String tempSongName;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    static boolean shufferBoolean = false, repeatBoolean = false;
    static TextView song_name, song_artist, durationPlayed, durationTotal;
    static ImageView back_btn, menu_btn, id_shuffer, id_prev, id_next, id_repeat, cover_art;
    static FloatingActionButton play_pause;
    static ObjectAnimator anim;

    SeekBar seekBar;
    NotificationManager notificationManager;
    private Thread playThread, prevThread, nextThread;

    private Handler handler = new Handler();
    private boolean readyToListen;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = "";
            if (readyToListen) {
                action = intent.getExtras().getString("actionname");
                readyToListen = false;
            }

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
                    if (play_pause_main != null) {
                        play_pause_main.setImageResource(R.drawable.ic_baseline_play_arrow);
                    }
                    mediaPlayer.pause();
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();

        playOnline = false;

        continuePlayingMusic();
        initAnim();

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
                CreateNotification.createNotification(getBaseContext(), R.drawable.ic_baseline_pause, listSongs.get(position));
            } else {
                CreateNotification.createNotification(getBaseContext(), R.drawable.ic_baseline_play_arrow, listSongs.get(position));
            }
        } else {
            CreateNotification.createNotification(getBaseContext(), R.drawable.ic_baseline_pause, listSongs.get(position));
        }
    }

    private void continuePlayingMusic() {
        if (tempSongName != null && getIntent().getStringExtra("songName") != null) {
            if (getIntent().getStringExtra("songName").equals(tempSongName)) {
                if (mediaPlayer != null) {
                    uri = Uri.parse(listSongs.get(position).getPath());
                    metaData(uri);
                    if (mediaPlayer.isPlaying()) {
                        play_pause.setImageResource(R.drawable.ic_baseline_pause);
                    } else {
                        play_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
                    }
                    return;
                }
            }
        }
        getIntentMethod();
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
                intent.putExtra("songName", tempSongName);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("back_btn", "onBackPressed");
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
    }

    private void getIntentMethod() {
        if (getIntent().getStringExtra("playAlbum") != null) {
            listSongs.clear();

            position = getIntent().getIntExtra("songIndex", -1);
            listSongs.addAll(albumFiles.get(getIntent().getIntExtra("albumIndex", -1)));
            listSongs.remove(0);
        } else {
            position = getIntent().getIntExtra("position", -1);
            listSongs = musicFiles;
        }

        tempSongName = listSongs.get(position).getTitle();

        if (listSongs != null) {
            play_pause.setImageResource(R.drawable.ic_baseline_pause);
            Log.d("uri", "getIntentMethod: " + listSongs.size() + " " + position);

            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    readyToListen = true;
                }
            });
        } else {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    readyToListen = true;
                }
            });
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

        /*MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());

        byte[] art = retriever.getEmbeddedPicture();*/
        MusicAdapter.setImage(MusicAdapter.getAlbumArt(uri.toString()), getApplicationContext(), cover_art);

        int totalTime = mediaPlayer.getDuration() / 1000;

        seekBar.setMax(totalTime);
        durationTotal.setText(formatTime(totalTime));

        if (!repeatBoolean) {
            id_repeat.setImageResource(R.drawable.ic_baseline_repeat);
        } else {
            id_repeat.setImageResource(R.drawable.ic_baseline_repeat_on);
        }

        tempSongName = listSongs.get(position).getTitle();
    }

    @Override
    protected void onResume() {
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("music_musicoff"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }
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

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                readyToListen = true;
            }
        });

        if (song_artist_main != null && song_name_main != null) {
            controlMusicPlayerFromMain(getApplicationContext());
        }

        metaData(uri);
        anim.cancel();
        anim.start();
    }

    private void play_pauseClicked() {
        if (mediaPlayer.isPlaying()) {
            CreateNotification.createNotification(getBaseContext(), R.drawable.ic_baseline_play_arrow, listSongs.get(position));
            if (play_pause_main != null) {
                play_pause_main.setImageResource(R.drawable.ic_baseline_play_arrow);
            }
            play_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
            mediaPlayer.pause();

            runOnUiThread();
            anim.pause();
        } else {
            CreateNotification.createNotification(getBaseContext(), R.drawable.ic_baseline_pause, listSongs.get(position));
            if (play_pause_main != null) {
                play_pause_main.setImageResource(R.drawable.ic_baseline_pause);
            }
            play_pause.setImageResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();

            runOnUiThread();
            anim.resume();
        }
    }

    private void id_nextClicked() {
        mediaPlayer.stop();
        mediaPlayer.release();

        if (!repeatBoolean) {
            position = (position + 1) % listSongs.size();
        }

        Log.d("position", "id_nextClicked: " + position);

        uri = Uri.parse(listSongs.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        runOnUiThread();

        play_pause.setImageResource(R.drawable.ic_baseline_pause);
        CreateNotification.createNotification(getBaseContext(), R.drawable.ic_baseline_pause, listSongs.get(position));
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                readyToListen = true;
            }
        });

        if (song_artist_main != null && song_name_main != null) {
            controlMusicPlayerFromMain(getApplicationContext());
        }
        cover_art.clearAnimation();

        metaData(uri);
        anim.cancel();
        anim.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        id_nextClicked();
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }*/

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

    private void initAnim() {
        ImageView spinImage = findViewById(R.id.cover_art);

        anim = ObjectAnimator.ofFloat(spinImage, "rotation", 0, 360);
        anim.setDuration(5000);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setInterpolator(new LinearInterpolator());

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                anim.start();
            } else {
                anim.start();
                anim.pause();
            }
        } else {
            anim.start();
        }

    }
}