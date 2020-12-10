package com.example.musicplayerapp;

import com.example.musicplayerapp.Entity.MusicFiles;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static boolean isRegisterOnline;
    public static boolean isRegisterOffline;

    public static boolean playOnline;

    public static boolean repeatBoolean;
    public static boolean shufferBoolean;

    public static List<MusicFiles> currentListSong;
    public static int songIndex;

    public static String currentIdSong;

    public static boolean addToAlbumScreen = false;

    public static List<MusicFiles> favoriteList;
    public static List<MusicFiles> uploadList;

    public static List<String> nameOfAlbums;

    public static List<MusicFiles> getFromAlbumOnline = new ArrayList<>();
}
