package com.example.musicplayerapp.Services;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerapp.Genre;
import com.example.musicplayerapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class FirestoreAdapter extends FirestoreRecyclerAdapter<Genre, FirestoreAdapter.ProductsViewHolder> {

    public FirestoreAdapter(@NonNull FirestoreRecyclerOptions<Genre> options) {
        super(options);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductsViewHolder holder, int position, @NonNull final Genre model) {
        holder.name.setText(model.getName() + " >");
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_album_list, parent, false);
        return new ProductsViewHolder(view);
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder{
        private TextView name;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.online_album_name);

        }
    }
}
