package com.example.musicplayerapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerapp.AlbumDetails.AlbumDetails;
import com.example.musicplayerapp.R;

import java.util.ArrayList;
import java.util.List;

//import static com.example.musicplayerapp.MainActivity.albumFiles;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {
    View view;
    private List<String> mAlbumName;
    private Context mContext;

    public AlbumAdapter(Context mContext, List<String> albumFiles) {
        this.mContext = mContext;
        this.mAlbumName = albumFiles;
    }

    @NonNull
    @Override
    public AlbumAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.MyViewHolder holder, final int position) {
        //holder.album_name.setText(albumFiles.get(position).get(0).getAlbum());
        final String album_name = mAlbumName.get(position);
        holder.album_name.setText(album_name);

        /*if (albumFiles.get(position).size() <= 1) {
            MusicAdapter.setImage(null, mContext, holder.album_image);
        } else {
            byte[] image = MusicAdapter.getAlbumArt(albumFiles.get(position).get(1).getPath());
            MusicAdapter.setImage(image, mContext, holder.album_image);
        }*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AlbumDetails.class);
                //intent.putExtra("albumIndex", position);
                intent.putExtra("albumName", album_name);
                mContext.startActivity(intent);
            }
        });
    }

    public void updateList(List<String> albumName) {
        mAlbumName = new ArrayList<>();
        mAlbumName.addAll(albumName);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mAlbumName.size();
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
