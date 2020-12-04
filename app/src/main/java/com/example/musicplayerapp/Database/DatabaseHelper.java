package com.example.musicplayerapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.musicplayerapp.Entity.MusicFiles;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String ID = "ID";
    private static final String MUSIC_TITLE = "MUSIC_TITLE";
    private static final String MUSIC_ARTIST = "MUSIC_ARTIST";
    private static final String MUSIC_PATH = "MUSIC_PATH";
    private static final String MUSIC_DURATION = "MUSIC_DURATION";
    private static final String MUSIC_ALBUM = "MUSIC_ALBUM";
    private static final String ID_OFF = "ID_OFF";

    List<String> forbiddenName = new ArrayList<>();

    public DatabaseHelper(@Nullable Context context) {
        super(context, "musicPlayerApp.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean createUserTable(String ALBUM_TITLE) {
        /*ArrayList<MusicFiles> newAlbumFile = new ArrayList<>();
        newAlbumFile.add(new MusicFiles(null, null, null, ALBUM_TITLE, null));*/
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String createTableStatement = "CREATE TABLE " + ALBUM_TITLE + " ( " +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ID_OFF + " TEXT, " +
                MUSIC_PATH + " TEXT, " +
                MUSIC_TITLE + " TEXT, " +
                MUSIC_ARTIST + " TEXT, " +
                MUSIC_ALBUM + " TEXT, " +
                MUSIC_DURATION + " TEXT )";

        try {
            sqLiteDatabase.execSQL(createTableStatement);
            Log.d("sqlite", "createUserTable: True");
        } catch (SQLException exception) {
            Log.d("sqlite", "createUserTable: False");
        }
        sqLiteDatabase.close();
        return true;
    }

    public ArrayList<String> getAllTableName() {
        forbiddenName.add("sqlite_sequence");
        forbiddenName.add("android_metadata");

        ArrayList<String> arrTblNames = new ArrayList<String>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String name = c.getString(c.getColumnIndex("name"));
                if (!forbiddenName.contains(name.trim()))
                    arrTblNames.add(name);
                c.moveToNext();
            }
            Log.d("sqlite", "getAllTableName: Done");
        }

        sqLiteDatabase.close();
        return arrTblNames;
    }

    public boolean deleteAlbum(String albumName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String deleteAlbumQuery = "DROP TABLE IF EXISTS " + albumName;
        sqLiteDatabase.execSQL(deleteAlbumQuery);
        Log.d("sqlite", "deleteAlbum: Done");

        sqLiteDatabase.close();
        return true;
    }

    public boolean deleteSongFromAlbum(String albumName, String songName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "DELETE FROM " + albumName + " WHERE " + MUSIC_TITLE + " = \"" + songName + "\"";
        sqLiteDatabase.execSQL(query);
        Log.d("sqlite", "deleteSongFromAlbum: Done " + query);

        sqLiteDatabase.close();
        return true;
    }

    public boolean deleteSongFromAllAlbum(String songName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        for (String name : getAllTableName()) {
            if (!deleteSongFromAlbum(name, songName)) {
                return false;
            }
        }
        return true;
    }

    public boolean addOne(String ALBUM_TITLE, MusicFiles musicFiles) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MUSIC_PATH, musicFiles.getPath());
        contentValues.put(MUSIC_TITLE, musicFiles.getTitle());
        contentValues.put(MUSIC_ARTIST, musicFiles.getArtist());
        contentValues.put(MUSIC_ALBUM, musicFiles.getAlbum());
        contentValues.put(MUSIC_DURATION, musicFiles.getDuration());
        contentValues.put(ID_OFF, musicFiles.getId_off());

        long insert = sqLiteDatabase.insert(ALBUM_TITLE, null, contentValues);

        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addMany(String ALBUM_TITLE, List<MusicFiles> musicFilesList) {
        for (MusicFiles musicFiles : musicFilesList) {
            boolean success = this.addOne(ALBUM_TITLE, musicFiles);
            if (!success) {
                return false;
            }
        }
        Log.d("sqlite", "addMany: Done");
        return true;
    }

    /*public synchronized ArrayList<ArrayList<MusicFiles>> getAllAlbumFiles() {
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
    }*/

    public List<MusicFiles> getFilesFromAlbum(String ALBUM_NAME) {
        List<MusicFiles> filesFromAlbum = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + ALBUM_NAME;

        Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);

        while (cursor.moveToNext()) {
            String id_off = cursor.getString(1);
            String path = cursor.getString(2);
            String title = cursor.getString(3);
            String album = cursor.getString(4);
            String artist = cursor.getString(5);
            String duration = cursor.getString(6);

            MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration, id_off);
            filesFromAlbum.add(musicFiles);
        }

        cursor.close();
        sqLiteDatabase.close();
        Log.d("sqlite", "getFilesFromAlbum: Done");

        return filesFromAlbum;
    }
}
