package com.example.musicplayerapp.Authenticate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musicplayerapp.Database.UploadFile;
import com.example.musicplayerapp.Entity.MusicFiles;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfor extends AppCompatActivity {
    TextView iEmail, iFullName;
    ImageView avatar;
    String userId;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infor);

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

        documentReference.update("likes", FieldValue.arrayRemove("lJqJDe5NbBScyTKdB3e1"));
        documentReference.update("likes", FieldValue.arrayUnion("lJqJDe5NbBScyTKdB3e1"));

        documentReference.update("test", FieldValue.delete()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TAG", "Done");
            }
        });

        documentReference.update("new album", "");

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

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UploadFile.class));
            }
        });
    }

    private void initView() {
        iEmail = findViewById(R.id.emailInfor);
        iFullName = findViewById(R.id.fullNameInfor);

        avatar = findViewById(R.id.user_avatar);
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
}