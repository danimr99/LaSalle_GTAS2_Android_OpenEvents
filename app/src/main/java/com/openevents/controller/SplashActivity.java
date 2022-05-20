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
    private String authenticationToken;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // Get an instance of SharedPreferences
            this.sharedPrefs = SharedPrefs.getInstance(this);

            // Get the authentication token
            this.authenticationToken = this.sharedPrefs.getAuthenticationToken();

            // Check if exists an authentication token
            if(!this.authenticationToken.isEmpty() ) {
                this.intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
               this.intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            // Navigate to the proper activity
            this.startActivity(intent);

            // Finish this activity to remove it from the stack of activities0
            this.finish();
        }, 2000);
    }
}