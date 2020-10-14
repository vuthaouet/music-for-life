package com.example.musicplayerapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.musicplayerapp.MusicFiles;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String ALBUM_MANAGER = "ALBUM_MANAGER";
    private static final String ALBUM_NAME = "ALBUM_NAME";

    private static final String MUSIC_TITLE = "MUSIC_TITLE";
    private static final String MUSIC_ARTIST = "MUSIC_ARTIST";
    private static final String MUSIC_PATH = "MUSIC_PATH";
    private static final String MUSIC_DURATION = "MUSIC_DURATION";
    private static final String MUSIC_ALBUM = "MUSIC_ALBUM";

    private static final String ID = "ID";

    private static String ALBUM_TITLE;

    public DatabaseHelper(@Nullable Context context) {
        super(context, "musicPlayerApp.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableStatement = "CREATE TABLE " + ALBUM_MANAGER + " ( " +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ALBUM_NAME + " TEXT )";

        sqLiteDatabase.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<MusicFiles> createUserTable(String ALBUM_TITLE) {
        this.ALBUM_TITLE = ALBUM_TITLE;
        ArrayList<MusicFiles> newAlbumFile = new ArrayList<>();
        newAlbumFile.add(new MusicFiles(null, null, null, ALBUM_TITLE, null));

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String createTableStatement = "CREATE TABLE " + ALBUM_TITLE + " ( " +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MUSIC_PATH + " TEXT, " +
                MUSIC_TITLE + " TEXT, " +
                MUSIC_ARTIST + " TEXT, " +
                MUSIC_ALBUM + " TEXT, " +
                MUSIC_DURATION + " TEXT )";


        sqLiteDatabase.execSQL(createTableStatement);

        ContentValues contentValues = new ContentValues();

        contentValues.put(ALBUM_NAME, ALBUM_TITLE);
        sqLiteDatabase.insert(ALBUM_MANAGER, null, contentValues);

        sqLiteDatabase.close();

        return newAlbumFile;
    }

    public void deleteAlbum(String albumName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String deleteAlbumQuery = "DROP TABLE IF EXISTS " + albumName;
        sqLiteDatabase.execSQL(deleteAlbumQuery);

        String deleteFromTable = "DELETE FROM " + ALBUM_MANAGER + " WHERE " + ALBUM_NAME + " = '" + albumName + "';";
        sqLiteDatabase.execSQL(deleteFromTable);

        sqLiteDatabase.close();
    }

    public synchronized boolean addOne(String ALBUM_TITLE, MusicFiles musicFiles) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MUSIC_PATH, musicFiles.getPath());
        contentValues.put(MUSIC_TITLE, musicFiles.getTitle());
        contentValues.put(MUSIC_ARTIST, musicFiles.getArtist());
        contentValues.put(MUSIC_ALBUM, musicFiles.getAlbum());
        contentValues.put(MUSIC_DURATION, musicFiles.getDuration());

        long insert = sqLiteDatabase.insert(ALBUM_TITLE, null, contentValues);

        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public synchronized ArrayList<ArrayList<MusicFiles>> getAllAlbumFiles() {
        ArrayList<ArrayList<MusicFiles>> selfMadeAlbum = new ArrayList<>();

        String queryString = "SELECT * FROM " + ALBUM_MANAGER;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);

        while (cursor.moveToNext()) {
            String albumName = cursor.getString(1);

            ArrayList<MusicFiles> storeAlbumName = new ArrayList<>();
            storeAlbumName.add(new MusicFiles(null, null, null, albumName, null));
            selfMadeAlbum.add(storeAlbumName);
        }

        cursor.close();
        sqLiteDatabase.close();

        return selfMadeAlbum;
    }

    public ArrayList<MusicFiles> getFilesFromAlbum(String ALBUM_NAME) {
        ArrayList<MusicFiles> filesFromAlbum = new ArrayList<>();

        String queryString = "SELECT * FROM " + ALBUM_NAME;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);

        while (cursor.moveToNext()) {

            String path = cursor.getString(1);
            String title = cursor.getString(2);
            String album = cursor.getString(3);
            String artist = cursor.getString(4);
            String duration = cursor.getString(5);

            MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration);
            filesFromAlbum.add(musicFiles);
        }

        cursor.close();
        sqLiteDatabase.close();

        return filesFromAlbum;
    }
}
