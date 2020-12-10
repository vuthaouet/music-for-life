package com.example.musicplayerapp.AlbumDetails;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerapp.Adapter.AlbumDetailsAdapter;
import com.example.musicplayerapp.R;

public abstract class Details extends AppCompatActivity {
    protected RecyclerView recyclerView;
    protected TextView albumName;
    protected ImageView albumPhoto, backBtnAlbum, showAction, addAlbum;

    protected AlbumDetailsAdapter albumDetailsAdapter;

    protected String albumNameIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
    }

    protected void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        albumPhoto = findViewById(R.id.albumPhoto);
        albumName = findViewById(R.id.album_name_detail);
        backBtnAlbum = findViewById(R.id.back_btn_album);
        showAction = findViewById(R.id.menu_btn_album);
        addAlbum = findViewById(R.id.addAlbum);
    }
}
