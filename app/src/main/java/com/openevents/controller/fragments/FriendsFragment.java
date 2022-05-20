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
import com.openevents.ViewPager2Adapter;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        ViewPager2 viewPager2 = view.findViewById(R.id.friends_pager);

        ViewPager2Adapter viewPager2Adapter = new
                ViewPager2Adapter(this);
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new MyFriendsListFragment());
        fragmentList.add(new RequestsListFragment());
        fragmentList.add(new UsersListFragment());
        viewPager2Adapter.setData(fragmentList);
        viewPager2.setAdapter(viewPager2Adapter);

        return view;
    }

    @MainThread
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TabLayout tabLayout = view.findViewById(R.id.friends_tab_layout);
        ViewPager2 viewPager2 = view.findViewById(R.id.friends_pager);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Friends");
                    break;
                case 1:
                    tab.setText("Requests");
                    break;
                case 2:
                    tab.setText("All users");
                    break;
            }
        }).attach();
    }
}