package com.example.musicplayerapp.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayerapp.Adapter.MusicAdapter;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.musicplayerapp.Database.SearchFiles.lookForSongsByArtist;
import static com.example.musicplayerapp.MainActivity.musicFiles;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchArtistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchArtistFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MusicAdapter artistAdapter;
    private RecyclerView recyclerView;

    public SearchArtistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchArtistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchArtistFragment newInstance(String param1, String param2) {
        SearchArtistFragment fragment = new SearchArtistFragment();
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
        View view = inflater.inflate(R.layout.fragment_search_artist, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        artistAdapter = new MusicAdapter(getContext(), lookForSongsByArtist, "SEARCH");
        recyclerView.setAdapter(artistAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        return view;
    }
}