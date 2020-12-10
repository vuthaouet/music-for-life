package com.example.musicplayerapp.AlbumDetails;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerapp.Adapter.AlbumDetailsAdapter;
import com.example.musicplayerapp.Config;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailsOnline extends Details {
    FirebaseFirestore firebaseFirestore;
    String albumContain;
    //public static List<MusicFiles> musicFilesList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        albumNameIntent = getIntent().getStringExtra("albumName").trim();
        albumContain = getIntent().getStringExtra("albumContain").trim();

        Log.d("albumDetail", "onCreate: " + albumContain);

        albumName.setText(albumNameIntent);

        getSongFromAlbumOnline();

        albumDetailsAdapter = new AlbumDetailsAdapter(AlbumDetailsOnline.this, Config.getFromAlbumOnline, albumNameIntent);
        recyclerView.setAdapter(albumDetailsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(AlbumDetailsOnline.this, RecyclerView.VERTICAL, false));
    }

    private void getSongFromAlbumOnline() {
        Config.getFromAlbumOnline.clear();

        if (!albumContain.equals("Upload")) {
            Log.d("albumDetail", "getSongFromAlbumOnline: Running");
            firebaseFirestore.collection("OnlineMusic")
                    .document(albumContain)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            Log.d("albumDetail", "getSongFromAlbumOnline: Before");
                            List<String> group = (List<String>) value.get(albumNameIntent);
                            if (!group.isEmpty()) {
                                for (String idMusic : group) {
                                    Log.d("albumDetail", "onEvent: " + idMusic.trim());
                                    if (idMusic.isEmpty()) {
                                        return;
                                    }
                                    Task<DocumentSnapshot> musicFilesTask = firebaseFirestore.collection("Music").document(idMusic.trim()).get();
                                    musicFilesTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                MusicFiles song = task.getResult().toObject(MusicFiles.class);
                                                if (song != null) {
                                                    Log.d("albumDetail", "onComplete: " + song.getTitle());
                                                    //Config.getFromAlbumOnline.add(song);
                                                    Config.getFromAlbumOnline.add(song);
                                                }
                                                albumDetailsAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
        } else {
            firebaseFirestore.collection("UserUpload")
                    .document("lopxe@gmail.com")
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            Log.d("albumDetail", "getSongFromAlbumOnline: Before");
                            List<String> group = (List<String>) value.get("songs");
                            if (!group.isEmpty()) {
                                for (String idMusic : group) {
                                    Log.d("albumDetail", "onEvent: " + idMusic.trim());
                                    if (idMusic.isEmpty()) {
                                        return;
                                    }
                                    Task<DocumentSnapshot> musicFilesTask = firebaseFirestore.collection("Music").document(idMusic.trim()).get();
                                    musicFilesTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                MusicFiles song = task.getResult().toObject(MusicFiles.class);
                                                if (song != null) {
                                                    Log.d("albumDetail", "onComplete: " + song.getTitle());
                                                    //Config.getFromAlbumOnline.add(song);
                                                    Config.getFromAlbumOnline.add(song);
                                                }
                                                albumDetailsAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
        }
    }

    @Override
    protected void initView() {
        super.initView();

        firebaseFirestore = FirebaseFirestore.getInstance();

        Config.playOnline = true;
        //Config.getFromAlbumOnline.clear();
    }
}
