package com.example.musicplayerapp.Services;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerapp.MusicFiles;
import com.example.musicplayerapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.io.IOException;

public class FirestoreAdapter extends FirestoreRecyclerAdapter<MusicFiles, FirestoreAdapter.ProductsViewHolder> {

    public FirestoreAdapter(@NonNull FirestoreRecyclerOptions<MusicFiles> options) {
        super(options);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductsViewHolder holder, int position, @NonNull final MusicFiles model) {
        holder.list_name.setText(model.getTitle());
        holder.list_artist.setText(model.getArtist());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    if (model.getLink() != null) {
                        mediaPlayer.setDataSource(model.getLink());
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });
                        mediaPlayer.prepare();
                    }
                    else {
                        Log.d("NOT_EXSITS", "Song not exist in database");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_items, parent, false);
        return new ProductsViewHolder(view);
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder{
        private TextView list_name;
        private TextView list_artist;
        private String link;
        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.music_file_name);
            list_artist = itemView.findViewById(R.id.music_file_artist);

        }
    }
}
