package com.example.musicplayerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.musicplayerapp.Adapter.ListAdapter;
import com.example.musicplayerapp.Adapter.MusicAdapter;
import com.example.musicplayerapp.Entity.MusicFiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CurrentListSong extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView backBtn;

    public static ListAdapter listAdapterCurrentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        initView();

        listAdapterCurrentSong = new ListAdapter(getApplicationContext(), (ArrayList<MusicFiles>) Config.currentListSong);

        recyclerView.setAdapter(listAdapterCurrentSong);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                if (position_dragged != Config.songIndex && position_target != Config.songIndex) {
                    Collections.swap(Config.currentListSong, position_dragged, position_target);
                    listAdapterCurrentSong.notifyItemMoved(position_dragged, position_target);
                }
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(recyclerView);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerViewList);
        backBtn = findViewById(R.id.back_btn);
    }
}