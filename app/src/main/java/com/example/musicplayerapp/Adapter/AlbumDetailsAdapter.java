package com.example.musicplayerapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerapp.Config;
import com.example.musicplayerapp.Database.DatabaseHelper;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.PlayMusic.PlayerActivity;
import com.example.musicplayerapp.PlayMusic.PlayerActivityOnline;
import com.example.musicplayerapp.R;

import java.util.List;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyViewHolder> {
    private View view;
    private Context mContext;
    private List<MusicFiles> albumFiles;
    private DatabaseHelper databaseHelper;
    private String albumName;

    public AlbumDetailsAdapter(Context mContext, List<MusicFiles> albumFiles, String albumName) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
        this.albumName = albumName;

        this.databaseHelper = new DatabaseHelper(mContext);
    }

    @NonNull
    @Override
    public AlbumDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlbumDetailsAdapter.MyViewHolder holder, final int position) {
        /*if (albumFiles.size() < 1) {
            holder.album_name.setText("No name");
            MusicAdapter.setImage(null, mContext, holder.album_image);
        } else {
            holder.album_name.setText(albumFiles.get(position).getTitle());
            byte[] image = MusicAdapter.getAlbumArt(albumFiles.get(position).getPath());
            MusicAdapter.setImage(image, mContext, holder.album_image);
        }*/

        holder.album_name.setText(albumFiles.get(position).getTitle());
        /*byte[] image = MusicAdapter.getAlbumArt(albumFiles.get(position).getPath());

        MusicAdapter.setImage(image, mContext, holder.album_image);*/
        holder.menu_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.delete:
                                //Toast.makeText(mContext, "Delete Clicked", Toast.LENGTH_SHORT).show();
                                deleleFromAlbum(position);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                }));
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Config.playOnline) {
                    Intent intent = new Intent(mContext, PlayerActivityOnline.class);
                    intent.setAction("albumSongListener");
                    intent.putExtra("songIndexOnl", position);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, PlayerActivity.class);
                    //intent.putExtra("playAlbum", "myAlbum");
                    //intent.putExtra("songName", albumFiles.get(position).getTitle());
                    intent.setAction("playAlbum");
                    intent.putExtra("albumNamePlayed", albumName);
                    intent.putExtra("songIndexPlayed", position);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView album_image, menu_more;
        TextView album_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.music_img);
            album_name = itemView.findViewById(R.id.music_file_name);
            menu_more = itemView.findViewById(R.id.menuMore);
        }
    }

    private void deleleFromAlbum(int position) {
        databaseHelper.deleteSongFromAlbum(albumName, albumFiles.get(position).getTitle());
        notifyItemRemoved(position);
        notifyItemChanged(position, albumFiles.size());
        albumFiles.remove(position);
    }
}
