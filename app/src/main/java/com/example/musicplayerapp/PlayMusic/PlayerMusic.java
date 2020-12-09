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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayerapp.Config;
import com.example.musicplayerapp.CreateNotification;
import com.example.musicplayerapp.CurrentListSong;
import com.example.musicplayerapp.Database.DownloadFile;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.R;
import com.example.musicplayerapp.Services.OnClearFromRecentService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

abstract public class PlayerMusic extends AppCompatActivity {
    NotificationManager notificationManager;

    protected static MediaPlayer mediaPlayer;

    protected static TextView song_name, song_artist, durationPlayed, durationTotal, currentList;
    protected static ImageView back_btn, menu_btn, id_shuffer, id_prev, id_next, id_repeat, cover_art, favorite_song;
    protected static FloatingActionButton play_pause;
    protected SeekBar seekBar;

    private Handler handler = new Handler();

    protected Thread playThread, prevThread, nextThread;

    @Override
    protected void onResume() {
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            if (Config.playOnline) {
                //unregisterReceiver(broadcastReceiver);
                if (!Config.isRegisterOnline) {
                    Log.d("broadcastReceiver", "onResume: Online " + Config.isRegisterOnline);
                    registerReceiver(broadcastReceiver, new IntentFilter("music_musiconl"));
                    Config.isRegisterOnline = true;
                }
            } else {
                //unregisterReceiver(broadcastReceiver);
                if (!Config.isRegisterOffline) {
                    Log.d("broadcastReceiver", "onResume: Offline " + Config.isRegisterOffline);
                    registerReceiver(broadcastReceiver, new IntentFilter("music_musicoff"));
                    Config.isRegisterOffline = true;
                }
            }

            //BroadcastReceiverFunction.addReceiver(broadcastReceiver, new IntentFilter("music_music"), PlayerMusic.this);
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

        id_shuffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.shufferBoolean) {
                    setShufferState(false);
                } else {
                    setShufferState(true);
                }
            }
        });

        id_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.repeatBoolean) {
                    setRepeatState(false);
                } else {
                    setRepeatState(true);
                }
            }
        });

        currentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CurrentListSong.class));
            }
        });

        favorite_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMyFavoriteSong();
            }
        });
    }

    protected abstract void addMyFavoriteSong();

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID, "Music", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);*/
        Log.d("broadcastReceiver", "onDestroy: Offline + Online");
    }

    protected String formatTime(int mCurrentPosition) {
        String minutes = String.valueOf(mCurrentPosition / 60);
        String second = String.valueOf(mCurrentPosition % 60);

        if (second.length() == 1) {
            return minutes + ":0" + second;
        }

        return minutes + ":" + second;
    }

    public void runOnUiThread() {
        PlayerMusic.this.runOnUiThread(new Runnable() {
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

    protected void initViews() {
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

        currentList = findViewById(R.id.play_lists);
        favorite_song = findViewById(R.id.favorite_song);
    }

    protected abstract void id_nextClicked();

    protected abstract void play_pauseClicked();

    protected abstract void id_prevClicked();

    protected abstract void continuePlayingMusic();

    protected abstract void getIntentMethod();

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("broadcastReceiver", "onReceive: Offline + Online");
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
                    //readyToListen = true;
                    /*if (play_pause_main != null) {
                        play_pause_main.setImageResource(R.drawable.ic_baseline_play_arrow);
                    }*/
                    mediaPlayer.pause();
                default:
                    break;
            }
        }
    };

    private void prevThreadBtn() {
        prevThread = new Thread() {
            public void run() {
                super.run();
                id_prev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
                        id_nextClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    protected void setRepeatState(boolean state) {
        if (state) {
            Config.repeatBoolean = true;
            id_repeat.setImageResource(R.drawable.ic_baseline_repeat_on);

            Config.shufferBoolean = false;
            id_shuffer.setImageResource(R.drawable.ic_baseline_shuffle);
        } else {
            Config.repeatBoolean = false;
            id_repeat.setImageResource(R.drawable.ic_baseline_repeat);
        }
    }

    protected void setShufferState(boolean state) {
        if (state) {
            Config.shufferBoolean = true;
            id_shuffer.setImageResource(R.drawable.ic_baseline_shuffle_on);

            Config.repeatBoolean = false;
            id_repeat.setImageResource(R.drawable.ic_baseline_repeat);
        } else {
            Config.shufferBoolean = false;
            id_shuffer.setImageResource(R.drawable.ic_baseline_shuffle);
        }
    }

    protected void menuAction() {
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(PlayerMusic.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.delete:
                                Toast.makeText(PlayerMusic.this, "Delete Clicked", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.download:
                                String name = Config.currentListSong.get(Config.songIndex).getTitle();
                                String link = Config.currentListSong.get(Config.songIndex).getLink();
                                DownloadFile.downloadByLink(name, link, PlayerMusic.this);
                                break;
                            case R.id.currentListSong:
                                showCurrentListSong();
                            default:
                                break;
                        }
                        return true;
                    }
                }));
            }
        });
    }

    private void showCurrentListSong() {
        for (MusicFiles musicFile : Config.currentListSong) {
            Log.d("listsong", "showCurrentListSong: " + musicFile.getTitle());
        }
    }
}
