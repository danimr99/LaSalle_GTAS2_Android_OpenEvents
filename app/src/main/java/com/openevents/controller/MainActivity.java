package com.openevents.controller;

import com.openevents.api.APIManager;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.constants.Constants;
import com.openevents.controller.fragments.SocialFragment;
import com.openevents.controller.fragments.HomeFragment;
import com.openevents.controller.fragments.MyEventsTabFragment;
import com.openevents.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.openevents.controller.fragments.UserFragment;
import com.openevents.api.responses.User;
import com.openevents.utils.SharedPrefs;

import android.annotation.SuppressLint;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    // Variables
    private APIManager apiManager;
    private SharedPrefs sharedPrefs;
    private Fragment fragment;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get instance of SharedPrefs
        this.sharedPrefs = SharedPrefs.getInstance(getApplicationContext());

        // Get an instance of APIManager and get the user from API
        this.apiManager = APIManager.getInstance();
        this.getUserLoggedIn();

        // Configure bottom navigation bar of the app
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_bar);
        bottomNav.setOnItemSelectedListener(view -> {
            switch (view.getItemId()) {
                case R.id.navBarHomeIcon:
                    this.fragment = new HomeFragment();
                    break;
                case R.id.navBarEventsIcon:
                    this.fragment = new MyEventsTabFragment();
                    break;
                case R.id.navBarFriendsIcon:
                    this.fragment = new SocialFragment();
                    break;
                case R.id.navBarUserIcon:
                    this.fragment = new UserFragment();
                    break;
            }

            // Change fragment to the one selected from the bottom navigation bar
            this.getSupportFragmentManager().beginTransaction().
                    replace(R.id.home_fragment_container, this.fragment).commit();
            return true;
        });

        // Set default fragment on enter to the app
        this.fragment = new HomeFragment();
        this.getSupportFragmentManager().beginTransaction().
                replace(R.id.home_fragment_container, this.fragment).commit();
    }


    private void getUserLoggedIn() {
        // Get user authentication token
        AuthenticationToken authenticationToken =
                new AuthenticationToken(this.sharedPrefs.getAuthenticationToken());

        // Get user's email address
        String email = this.sharedPrefs.getStringEntry(Constants.USER_EMAIL_SHARED_PREFERENCES);

        // Get user from API
        this.apiManager.getUserByEmail(authenticationToken.getAccessToken(), email,
                new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<User>> call, @NonNull Response<ArrayList<User>> response) {
                if (response.isSuccessful()) {
                    if(response.body() != null) {
                        // Get user from response
                        User user = response.body().get(0);

                        // Save user to SharedPreferences
                        sharedPrefs.saveUser(user);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<User>> call, @NonNull Throwable t) { }
        });
    }

}