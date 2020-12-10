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
import com.example.musicplayerapp.AlbumDetails.AlbumDetailsOnline;
import com.example.musicplayerapp.Entity.HorizontalModel;
import com.example.musicplayerapp.R;

import java.util.ArrayList;

public class HorizontalRecycleViewAdapter extends RecyclerView.Adapter<HorizontalRecycleViewAdapter.HorizontalRVHolder> {

    Context context;
    ArrayList<HorizontalModel> arrayList;

    public HorizontalRecycleViewAdapter(Context context, ArrayList<HorizontalModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public HorizontalRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal, parent, false);
        return new HorizontalRVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalRVHolder holder, int position) {
        final HorizontalModel horizontalModel = arrayList.get(position);
        holder.title.setText(horizontalModel.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlbumDetailsOnline.class);
                intent.putExtra("albumContain", horizontalModel.getDescription());
                intent.putExtra("albumName", horizontalModel.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class HorizontalRVHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView imageView;

        public HorizontalRVHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleHorizontal);
            imageView = itemView.findViewById(R.id.img);
        }
    }

}
