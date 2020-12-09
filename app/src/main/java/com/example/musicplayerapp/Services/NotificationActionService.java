package com.example.musicplayerapp.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.musicplayerapp.Config;

public class NotificationActionService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Config.playOnline) {
            context.sendBroadcast(new Intent("music_musicoff")
                    .putExtra("actionname", intent.getAction()));
            Log.d("broadcastReceiver", "onReceive: Offline " + intent.getAction());
        } else {
            context.sendBroadcast(new Intent("music_musiconl")
                    .putExtra("actionname", intent.getAction()));
            Log.d("broadcastReceiver", "onReceive: Online " + intent.getAction());
        }

        /*context.sendBroadcast(new Intent("music_music")
                .putExtra("actionname", intent.getAction()));
        Log.d("broadcastReceiver", "onReceive: Offline + Online " + intent.getAction());*/
    }
}
