package com.example.musicplayerapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.musicplayerapp.MainActivity.albumFiles;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {
    View view;
    private Context mContext;

    public AlbumAdapter(Context mContext, ArrayList<ArrayList<MusicFiles>> albumFiles) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AlbumAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.MyViewHolder holder, final int position) {
        holder.album_name.setText(albumFiles.get(position).get(0).getAlbum());

        if (albumFiles.get(position).size() <= 1) {
            MusicAdapter.setImage(null, mContext, holder.album_image);
        } else {
            byte[] image = MusicAdapter.getAlbumArt(albumFiles.get(position).get(1).getPath());
            MusicAdapter.setImage(image, mContext, holder.album_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AlbumDetails.class);
                intent.putExtra("albumIndex", position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView album_image;
        TextView album_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.album_image);
            album_name = itemView.findViewById(R.id.album_name);
        }
    }
}
