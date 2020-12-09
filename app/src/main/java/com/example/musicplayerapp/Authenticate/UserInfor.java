package com.example.musicplayerapp.Authenticate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayerapp.Adapter.MusicAdapter;
import com.example.musicplayerapp.Config;
import com.example.musicplayerapp.Database.UploadFile;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.Format.Format;
import com.example.musicplayerapp.R;
import com.example.musicplayerapp.Random.RandomString;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfor extends AppCompatActivity {
    private TextView iEmail, iFullName, logOutText, favoriteText;
    private ImageView avatar, showFavoriteList;
    private String userId;
    //private SearchView searchView;
    private RecyclerView recyclerView_favorite;
    private MusicAdapter musicAdapter_favorite;

    private ArrayList<MusicAdapter> favoriteList = new ArrayList<>();

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting);

        initView();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();

        userId = firebaseUser.getUid();

        setImage(firebaseUser.getPhotoUrl(), this, avatar);

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(userId);
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
        });

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

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UploadFile.class));
            }
        });

        logOutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        favoriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        getFavoriteList(documentReference);
    }

    private void initView() {
        iEmail = findViewById(R.id.music_file_artist);
        iFullName = findViewById(R.id.user_name);

        avatar = findViewById(R.id.cover_art);

        logOutText = findViewById(R.id.log_out_text);
        favoriteText = findViewById(R.id.favorite_song_text);
        //searchView = findViewById(R.id.search_song);

        recyclerView_favorite = findViewById(R.id.recyclerView_favorite);

        //parentScrollView = (ScrollView) findViewById(R.id.parent_scrollView);
        //childScrollView = (ScrollView) findViewById(R.id.favorite_list);
        showFavoriteList = findViewById(R.id.show_favorite_list);
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
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    private void getFavoriteList(DocumentReference documentReference) {
        favoriteList.clear();

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                List<String> group = (List<String>) value.get("likes");

                //Log.d("firebase", "onEvent: " + group.toString());
                for (String idMusic : group) {
                    Task<DocumentSnapshot> musicFilesTask = firebaseFirestore.collection("Music").document(idMusic).get();
                    musicFilesTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                MusicFiles song = task.getResult().toObject(MusicFiles.class);
                                if (song != null) {
                                    Log.d("TAG", "onComplete: " + song.getTitle());
                                }
                            }
                        }
                    });
                }
            }
        });

        musicAdapter_favorite = new MusicAdapter(getApplicationContext(), (ArrayList<MusicFiles>) Config.currentListSong);
        recyclerView_favorite.setAdapter(musicAdapter_favorite);
        recyclerView_favorite.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
    }
}