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
import com.example.musicplayerapp.Adapter.VerticalRecycleViewAdapter;
import com.example.musicplayerapp.Entity.HorizontalModel;
import com.example.musicplayerapp.Entity.VerticalModel;
import com.example.musicplayerapp.R;


import java.util.ArrayList;

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

    VerticalRecycleViewAdapter adapter;
    ArrayList<VerticalModel> arrayList = new ArrayList<>();

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


        setData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online, container, false);
        recyclerViewAlbum = view.findViewById(R.id.recyclerView);
        recyclerViewAlbum.setHasFixedSize(true);

        recyclerViewAlbum.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new VerticalRecycleViewAdapter(getContext(), arrayList);

        recyclerViewAlbum.setAdapter(adapter);


        return view;
    }

    private void setData() {

        for (int i = 0; i < 5; i++) {
            VerticalModel verticalModel = new VerticalModel();
            verticalModel.setTitle("title:" + i);
            ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();
            for (int j=0; j<5; j++){
                HorizontalModel horizontalModel = new HorizontalModel();
                horizontalModel.setDescription("bucac" + j);
                horizontalModel.setName("Name:" + j);
                arrayListHorizontal.add(horizontalModel);
            }

            verticalModel.setArrayList(arrayListHorizontal);
            arrayList.add(verticalModel);
        }
    }
}
