package com.mtc.musicForLife.models.tag;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.mtc.musicForLife.R;
import com.mtc.musicForLife.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class TagAdapter extends RecyclerView.Adapter<TagAdapter.RecyclerViewHolder>{

    private List<String> data = new ArrayList<>();

    public TagAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tag_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        holder.tagName.setText(data.get(position));
        holder.tagName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openSearchPage(view.getContext(), String.valueOf(data.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        Button tagName;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            tagName = (Button) itemView.findViewById(R.id.tag_name);
        }
    }
}