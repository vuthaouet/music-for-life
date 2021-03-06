package com.example.musicplayerapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.musicplayerapp.Database.DatabaseHelper;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static com.example.musicplayerapp.MusicAdapter.isCheckedList;
import static com.example.musicplayerapp.PlayerActivity.listSongs;
import static com.example.musicplayerapp.PlayerActivity.mediaPlayer;
import static com.example.musicplayerapp.PlayerActivity.position;
import static com.example.musicplayerapp.PlayerActivity.tempSongName;
import static com.example.musicplayerapp.PlayerActivity.uri;
import static com.example.musicplayerapp.SongsFragment.musicAdapter;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static final int REQUEST_CODE = 1;

    static ArrayList<MusicFiles> musicFiles;
    public static ArrayList<ArrayList<MusicFiles>> albumFiles;

    static TextView song_name_main;
    static TextView song_artist_main;

    static ImageView cover_art_main;

    static ImageView play_pause_main;
    static ImageView id_next_main;

    static boolean addToAlbumScreen = false;

    private String MY_SORT_PREF = "SortOrder";

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private String albumName;

    DatabaseHelper databaseHelper;

    public static void controlMusicPlayerFromMain(final Context context) {
        song_name_main.setText(listSongs.get(position).getTitle());
        song_artist_main.setText(listSongs.get(position).getArtist());

        if (mediaPlayer.isPlaying()) {
            play_pause_main.setImageResource(R.drawable.ic_baseline_pause);
        } else {
            play_pause_main.setImageResource(R.drawable.ic_baseline_play_arrow);
        }

        byte[] image = MusicAdapter.getAlbumArt(listSongs.get(position).getPath());
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
                    CreateNotification.createNotification(context, R.drawable.ic_baseline_play_arrow, listSongs.get(position));
                    play_pause_main.setImageResource(R.drawable.ic_baseline_play_arrow);
                } else {
                    mediaPlayer.start();
                    CreateNotification.createNotification(context, R.drawable.ic_baseline_pause, listSongs.get(position));
                    play_pause_main.setImageResource(R.drawable.ic_baseline_pause);
                }

            }
        });

        id_next_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();

                position = (position + 1) % listSongs.size();

                uri = Uri.parse(listSongs.get(position).getPath());
                mediaPlayer = MediaPlayer.create(context, uri);

                CreateNotification.createNotification(context, R.drawable.ic_baseline_pause, listSongs.get(position));
                mediaPlayer.start();

                tempSongName = listSongs.get(position).getTitle();

                controlMusicPlayerFromMain(context);
            }
        });
    }

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
                initViewPage();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    private void initViewPage() {
        viewPager = findViewById(R.id.viewpage);
        tabLayout = findViewById(R.id.tab_layout);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragments(new OnlineFragment(), "Online");
        viewPagerAdapter.addFragments(new OfflineFragment(), "Offline");
        viewPagerAdapter.addFragments(new ProfileFragment(), "Cá nhân");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_music_song);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_album);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_profile);


        databaseHelper = new DatabaseHelper(MainActivity.this);

        musicFiles = getAllAudio(this);

        if (albumFiles == null) {
            albumFiles = categorizeByAlbum(musicFiles);
            albumFiles.addAll(databaseHelper.getAllAlbumFiles());
        } else {
            if (getIntent().getAction() != null) {
                switch (getIntent().getAction()) {
                    case "Update album":
                        albumFiles.add(databaseHelper.createUserTable(
                                getIntent().getStringExtra("createNewAlbum")));
                        break;
                    case "Delete album":
                        viewPager.setCurrentItem(1, false);

                        int index = getIntent().getIntExtra("albumIndex", -1);
                        databaseHelper.deleteAlbum(albumFiles.get(index).get(0).getAlbum());
                        albumFiles.remove(index);
                        break;
                    case "Add to album":
                        addToAlbumScreen = true;
                        albumName = getIntent().getStringExtra("albumName");
                        break;
                    case "toAlbumFragment":
                        viewPager.setCurrentItem(1, false);
                        break;
                }
            }
        }

        if (mediaPlayer != null) {
            addPlayingSongLayout();
        }
    }

    private void addPlayingSongLayout() {
        RelativeLayout relativeLayout = findViewById(R.id.main_layout);
        getLayoutInflater().inflate(R.layout.player_running, relativeLayout);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
        float dp = getApplicationContext().getResources().getDisplayMetrics().density;
        params.bottomMargin += (int) (60 * dp);

        RelativeLayout playerRunningOnMain = findViewById(R.id.playerRunningOnMain);
        playerRunningOnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("songName", tempSongName);
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

    public ArrayList<MusicFiles> getAllAudio(Context context) {
        SharedPreferences preferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting", "sortByName");

        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();

        String order = null;
        switch (sortOrder) {
            case "sortByName":
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;
            case "sortByDate":
                order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;
            case "sortBySize":
                order = MediaStore.MediaColumns.SIZE + " DESC";
                break;
        }

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, order);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);

                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration);
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
        musicAdapter.updateList(myFiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE).edit();
        Intent intent;

        switch (item.getItemId()) {
            case R.id.by_name:
                editor.putString("sorting", "sortByName");
                editor.apply();
                this.recreate();
                break;
            case R.id.by_date:
                editor.putString("sorting", "sortByDate");
                editor.apply();
                this.recreate();
                break;
            case R.id.by_size:
                editor.putString("sorting", "sortBySize");
                editor.apply();
                this.recreate();
                break;
            case R.id.create_album:
                intent = new Intent(getApplicationContext(), CreateNewAlbum.class);
                startActivity(intent);
                break;
            case R.id.addOrExit:
                addToAlbumScreen = false;

                synchronized (this) {
                    while (!addToAlbum(albumName)) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean addToAlbum(final String albumName) {
        Log.d("isCheckedList", "length: " + isCheckedList.length);

        for (int i = 0; i < isCheckedList.length; i++) {
            Log.d("isCheckedList", "addToAlbum: " + isCheckedList[i]);
            if (isCheckedList[i]) {
                synchronized (databaseHelper) {
                    while (!databaseHelper.addOne(albumName, musicFiles.get(i))) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return true;

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

    private ArrayList<ArrayList<MusicFiles>> categorizeByAlbum(final ArrayList<MusicFiles> musicFiles) {
        ArrayList<ArrayList<MusicFiles>> albumFiles = new ArrayList<>();
        for (int i = 0; i < musicFiles.size(); i++) {
            boolean checkAddMusic = false;
            for (int j = 0; j < albumFiles.size(); j++) {
                if (albumFiles.get(j).get(1).getAlbum().equals(musicFiles.get(i).getAlbum())) {
                    albumFiles.get(j).add(musicFiles.get(i));
                    checkAddMusic = true;
                    break;
                }
            }
            if (!checkAddMusic) {
                ArrayList<MusicFiles> newAlbum = new ArrayList<>();
                newAlbum.add(new MusicFiles(null, null, null, musicFiles.get(i).getAlbum(), null));
                newAlbum.add(musicFiles.get(i));
                albumFiles.add(newAlbum);
            }
        }
        return albumFiles;
    }
}