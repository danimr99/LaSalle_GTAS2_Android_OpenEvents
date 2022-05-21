package com.openevents.controller.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.openevents.R;
import com.openevents.api.responses.UserProfile;
import com.openevents.model.adapters.EventsAdapter;
import com.openevents.model.adapters.UsersAdapter;
import com.openevents.model.interfaces.OnListItemListener;

import java.util.ArrayList;


public class AllUsersFragment extends Fragment implements OnListItemListener {
    // Constants
    public static final String TAG_ALL_USERS = "ALL_USERS";

    // UI Components
    private EditText usersSearchBar;
    private RecyclerView allUsersRecyclerView;
    private RecyclerView.Adapter allUsersAdapter;

    // Variables
    private ArrayList<UserProfile> users;

    public AllUsersFragment() {
        this.users = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_users, container, false);

        // Get all components from view
        this.usersSearchBar = view.findViewById(R.id.users_search_bar);
        this.allUsersRecyclerView = view.findViewById(R.id.all_users_recycler_view);

        // Create UsersAdapter and pass it to the users recycler view
        this.allUsersAdapter = new UsersAdapter(this.users, AllUsersFragment.this,
                TAG_ALL_USERS);
        this.allUsersRecyclerView.setAdapter(this.allUsersAdapter);

        return view;
    }

    @Override
    public void onListItemClicked(int index) {

    }
}