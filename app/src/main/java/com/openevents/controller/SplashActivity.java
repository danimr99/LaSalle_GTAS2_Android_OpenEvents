package com.openevents.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.openevents.R;
import com.openevents.constants.Constants;
import com.openevents.utils.SharedPrefs;

public class SplashActivity extends AppCompatActivity {
    private SharedPrefs sharedPrefs;
    private String userEmail;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().post(() -> {
            this.sharedPrefs = SharedPrefs.getInstance(this);
            this.userEmail = this.sharedPrefs.getStringEntry(Constants.USER_EMAIL);
            this.userPassword = this.sharedPrefs.getStringEntry(Constants.USER_PASSWORD);

            if(!this.userEmail.isEmpty() && !this.userPassword.isEmpty()) {
                // TODO Try to retrieve API TOKEN from the email and password stored on SharedPreferences
                // TODO Depending on API TOKEN, intent to LoginActivity or HomeActivity
            } else {
                // TODO Intent to LoginActivity
                // Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
        });
    }
}