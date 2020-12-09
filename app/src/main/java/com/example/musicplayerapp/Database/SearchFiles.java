package com.example.musicplayerapp.Database;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TableLayout;

import com.example.musicplayerapp.Adapter.ListAdapter;
import com.example.musicplayerapp.Adapter.ViewPagerAdapter;
import com.example.musicplayerapp.Config;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.Format.Format;
import com.example.musicplayerapp.Fragment.AlbumFragment;
import com.example.musicplayerapp.Fragment.SearchArtistFragment;
import com.example.musicplayerapp.Fragment.SearchPlaylistFragment;
import com.example.musicplayerapp.Fragment.SearchSongsFragment;
import com.example.musicplayerapp.Fragment.SongsFragment;
import com.example.musicplayerapp.MainActivity;
import com.example.musicplayerapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.musicplayerapp.Fragment.SearchSongsFragment.musicAdapterSearch;

//import static com.example.musicplayerapp.Fragment.SearchSongsFragment.listAdapter;

public class SearchFiles extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SearchView searchView;
    private ImageView backBtn;

    private FirebaseFirestore firebaseFirestore;

    public static ArrayList<MusicFiles> lookForSongsByTitle = new ArrayList<>();
    public static ArrayList<MusicFiles> lookForSongsByArtist = new ArrayList<>();
    public static ArrayList<MusicFiles> lookForSongByPlaylist = new ArrayList<>();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        initView();

        searchView.setOnQueryTextListener(this);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragments(new SearchSongsFragment(), "Song");
        viewPagerAdapter.addFragments(new SearchPlaylistFragment(), "Playlist");
        viewPagerAdapter.addFragments(new SearchArtistFragment(), "Artist");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void initView() {
        viewPager = findViewById(R.id.view_pager_search);
        tabLayout = findViewById(R.id.tab_layout_search);
        searchView = findViewById(R.id.search_text);

        backBtn = findViewById(R.id.back_btn);

        firebaseFirestore = FirebaseFirestore.getInstance();

        lookForSongsByTitle.clear();
        lookForSongByPlaylist.clear();
        lookForSongsByArtist.clear();

        Config.playOnline = true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("findMusic", "onQueryTextSubmit: OK");
        if (!query.isEmpty()) {
            findMusicSong(query.trim());
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("findMusic", "onQueryTextChange: Ok");
        /*findMusicSong(newText);
        musicAdapter.updateList(lookForSongsByTitle);*/
        return false;
    }

    private void findMusicSong(final String searchText) {
        Log.d("findMusic", "findMusicSong: " + searchText);
        lookForSongsByTitle.clear();

        Query query = firebaseFirestore.collection("Music");
        query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<MusicFiles> lookForSongs = value.toObjects(MusicFiles.class);
                for (MusicFiles musicFile : lookForSongs) {
                    if (Format.getConvertedTitle(musicFile.getTitle()).trim().toLowerCase().contains(searchText)) {
                        lookForSongsByTitle.add(musicFile);
                    }
                }
                musicAdapterSearch.updateList(lookForSongsByTitle);
            }
        });
    }
}