package com.openevents.controller.fragments;

import android.os.Bundle;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.openevents.R;
import com.openevents.model.adapters.ViewPagerAdapter;

import java.util.ArrayList;

public class SocialTabFragment extends Fragment {
    // UI Components
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    // Variables
    private final ArrayList<Fragment> tabs;

    public SocialTabFragment() {
        this.tabs = new ArrayList<>();

        // Add tabs to list
        this.tabs.add(new AllUsersFragment());
        this.tabs.add(new MyFriendsFragment());
        this.tabs.add(new FriendRequestsFragment());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        // Get all components from view
        this.tabLayout = view.findViewById(R.id.social_tab);
        this.viewPager = view.findViewById(R.id.social_pager);

        // Configure view pager adapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPagerAdapter.setData(this.tabs);
        this.viewPager.setAdapter(viewPagerAdapter);

        return view;
    }

    @MainThread
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        new TabLayoutMediator(this.tabLayout, this.viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getText(R.string.allUsersLabel));
                    break;
                case 1:
                    tab.setText(getText(R.string.friendsLabel));
                    break;
                case 2:
                    tab.setText(getText(R.string.friendRequestsLabel));
                    break;
            }
        }).attach();
    }
}