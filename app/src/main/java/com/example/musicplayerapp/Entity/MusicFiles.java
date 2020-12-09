package com.example.musicplayerapp.Entity;

public class MusicFiles {
    private String id;
    private String link;
    private String path;
    private String title;
    private String artist;
    private String album;
    private String duration;
    private String image = "";
    private String id_off;

    public MusicFiles(String path, String title, String artist, String album, String duration, String id_off) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.id_off = id_off;
    }

    public MusicFiles(String title, String artist, String link) {
        this.title = title;
        this.artist = artist;
        this.link = link;
    }

    /*public MusicFiles(String link, String path, String title, String artist, String album, String duration) {
        this.link = link;
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }*/

    public MusicFiles() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String images) {
        this.image = images;
    }

    public String getId_off() {
        return id_off;
    }

    public void setId_off(String id_off) {
        this.id_off = id_off;
    }
}
