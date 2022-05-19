package com.openevents.controller.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.controller.components.ImageSelectorFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment {
    private APIManager apiManager;
    private ImageSelectorFragment fragment;
    private CircleImageView profileImage;

    public UserFragment() {
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Create an instance of APIManager
        this.apiManager = APIManager.getInstance();

        // Create ImageSelectorFragment
        FragmentManager fm = this.getChildFragmentManager();
        this.fragment = (ImageSelectorFragment) fm.findFragmentById(R.id.fragment_container);

        // Inflate view with the ImageSelectorFragment
        if (this.fragment == null) {
            this.fragment = new ImageSelectorFragment();
            fm.beginTransaction().add(R.id.fragment_container, this.fragment).commit();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get profile image view once the fragment has been loaded
        View fragmentView = this.fragment.getView();
        this.profileImage = fragmentView != null ?
                fragmentView.findViewById(R.id.imageSelector) : null;
    }
}