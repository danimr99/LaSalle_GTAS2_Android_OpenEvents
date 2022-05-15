package com.openevents.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.openevents.R;
import com.openevents.constants.Constants;
import com.openevents.utils.SharedPrefs;

public class SplashActivity extends AppCompatActivity {
    private SharedPrefs sharedPrefs;
    private String authToken;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().post(() -> {
            this.sharedPrefs = SharedPrefs.getInstance(this);
            this.authToken = this.sharedPrefs.getStringEntry(Constants.AUTH_TOKEN);

            if(!this.authToken.isEmpty() ) {
                this.intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
               this.intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            this.finish();
        });
    }
}