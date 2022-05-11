package com.openevents.controller;

import com.openevents.EventsFragment;
import com.openevents.HomeFragment;
import com.openevents.R;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.openevents.SearchFragment;
import com.openevents.UserFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;


public class MainActivity extends AppCompatActivity {
    private Fragment fragment;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        this.fragment = new HomeFragment();
        bottomNav.setOnItemSelectedListener(view -> {

            switch (view.getItemId()) {
                case R.id.navBarHomeIcon:
                    fragment = new HomeFragment();
                    break;
                case R.id.navBarEventsIcon:
                    fragment = new EventsFragment();
                    break;
                case R.id.navBarSearchIcon:
                    fragment = new SearchFragment();
                    break;
                case R.id.navBarUserIcon:
                    fragment = new UserFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, this.fragment).commit();
    }
}