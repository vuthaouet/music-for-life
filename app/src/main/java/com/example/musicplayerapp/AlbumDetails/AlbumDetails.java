package com.example.musicplayerapp.AlbumDetails;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerapp.Adapter.AlbumDetailsAdapter;
import com.example.musicplayerapp.Config;
import com.example.musicplayerapp.Database.DatabaseHelper;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.MainActivity;
import com.example.musicplayerapp.R;

import java.util.List;

//import static com.example.musicplayerapp.MainActivity.albumFiles;

public class AlbumDetails extends Details {
    private List<MusicFiles> albumSongs;
    private DatabaseHelper databaseHelper = new DatabaseHelper(AlbumDetails.this);

    //int albumIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //albumIndex = getIntent().getIntExtra("albumIndex", -1);

        initView();

        //albumNameIntent = albumFiles.get(albumIndex).get(0).getAlbum();
        albumNameIntent = getIntent().getStringExtra("albumName").trim();
        albumName.setText(albumNameIntent);

        Log.d("sqlite", "onCreate: " + databaseHelper.getFilesFromAlbum(albumNameIntent).toString());

        albumSongs = databaseHelper.getFilesFromAlbum(albumNameIntent);

        /*albumSongs.addAll(albumFiles.get(albumIndex));
        albumSongs.remove(0);

        if (albumSongs.isEmpty()) {
            albumName.setText(albumNameIntent);
            MusicAdapter.setImage(null, this, albumPhoto);

            synchronized (this) {
                while (!getFromDatabase(albumSongs, albumNameIntent)) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            albumName.setText(albumNameIntent);

            byte[] image = MusicAdapter.getAlbumArt(albumSongs.get(0).getPath());
            MusicAdapter.setImage(image, this, albumPhoto);
        }*/

        backBtnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.setAction("toAlbumFragment");
                startActivity(intent);
            }
        });

        addAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Config.addToAlbumScreen = true;
                //intent.setAction("AddToAlbum");
                //intent[0].putExtra("albumIndex", albumIndex);
                intent.putExtra("albumNameAdded", albumNameIntent);
                startActivity(intent);
            }
        });

        showAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent[] intent = {new Intent(getApplicationContext(), MainActivity.class)};

                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.deleteAlbum:
                                intent[0].setAction("DeleteAlbum");
                                intent[0].putExtra("albumName", albumNameIntent);
                                startActivity(intent[0]);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                }));
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();

        Config.playOnline = false;
    }

    /*private boolean getFromDatabase(final ArrayList<MusicFiles> albumSongs, final String albumNameIntent) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        *//*albumSongs.addAll(databaseHelper.getFilesFromAlbum(albumNameIntent));*//*

        for (int i = 0; i < albumFiles.size(); i++) {
            if (albumFiles.get(i).get(0).getAlbum() == albumNameIntent) {
                albumFiles.get(i).addAll(albumSongs);
                break;
            }
        }
        return true;
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if (albumSongs.size() > 0) {
            albumDetailsAdapter = new AlbumDetailsAdapter(this, albumSongs, albumNameIntent);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
    }
}