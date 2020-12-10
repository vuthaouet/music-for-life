package com.example.musicplayerapp.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicplayerapp.Adapter.MusicAdapter;
import com.example.musicplayerapp.Authenticate.Login;
import com.example.musicplayerapp.Config;
import com.example.musicplayerapp.Database.UploadFile;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.MainActivity;
import com.example.musicplayerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private TextView iEmail, iFullName, logOutText;
    private ImageView avatar, showFavoriteList, showUploadList;
    private String userId;

    private RecyclerView recyclerView_favorite;
    private MusicAdapter musicAdapter_favorite;
    private ScrollView favoriteScrollView;

    private RecyclerView recyclerView_upload;
    private MusicAdapter musicAdapter_upload;
    private ScrollView uploadScrollView;

    //public static ArrayList<MusicFiles> favoriteList = new ArrayList<>();
    //public static ArrayList<MusicFiles> uploadList = new ArrayList<>();

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    FirebaseUser firebaseUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();

        userId = firebaseUser.getUid();

        firebaseFirestore.collection("UserUpload")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("albumName", "onComplete: " + document.getId());
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_setting, container, false);
        initView(view);
        //setImage(firebaseUser.getPhotoUrl(), getContext(), avatar);


        /*documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                iEmail.setText(value.getString("email"));
                iFullName.setText(value.getString("fullName"));
            }
        });*/
        iFullName.setText(firebaseUser.getDisplayName());
        iEmail.setText(firebaseUser.getEmail());

        /*documentReference.update("likes", FieldValue.arrayRemove("lJqJDe5NbBScyTKdB3e1"));
        documentReference.update("likes", FieldValue.arrayUnion("lJqJDe5NbBScyTKdB3e1"));*/
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), UploadFile.class));
            }
        });

        logOutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        showFavoriteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favoriteScrollView.getVisibility() == View.GONE) {
                    favoriteScrollView.setVisibility(View.VISIBLE);
                    showFavoriteList.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                } else {
                    favoriteScrollView.setVisibility(View.GONE);
                    showFavoriteList.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right);
                }
            }
        });

        showUploadList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadScrollView.getVisibility() == View.GONE) {
                    uploadScrollView.setVisibility(View.VISIBLE);
                    showUploadList.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                } else {
                    uploadScrollView.setVisibility(View.GONE);
                    showUploadList.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right);
                }
            }
        });

        /*DocumentReference documentReference = firebaseFirestore.collection("Users").document(userId);

        documentReference.update("test", FieldValue.delete()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TAG", "Done");
            }
        });

        documentReference.update("new album", "");

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    Map<String, Object> map = task.getResult().getData();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        list.add(entry.getKey());
                        Log.d("TAG", entry.getKey());
                    }
                }
            }
        });*/

        getFavoriteList();
        getUploadList();

        return view;
    }

    private void initView(View view) {
        iEmail = view.findViewById(R.id.music_file_artist);
        iFullName = view.findViewById(R.id.user_name);

        avatar = view.findViewById(R.id.cover_art);

        logOutText = view.findViewById(R.id.log_out_text);
        //searchView = findViewById(R.id.search_song);

        recyclerView_favorite = view.findViewById(R.id.recyclerView_favorite);
        recyclerView_upload = view.findViewById(R.id.recyclerView_upload);

        //parentScrollView = (ScrollView) findViewById(R.id.parent_scrollView);
        //childScrollView = (ScrollView) findViewById(R.id.favorite_list);
        showFavoriteList = view.findViewById(R.id.show_favorite_list);
        showUploadList = view.findViewById(R.id.show_upload_list);

        favoriteScrollView = view.findViewById(R.id.favorite_list);
        uploadScrollView = view.findViewById(R.id.upload_list);

        Config.favoriteList = new ArrayList<>();
        Config.uploadList = new ArrayList<>();

        Config.playOnline = true;
    }

    private void setImage(Uri url, Context context, ImageView imageView) {
        if (url != null) {
            Glide.with(context).asBitmap()
                    .load(url.toString())
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(R.drawable.pepe_the_frog)
                    .into(imageView);
        }
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(), Login.class));
        getActivity().finish();
    }

    private void getFavoriteList() {
        Config.favoriteList.clear();

        firebaseFirestore.collection("Users")
                .document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<String> group = (List<String>) value.get("likes");
                        //Log.d("firebase", "onEvent: " + group.toString());
                        if (group != null) {
                            for (String idMusic : group) {
                                Task<DocumentSnapshot> musicFilesTask = firebaseFirestore.collection("Music").document(idMusic).get();
                                musicFilesTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            MusicFiles song = task.getResult().toObject(MusicFiles.class);
                                            if (song != null) {
                                                Log.d("favorite", "onComplete: " + song.getTitle());
                                                Config.favoriteList.add(song);
                                            }
                                            musicAdapter_favorite.updateList((ArrayList<MusicFiles>) Config.favoriteList);
                                        }
                                    }
                                });
                            }
                        }

                    }
                });

        musicAdapter_favorite = new MusicAdapter(getContext(), (ArrayList<MusicFiles>) Config.favoriteList, "FAVOR");
        recyclerView_favorite.setAdapter(musicAdapter_favorite);
        recyclerView_favorite.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    private void getUploadList() {
        Config.uploadList.clear();
        Log.d("email", "getUploadList: " + firebaseUser.getEmail());

        firebaseFirestore.collection("UserUpload")
                .document(firebaseUser.getEmail())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<String> group = (List<String>) value.get("songs");

                        if (group != null) {
                            for (String idMusic : group) {
                                Task<DocumentSnapshot> musicFilesTask = firebaseFirestore.collection("Music").document(idMusic).get();
                                musicFilesTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            MusicFiles song = task.getResult().toObject(MusicFiles.class);
                                            if (song != null) {
                                                Log.d("upload", "onComplete: " + song.getTitle());
                                                Config.uploadList.add(song);
                                            }
                                            musicAdapter_upload.updateList((ArrayList<MusicFiles>) Config.uploadList);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

        musicAdapter_upload = new MusicAdapter(getContext(), (ArrayList<MusicFiles>) Config.uploadList, "UPLOAD");
        recyclerView_upload.setAdapter(musicAdapter_upload);
        recyclerView_upload.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }
}