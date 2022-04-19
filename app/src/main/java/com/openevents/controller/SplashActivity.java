package com.openevents.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.openevents.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().post(() -> {
            Log.i("OpenEvents", "SplashScreen has been displayed!");

            /* TODO Check whether a user was already logged in or not (HomeActivity or LoginActivity)*/

            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
        });
    }
}