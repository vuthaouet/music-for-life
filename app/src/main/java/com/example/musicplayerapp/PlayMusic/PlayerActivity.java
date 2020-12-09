package com.example.musicplayerapp.PlayMusic;

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
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayerapp.Config;
import com.example.musicplayerapp.Database.DatabaseHelper;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.MainActivity;
import com.example.musicplayerapp.R;
import com.example.musicplayerapp.CreateNotification;
import com.example.musicplayerapp.Random.RandomString;
import com.example.musicplayerapp.Services.OnClearFromRecentService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.musicplayerapp.CurrentListSong.listAdapterCurrentSong;
import static com.example.musicplayerapp.MainActivity.musicFiles;

public class PlayerActivity extends PlayerMusic implements MediaPlayer.OnCompletionListener {

    //public static int position = -1;
    //public static List<MusicFiles> listSongs = new ArrayList<>();

    //public static String tempSongName;
    public static Uri uri;

    static ObjectAnimator anim;

    private DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();

        Config.playOnline = false;

        continuePlayingMusic();

        //getIntentMethod();

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

        menuAction();

        showNotification();
        backToMain();
    }

    protected void initViews() {
        super.initViews();

        relativeLayout = findViewById(R.id.comment_zone);
        relativeLayout.setVisibility(View.GONE);
    }

    private void showNotification() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                CreateNotification.createNotification(getBaseContext(), R.drawable.ic_baseline_pause, Config.currentListSong.get(Config.songIndex));
            } else {
                CreateNotification.createNotification(getBaseContext(), R.drawable.ic_baseline_play_arrow, Config.currentListSong.get(Config.songIndex));
            }
        } else {
            CreateNotification.createNotification(getBaseContext(), R.drawable.ic_baseline_pause, Config.currentListSong.get(Config.songIndex));
        }
    }

    protected void continuePlayingMusic() {
        String currentSong = getIntent().getStringExtra("idSongOff");
        if (currentSong != null) {
            if (currentSong.equals(Config.currentIdSong)) {
                if (mediaPlayer != null) {
                    uri = Uri.parse(Config.currentListSong.get(Config.songIndex).getPath());
                    metaData(uri);
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

    private void backToMain() {
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //intent.putExtra("songName", tempSongName);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("back_btn", "onBackPressed");
    }

    protected void getIntentMethod() {
        if (getIntent().getAction() != null) {
            switch (getIntent().getAction()) {
                case "playAlbum":
                    String albumName = getIntent().getStringExtra("albumNamePlayed");
                    int index = getIntent().getIntExtra("songIndexPlayed", -1);
                    Config.currentListSong = databaseHelper.getFilesFromAlbum(albumName);
                    Config.songIndex = index;
                    break;
                default:
                    break;
            }
            //listSongs.clear();

            //position = getIntent().getIntExtra("songIndex", -1);
            //listSongs.addAll(albumFiles.get(getIntent().getIntExtra("albumIndex", -1)));
            //listSongs.remove(0);
        } else {
            Config.songIndex = getIntent().getIntExtra("position", -1);
            Config.currentListSong = musicFiles;
        }

        Config.currentIdSong = Config.currentListSong.get(Config.songIndex).getId_off();

        if (Config.currentListSong != null) {
            play_pause.setImageResource(R.drawable.ic_baseline_pause);
            Log.d("uri", "getIntentMethod: " + Config.currentListSong.size() + " " + Config.songIndex);

            uri = Uri.parse(Config.currentListSong.get(Config.songIndex).getPath());
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            /*mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    readyToListen = true;
                }
            });*/
        } else {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            /*mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    readyToListen = true;
                }
            });*/
        }

        setRepeatState(false);
        setShufferState(false);

        metaData(uri);
    }

    private void metaData(Uri uri) {
        song_name.setText(Config.currentListSong.get(Config.songIndex).getTitle());
        song_artist.setText(Config.currentListSong.get(Config.songIndex).getArtist());
        showNotification();
        mediaPlayer.setOnCompletionListener(this);

        /*MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());

        byte[] art = retriever.getEmbeddedPicture();*/
        //MusicAdapter.setImage(MusicAdapter.getAlbumArt(uri.toString()), getApplicationContext(), cover_art);

        int totalTime = mediaPlayer.getDuration() / 1000;

        seekBar.setMax(totalTime);
        durationTotal.setText(formatTime(totalTime));

        Config.currentIdSong = Config.currentListSong.get(Config.songIndex).getId_off();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiverOff, new IntentFilter("music_musicoff"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }*/
    }

    @Override
    protected void addMyFavoriteSong() {

    }

    protected void id_prevClicked() {
        mediaPlayer.stop();
        mediaPlayer.release();

        if (!Config.repeatBoolean) {
            Config.songIndex = (Config.currentListSong.size() + Config.songIndex - 1) % Config.currentListSong.size();
        }

        if (Config.shufferBoolean) {
            Config.songIndex = RandomString.getNumber(Config.currentListSong.size(), Config.songIndex);
        }

        uri = Uri.parse(Config.currentListSong.get(Config.songIndex).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        runOnUiThread();

        play_pause.setImageResource(R.drawable.ic_baseline_pause);

        /*mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                readyToListen = true;
            }
        });*/
        mediaPlayer.start();

        /*if (song_artist_main != null && song_name_main != null) {
            controlMusicPlayerFromMain(getApplicationContext());
        }*/

        metaData(uri);
        //anim.cancel();
        //anim.start();
    }

    protected void play_pauseClicked() {
        if (mediaPlayer.isPlaying()) {
            CreateNotification.createNotification(getBaseContext(), R.drawable.ic_baseline_play_arrow, Config.currentListSong.get(Config.songIndex));
            /*if (play_pause_main != null) {
                play_pause_main.setImageResource(R.drawable.ic_baseline_play_arrow);
            }*/
            play_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
            mediaPlayer.pause();

            runOnUiThread();
            //anim.pause();
        } else {
            CreateNotification.createNotification(getBaseContext(), R.drawable.ic_baseline_pause, Config.currentListSong.get(Config.songIndex));
            /*if (play_pause_main != null) {
                play_pause_main.setImageResource(R.drawable.ic_baseline_pause);
            }*/
            play_pause.setImageResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();

            runOnUiThread();
            //anim.resume();
        }
    }

    protected void id_nextClicked() {
        mediaPlayer.stop();
        mediaPlayer.release();

        if (!Config.repeatBoolean) {
            Config.songIndex = (Config.songIndex + 1) % Config.currentListSong.size();
        }

        if (Config.shufferBoolean) {
            Config.songIndex = RandomString.getNumber(Config.currentListSong.size(), Config.songIndex);
        }

        Log.d("position", "id_nextClicked: " + Config.songIndex);

        uri = Uri.parse(Config.currentListSong.get(Config.songIndex).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        runOnUiThread();

        play_pause.setImageResource(R.drawable.ic_baseline_pause);
        CreateNotification.createNotification(getBaseContext(), R.drawable.ic_baseline_pause, Config.currentListSong.get(Config.songIndex));
        /*mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                readyToListen = true;
            }
        });*/
        mediaPlayer.start();

        /*if (song_artist_main != null && song_name_main != null) {
            controlMusicPlayerFromMain(getApplicationContext());
        }*/
        cover_art.clearAnimation();

        metaData(uri);
        //anim.cancel();
        //anim.start();
        listAdapterCurrentSong.notifyDataSetChanged();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        id_nextClicked();
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