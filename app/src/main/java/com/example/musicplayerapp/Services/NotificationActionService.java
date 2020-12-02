package com.example.musicplayerapp.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.example.musicplayerapp.MainActivity.playOnline;

public class NotificationActionService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!playOnline) {
            context.sendBroadcast(new Intent("music_musicoff")
                    .putExtra("actionname", intent.getAction()));
        } else {
            context.sendBroadcast(new Intent("music_musiconl")
                    .putExtra("actionnameonl", intent.getAction()));
        }
    }
}
