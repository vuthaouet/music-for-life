package com.example.musicplayerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerapp.Entity.Comments;


import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private Context cContext;
    private ArrayList<Comments> comments;

    CommentAdapter(Context cContext, ArrayList<Comments> comments){
        this.cContext = cContext;
        this.comments = comments;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(cContext).inflate(R.layout.all_comments_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.uId.setText(comments.get(position).getUId());
        holder.content.setText(comments.get(position).getContent());
        holder.date.setText(comments.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView uId, content, date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            uId = itemView.findViewById(R.id.username);
            content = itemView.findViewById(R.id.comment_text);
            date = itemView.findViewById(R.id.comment_date);
        }
    }
}