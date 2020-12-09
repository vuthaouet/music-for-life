package com.example.musicplayerapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayerapp.Config;
import com.example.musicplayerapp.Database.DownloadFile;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.PlayMusic.PlayerActivity;
import com.example.musicplayerapp.PlayMusic.PlayerActivityOnline;
import com.example.musicplayerapp.PlayMusic.PlayerMusic;
import com.example.musicplayerapp.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.musicplayerapp.CurrentListSong.listAdapterCurrentSong;
//import static com.example.musicplayerapp.MainActivity.songIsChecked;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<MusicFiles> mFiles;

    public ListAdapter(Context mContext, ArrayList<MusicFiles> mFiles) {
        this.mContext = mContext;
        this.mFiles = mFiles;
    }

    /*public static byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);

        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();

        return art;
    }*/

    public static void setImage(byte[] image, Context context, ImageView imageView) {
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
    public ListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(mContext).inflate(R.layout.list_music_items, parent, false);
                break;
            case 1:
                view = LayoutInflater.from(mContext).inflate(R.layout.list_music_item_playing, parent, false);
                break;
            default:
                break;
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListAdapter.MyViewHolder holder, final int position) {
        Log.d("songIndex", "onBindViewHolder: " + position);

        holder.file_name.setText(mFiles.get(position).getTitle());
        holder.artist_name.setText(mFiles.get(position).getArtist());

        /*byte[] image = getAlbumArt(mFiles.get(position).getPath());
        setImage(image, mContext, holder.album_art);*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Config.playOnline) {
                    Intent intent = new Intent(mContext, PlayerActivity.class);
                    intent.putExtra("songName", mFiles.get(position).getTitle());
                    intent.putExtra("position", position);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, PlayerActivityOnline.class);
                    intent.putExtra("songIndexOnl", position);
                    intent.setAction("searchSongListener");
                    mContext.startActivity(intent);
                }
            }
        });

        if (holder.menuMoreFunction != null) {
            holder.menuMoreFunction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popupMenu = new PopupMenu(mContext, v);
                    popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener((new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.delete:
                                    if (Config.songIndex > position) {
                                        Config.songIndex -= 1;
                                    }
                                    Config.currentListSong.remove(position);
                                    listAdapterCurrentSong.notifyItemRemoved(position);
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
    }

    @Override
    public int getItemViewType(int position) {
        if (position == Config.songIndex) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public void updateList(ArrayList<MusicFiles> musicFilesArrayList) {
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }

    public void updateList() {
        notifyDataSetChanged();
        notify();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView file_name, artist_name;
        ImageView album_art, menuMoreFunction;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            artist_name = itemView.findViewById(R.id.music_file_artist);
            album_art = itemView.findViewById(R.id.music_img);
            menuMoreFunction = itemView.findViewById(R.id.dragAndDrop);
        }
    }
}
