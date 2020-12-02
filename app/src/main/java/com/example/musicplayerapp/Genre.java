package com.example.musicplayerapp;

import android.widget.ImageView;

public class Genre {
    private String name;
    private ImageView image;

    public Genre(String name, ImageView image) {
        this.name = name;
        this.image = image;
    }

    public Genre() {
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
