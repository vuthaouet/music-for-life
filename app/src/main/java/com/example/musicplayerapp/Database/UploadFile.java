package com.example.musicplayerapp.Database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.musicplayerapp.Authenticate.Login;
import com.example.musicplayerapp.Authenticate.UserInfor;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.MainActivity;
import com.example.musicplayerapp.R;
import com.example.musicplayerapp.Random.RandomString;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UploadFile extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    private FirebaseUser firebaseUser;

    private EditText nameNewSong;
    private Button uploadBtn;
    private ProgressBar progressBarUploading;

    private static final int REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        progressBarUploading = findViewById(R.id.progressBarUpload);
        progressBarUploading.setVisibility(View.GONE);

        initView();
    }

    public void initView() {
        nameNewSong = findViewById(R.id.nameUpload);
        uploadBtn = findViewById(R.id.buttonUpload);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }

    private void showFileChooser() {
        if (!nameNewSong.getText().toString().isEmpty()) {
            Intent intent = new Intent();
            intent.setType("audio/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Select a song"), 1);

            progressBarUploading.setVisibility(View.VISIBLE);
        } else {
            nameNewSong.setError("Please enter a name !");
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data.getData() != null) {
                //the selected audio.
                Uri uri = data.getData();
                String songName = nameNewSong.getText().toString();
                updateContentResolver(uri, firebaseUser);
                uploadSong(uri, songName, firebaseUser);
            }
        }
    }

    private void updateContentResolver(Uri uri, FirebaseUser user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Media.ARTIST, user.getDisplayName());

        //grantUriPermission(aPackage + , uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        getContentResolver().update(uri, contentValues, null, null);
        Log.d("content", "updateContentResolver: Done");
    }

    private void uploadSong(Uri uri, final String songName, final FirebaseUser user) {
        final String fileName = songName + RandomString.getNumericString(3);

        final StorageReference newMusicFile = firebaseStorage.getReference().child(fileName + ".mp3");
        newMusicFile.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        newMusicFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                MusicFiles musicFiles = new MusicFiles(songName, user.getDisplayName(), uri.toString());
                                firebaseFirestore.collection("Music").add(musicFiles)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.update("id", documentReference.getId());
                                                Log.d("name", "onSuccess: " + firebaseUser.getDisplayName());

                                                Map<String, Object> upload = new HashMap<>();
                                                upload.put("songs", FieldValue.arrayUnion(documentReference.getId()));

                                                firebaseFirestore.collection("UserUpload")
                                                        .document(user.getEmail())
                                                        .set(upload, SetOptions.merge())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("uploadfile", "onSuccess: OK");
                                                                finish();
                                                                Toast.makeText(getApplicationContext(), "Upload file successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}