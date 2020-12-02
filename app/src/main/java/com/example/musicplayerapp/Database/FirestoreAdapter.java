package com.example.musicplayerapp.Database;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.PlayerActivityOnline;
import com.example.musicplayerapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class FirestoreAdapter extends FirestoreRecyclerAdapter<MusicFiles, FirestoreAdapter.ProductsViewHolder> {
    private Context fContext;

    public FirestoreAdapter(@NonNull FirestoreRecyclerOptions<MusicFiles> options, Context context) {
        super(options);
        this.fContext = context;
    }

    static void setImage(String url, Context context, ImageView imageView) {
        if (url != null) {
            Glide.with(context).asBitmap()
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(R.drawable.pepe_the_frog)
                    .into(imageView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductsViewHolder holder, final int position, @NonNull final MusicFiles model) {
        if (!model.getImage().isEmpty()) {
            setImage(model.getImage(), fContext, holder.music_img);
        }
        holder.list_name.setText(model.getTitle());
        holder.list_artist.setText(model.getArtist());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fContext, PlayerActivityOnline.class);
                intent.putExtra("songIndexOnl", position);
                fContext.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_items, parent, false);
        return new ProductsViewHolder(view);
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder {
        private TextView list_name;
        private TextView list_artist;
        private ImageView music_img;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.music_file_name);
            list_artist = itemView.findViewById(R.id.music_file_artist);
            music_img = itemView.findViewById(R.id.music_img);
        }
    }
}
