package com.example.musicplayerapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.example.musicplayerapp.MainActivity.addToAlbumScreen;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<MusicFiles> mFiles;

    static boolean[] isCheckedList;

    MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles) {
        this.mContext = mContext;
        this.mFiles = mFiles;

        isCheckedList = new boolean[mFiles.size()];
    }

    static byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);

        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();

        return art;
    }

    static void setImage(byte[] image, Context context, ImageView imageView) {
        if (image != null) {
            Glide.with(context).asBitmap()
                    .load(image)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(R.drawable.pepe_the_frog)
                    .into(imageView);
        }
    }

    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MusicAdapter.MyViewHolder holder, final int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        holder.artist_name.setText(mFiles.get(position).getArtist());

        byte[] image = getAlbumArt(mFiles.get(position).getPath());
        setImage(image, mContext, holder.album_art);

        if (addToAlbumScreen) {
            holder.menuMore.setVisibility(View.INVISIBLE);
            holder.checkToAdd.setVisibility(View.VISIBLE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("albumTestPos", "onClick: " + Integer.toString(position));

                    if (holder.checkToAdd.isChecked()) {
                        isCheckedList[position] = false;
                        holder.checkToAdd.setChecked(false);
                    } else {
                        isCheckedList[position] = true;
                        holder.checkToAdd.setChecked(true);
                    }
                }
            });

        } else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PlayerActivity.class);
                    intent.putExtra("songName", mFiles.get(position).getTitle());
                    intent.putExtra("position", position);
                    mContext.startActivity(intent);
                }
            });

            holder.menuMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PopupMenu popupMenu = new PopupMenu(mContext, view);
                    popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener((new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.delete:
                                    Toast.makeText(mContext, "Delete Clicked", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    }));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    void updateList(ArrayList<MusicFiles> musicFilesArrayList) {
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView file_name, artist_name;
        ImageView album_art, menuMore;
        CheckBox checkToAdd;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            artist_name = itemView.findViewById(R.id.music_file_artist);
            album_art = itemView.findViewById(R.id.music_img);
            menuMore = itemView.findViewById(R.id.menuMore);
            checkToAdd = itemView.findViewById(R.id.checkToAdd);
        }
    }
}
