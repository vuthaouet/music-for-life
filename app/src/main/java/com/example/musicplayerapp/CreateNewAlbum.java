package com.example.musicplayerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayerapp.Format.Format;

public class CreateNewAlbum extends AppCompatActivity {
    EditText editTextAlbumName;
    Button buttonAlbumName;

    String newAlbumName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_album);

        editTextAlbumName = findViewById(R.id.editTextAlbumName);
        buttonAlbumName = findViewById(R.id.buttonAlbumName);

        buttonAlbumName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setAction("UpdateAlbum");

                newAlbumName = editTextAlbumName.getText().toString();
                intent.putExtra("createNewAlbum", Format.formatName(newAlbumName));
                startActivity(intent);
            }
        });
    }
}