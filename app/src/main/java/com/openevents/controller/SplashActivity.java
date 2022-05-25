package com.openevents.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.openevents.R;
import com.openevents.utils.SharedPrefs;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    // Variables
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

            // Finish this activity to remove it from the stack of activities
            this.finish();
        }, 2000);
    }
}