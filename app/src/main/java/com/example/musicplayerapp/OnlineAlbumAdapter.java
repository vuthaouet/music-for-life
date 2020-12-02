package com.example.musicplayerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class OnlineAlbumAdapter extends FirestoreRecyclerAdapter<Genre, OnlineAlbumAdapter.ProductsViewHolder> {

    public OnlineAlbumAdapter(@NonNull FirestoreRecyclerOptions<Genre> options) {
        super(options);
    }

    @Override
    public void onBindViewHolder(@NonNull final OnlineAlbumAdapter.ProductsViewHolder holder, int position, @NonNull final Genre model) {
        holder.name.setText(model.getName());
        Glide.with().load(model.getImage()).into(holder.image);
    }

    @NonNull
    @Override
    public OnlineAlbumAdapter.ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_list_items, parent, false);
        return new OnlineAlbumAdapter.ProductsViewHolder(view);
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView image;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.online_card_name);
            image = itemView.findViewById(R.id.online_album_image);
        }
    }
}
