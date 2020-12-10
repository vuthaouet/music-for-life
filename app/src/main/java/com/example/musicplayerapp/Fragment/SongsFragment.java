package com.example.musicplayerapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerapp.Adapter.MusicAdapter;
import com.example.musicplayerapp.Config;
import com.example.musicplayerapp.Database.DatabaseHelper;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.MainActivity;
import com.example.musicplayerapp.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.musicplayerapp.Adapter.MusicAdapter.songIsChecked;
import static com.example.musicplayerapp.MainActivity.musicFiles;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static MusicAdapter musicAdapter;
    RecyclerView recyclerView;
    private Button addToAlbum;
    private DatabaseHelper databaseHelper;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SongsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SongsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SongsFragment newInstance(String param1, String param2) {
        SongsFragment fragment = new SongsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        databaseHelper = new DatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        addToAlbum = view.findViewById(R.id.add_to_album);

        Config.playOnline = false;

        if (Config.addToAlbumScreen) {
            addToAlbum.setVisibility(View.VISIBLE);
            addToAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Config.addToAlbumScreen = false;
                    String albumName = getActivity().getIntent().getStringExtra("albumNameAdded");
                    Log.d("albumName", "onClick: " + albumName);
                    if (databaseHelper.addMany(albumName, addToAlbum())) {
                        Toast.makeText(getContext(), "Added to album " + albumName, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }

        recyclerView.setHasFixedSize(true);

        if (!(musicFiles.size() < 1)) {
            musicAdapter = new MusicAdapter(getContext(), musicFiles, null);
            recyclerView.setAdapter(musicAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }

    private List<MusicFiles> addToAlbum() {
        List<MusicFiles> listAddedSong = new ArrayList<>();

        Log.d("sqlite", "totalInAlbum: " + songIsChecked.size());
        for (int i = 0; i < songIsChecked.size(); i++) {
            int index = songIsChecked.get(i);
            listAddedSong.add(musicFiles.get(index));
        }

        songIsChecked.clear();
        return listAddedSong;
    }
}