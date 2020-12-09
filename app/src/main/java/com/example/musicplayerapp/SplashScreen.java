package com.example.musicplayerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView logo = findViewById(R.id.logo);
        ImageView logoText = findViewById(R.id.logoText);
        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo);
        Animation textAnim = AnimationUtils.loadAnimation(this, R.anim.logo_text);
        logo.startAnimation(logoAnim);
        logoText.startAnimation(textAnim);
        final Intent main = new Intent(this, MainActivity.class);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(main);
                    finish();
                }
            }
        };
        timer.start();
    }
}