package com.example.musicplayerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static com.example.musicplayerapp.PlayerActivity.listSongs;
import static com.example.musicplayerapp.PlayerActivity.mediaPlayer;
import static com.example.musicplayerapp.PlayerActivity.position;
import static com.example.musicplayerapp.PlayerActivity.uri;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static final int REQUEST_CODE = 1;
    static ArrayList<MusicFiles> musicFiles;

    static TextView song_name_main;
    static TextView song_artist_main;

    static ImageView cover_art_main;

    static ImageView play_pause_main;
    static ImageView id_next_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            musicFiles = getAllAudio(this);
            initViewPage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Do something
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                musicFiles = getAllAudio(this);
                initViewPage();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    private void initViewPage() {
        ViewPager viewPager = findViewById(R.id.viewpage);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragments(new SongsFragment(), "Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(), "Albums");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        if (mediaPlayer != null) {
            addPlayingSongLayout();
        }
    }

    private void addPlayingSongLayout() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
        getLayoutInflater().inflate(R.layout.player_running, relativeLayout);

        RelativeLayout playerRunningOnMain = findViewById(R.id.playerRunningOnMain);
        playerRunningOnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        song_name_main = findViewById(R.id.song_name_main);
        song_artist_main = findViewById(R.id.song_artist_main);

        cover_art_main = findViewById(R.id.cover_art_main);

        play_pause_main = findViewById(R.id.play_pause_main);
        id_next_main = findViewById(R.id.id_next_main);

        controlMusicPlayerFromMain(getApplicationContext());
    }

    public static void controlMusicPlayerFromMain(final Context context) {
        song_name_main.setText(musicFiles.get(position).getTitle());
        song_artist_main.setText(musicFiles.get(position).getArtist());

        if (mediaPlayer.isPlaying()) {
            play_pause_main.setImageResource(R.drawable.ic_baseline_pause);
        } else {
            play_pause_main.setImageResource(R.drawable.ic_baseline_play_arrow);
        }

        byte[] image = MusicAdapter.getAlbumArt(musicFiles.get(position).getPath());
        if (image != null) {
            Glide.with(context).asBitmap()
                    .load(image)
                    .into(cover_art_main);
        } else {
            Glide.with(context)
                    .load(R.drawable.pepe_the_frog)
                    .into(cover_art_main);
        }

        play_pause_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    CreateNotification.createNotification(context, R.drawable.ic_baseline_play_arrow, musicFiles.get(position));
                    play_pause_main.setImageResource(R.drawable.ic_baseline_play_arrow);
                } else {
                    mediaPlayer.start();
                    CreateNotification.createNotification(context, R.drawable.ic_baseline_pause, musicFiles.get(position));
                    play_pause_main.setImageResource(R.drawable.ic_baseline_pause);
                }

            }
        });

        id_next_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();

                position = (position + 1) % musicFiles.size();

                uri = Uri.parse(musicFiles.get(position).getPath());
                mediaPlayer = MediaPlayer.create(context, uri);

                CreateNotification.createNotification(context, R.drawable.ic_baseline_pause, musicFiles.get(position));
                mediaPlayer.start();

                controlMusicPlayerFromMain(context);
            }
        });
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public static ArrayList<MusicFiles> getAllAudio(Context context) {
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);

                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration);
                Log.d("musicfile", "Path: " + path + " Album: " + album);
                tempAudioList.add(musicFiles);
            }
            cursor.close();
        }

        return tempAudioList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_option);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        String userInput = s.toLowerCase();
        ArrayList<MusicFiles> myFiles = new ArrayList<>();
        for (MusicFiles song : musicFiles) {
            if (song.getConvertedTitle().toLowerCase().contains(userInput)) {
                myFiles.add(song);
            }
        }
        SongsFragment.musicAdapter.updateList(myFiles);
        return true;
    }
}