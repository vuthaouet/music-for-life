package com.example.musicplayerapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerapp.Adapter.AlbumAdapter;
import com.example.musicplayerapp.Adapter.MusicAdapter;
import com.example.musicplayerapp.R;


import static com.example.musicplayerapp.MainActivity.allNameAlbum;
import static com.example.musicplayerapp.MainActivity.musicFiles;

public class OnlineFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView recyclerViewAlbum;
    RecyclerView recyclerViewSong;
    static MusicAdapter musicAdapter;
    AlbumAdapter albumAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OnlineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlbumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnlineFragment newInstance(String param1, String param2) {
        OnlineFragment fragment = new OnlineFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online, container, false);
        recyclerViewAlbum = view.findViewById(R.id.recyclerViewAlbum);
        recyclerViewAlbum.setHasFixedSize(true);
        if (!(musicFiles.size() < 1)) {
            albumAdapter = new AlbumAdapter(getContext(), allNameAlbum);
            recyclerViewAlbum.setAdapter(albumAdapter);
            recyclerViewAlbum.setLayoutManager( new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
        recyclerViewSong = view.findViewById(R.id.recyclerViewSong);
        recyclerViewSong.setHasFixedSize(true);

        if (!(musicFiles.size() < 1)) {
            musicAdapter = new MusicAdapter(getContext(), musicFiles);
            recyclerViewSong.setAdapter(musicAdapter);
            recyclerViewSong.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}
