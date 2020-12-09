package com.example.musicplayerapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.musicplayerapp.Entity.MusicFiles;
import com.example.musicplayerapp.PlayMusic.PlayerActivity;
import com.example.musicplayerapp.PlayMusic.PlayerActivityOnline;
import com.example.musicplayerapp.R;
import com.example.musicplayerapp.Services.NotificationActionService;

//import static com.example.musicplayerapp.PlayMusic.PlayerActivity.position;
//import static com.example.musicplayerapp.PlayMusic.PlayerActivityOnline.Config.songIndex;

public class CreateNotification {
    public static final String CHANNEL_ID = "channel1";

    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_PLAY = "action_play_pause";
    public static final String ACTION_NEXT = "action_next";
    public static final String CLOSE_NOTIFICATION = "close_notification";

    public static Notification notification;

    public static void createNotification(Context context, int playPauseButton, MusicFiles musicFiles) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "music");

            PendingIntent pendingIntentPrevious;
            Intent intentPrevious = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PREVIOUS);
            pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent pendingIntentNext;
            Intent intentNext = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent pendingIntentPlayPause;
            Intent intentPlayPause = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PLAY);
            pendingIntentPlayPause = PendingIntent.getBroadcast(context, 0, intentPlayPause, PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent pendingPlayerActivity;

            Intent intentPlayerActivity;
            if (Config.playOnline) {
                intentPlayerActivity = new Intent(context, PlayerActivityOnline.class);
                intentPlayerActivity.putExtra("idSongOnl", musicFiles.getId());
            } else {
                intentPlayerActivity = new Intent(context, PlayerActivity.class);
                //intentPlayerActivity.putExtra("position", position);
                //intentPlayerActivity.putExtra("songNameOff", musicFiles.getTitle());
                intentPlayerActivity.putExtra("idSongOff", musicFiles.getId_off());
            }

            pendingPlayerActivity = PendingIntent.getActivity(context, 0, intentPlayerActivity, PendingIntent.FLAG_UPDATE_CURRENT);


            PendingIntent pendingCloseNotification;
            Intent intentCloseNotification = new Intent(context, NotificationActionService.class)
                    .setAction(CLOSE_NOTIFICATION);
            pendingCloseNotification = PendingIntent.getBroadcast(context, 0, intentCloseNotification, 0);

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.pepe_the_frog)
                    .setContentTitle(musicFiles.getTitle())
                    .setContentText(musicFiles.getArtist())
                    .setContentIntent(pendingPlayerActivity)
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .addAction(R.drawable.ic_baseline_skip_previous, "Previous", pendingIntentPrevious)
                    .addAction(playPauseButton, "Play", pendingIntentPlayPause)
                    .addAction(R.drawable.ic_baseline_skip_next, "Next", pendingIntentNext)
                    .addAction(R.drawable.ic_baseline_close, "Close", pendingCloseNotification)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            notificationManagerCompat.notify(1, notification);
        }
    }
}
