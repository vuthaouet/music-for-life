package com.example.musicplayerapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerapp.Adapter.AlbumAdapter;
import com.example.musicplayerapp.Adapter.MusicAdapter;
import com.example.musicplayerapp.Adapter.VerticalRecycleViewAdapter;
import com.example.musicplayerapp.Config;
import com.example.musicplayerapp.Database.SearchFiles;
import com.example.musicplayerapp.Entity.HorizontalModel;
import com.example.musicplayerapp.Entity.VerticalModel;
import com.example.musicplayerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.musicplayerapp.MainActivity.allNameAlbum;
import static com.example.musicplayerapp.MainActivity.musicFiles;

public class OnlineFragment extends Fragment {

    private RecyclerView recyclerViewAlbum;
    private RecyclerView recyclerViewSong;
    private TextView findMusicActivity;

    static MusicAdapter musicAdapter;
    AlbumAdapter albumAdapter;

    VerticalRecycleViewAdapter adapter;
    ArrayList<VerticalModel> arrayList = new ArrayList<>();

    FirebaseFirestore firebaseFirestore;

    public OnlineFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Config.nameOfAlbums = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("OnlineMusic")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("nameAlbum", "onComplete: " + document.getId());
                                Config.nameOfAlbums.add(document.getId());
                            }
                            Config.nameOfAlbums.add("Upload");
                            setData(Config.nameOfAlbums);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online, container, false);

        findMusicActivity = view.findViewById(R.id.search_option);
        findMusicActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchFiles.class));
            }
        });

        recyclerViewAlbum = view.findViewById(R.id.recyclerView);
        recyclerViewAlbum.setHasFixedSize(true);

        recyclerViewAlbum.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new VerticalRecycleViewAdapter(getContext(), arrayList);
        recyclerViewAlbum.setAdapter(adapter);

        return view;
    }

    private void setData(List<String> nameOfAlbums) {
        for (final String name : nameOfAlbums) {
            final VerticalModel verticalModel = new VerticalModel();
            if (!name.equals("Upload")) {
                Log.d("nameAlbum", "setData: " + name);
                //final VerticalModel verticalModel = new VerticalModel();
                verticalModel.setTitle(name);
                final ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();

                firebaseFirestore.collection("OnlineMusic")
                        .document(name)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Map<String, Object> map = task.getResult().getData();
                                    for (Map.Entry<String, Object> entry : map.entrySet()) {

                                        HorizontalModel horizontalModel = new HorizontalModel();
                                        horizontalModel.setDescription(name);
                                        horizontalModel.setName(entry.getKey());
                                        arrayListHorizontal.add(horizontalModel);
                                    }

                                    verticalModel.setArrayList(arrayListHorizontal);
                                    arrayList.add(verticalModel);

                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
            } else {
                verticalModel.setTitle(name);
                final ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();

                firebaseFirestore.collection("UserUpload")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    HorizontalModel horizontalModel = new HorizontalModel();
                                    horizontalModel.setDescription(name);
                                    horizontalModel.setName(documentSnapshot.getString("name"));
                                    arrayListHorizontal.add(horizontalModel);
                                    //Log.d("documentSnapshot", "onComplete: " + documentSnapshot.getString("name"));
                                }

                                verticalModel.setArrayList(arrayListHorizontal);
                                arrayList.add(verticalModel);

                                adapter.notifyDataSetChanged();
                            }
                        });
            }
        }

        adapter.notifyDataSetChanged();
    }
}
