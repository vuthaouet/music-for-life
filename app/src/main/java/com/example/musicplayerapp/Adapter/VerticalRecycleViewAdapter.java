package com.example.musicplayerapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerapp.Entity.HorizontalModel;
import com.example.musicplayerapp.Entity.VerticalModel;
import com.example.musicplayerapp.R;

import java.util.ArrayList;

public class VerticalRecycleViewAdapter extends RecyclerView.Adapter<VerticalRecycleViewAdapter.VerticalRVHolder> {
    Context context;
    ArrayList<VerticalModel> arrayList;

    public VerticalRecycleViewAdapter(Context context, ArrayList<VerticalModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public VerticalRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vertical,parent,false);
        return new VerticalRVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalRVHolder holder, int position) {
        final VerticalModel verticalModel = arrayList.get(position);
        String title = verticalModel.getTitle();
        ArrayList<HorizontalModel> singleItem = verticalModel.getArrayList();

        holder.textView.setText(title);
        HorizontalRecycleViewAdapter horizontalRecycleViewAdapter = new HorizontalRecycleViewAdapter(context,singleItem);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false));
        holder.recyclerView.setAdapter(horizontalRecycleViewAdapter);
        /*holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,verticalModel.getTitle(),Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class VerticalRVHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView textView;
        //Button btnMore;

        public VerticalRVHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView1);
            textView = itemView.findViewById(R.id.title1);
            //btnMore = itemView.findViewById(R.id.btn);
        }
    }
}
