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

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyViewHolder> {
    View view;
    private Context mContext;
    private ArrayList<MusicFiles> albumFiles;
    private int albumIndex;

    public AlbumDetailsAdapter(Context mContext, ArrayList<MusicFiles> albumFiles, int albumIndex) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
        this.albumIndex = albumIndex;
    }

    @NonNull
    @Override
    public AlbumDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlbumDetailsAdapter.MyViewHolder holder, final int position) {
        if (albumFiles.size() < 1) {
            holder.album_name.setText("No name");

            MusicAdapter.setImage(null, mContext, holder.album_image);
        } else {
            holder.album_name.setText(albumFiles.get(position).getTitle());
            byte[] image = MusicAdapter.getAlbumArt(albumFiles.get(position).getPath());

            MusicAdapter.setImage(image, mContext, holder.album_image);
        }

        /*holder.album_name.setText(albumFiles.get(position).getTitle());
        byte[] image = MusicAdapter.getAlbumArt(albumFiles.get(position).getPath());

        MusicAdapter.setImage(image, mContext, holder.album_image);*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("playAlbum", "myAlbum");
                intent.putExtra("songName", albumFiles.get(position).getTitle());
                intent.putExtra("albumIndex", albumIndex);
                intent.putExtra("songIndex", position);
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
            album_image = itemView.findViewById(R.id.music_img);
            album_name = itemView.findViewById(R.id.music_file_name);
        }
    }
}
