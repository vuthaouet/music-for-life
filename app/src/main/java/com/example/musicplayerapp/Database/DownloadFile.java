package com.example.musicplayerapp.Database;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.musicplayerapp.MainActivity;

public class DownloadFile {
    public static void downloadByLink(String name, String link, Context context) {
        String path = String.valueOf(Environment.getExternalStorageDirectory());
        downloadFile(context, name, ".mp3", path, link);
        Log.d("firebase", "downloadByLink: Done");
    }

    private static void downloadFile(Context context, String filename, String fileExt, String destination, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //request.setDestinationInExternalFilesDir(context, destination, filename + fileExt);
        request.setDestinationInExternalPublicDir(destination, filename + fileExt);
        downloadManager.enqueue(request);
    }
}
