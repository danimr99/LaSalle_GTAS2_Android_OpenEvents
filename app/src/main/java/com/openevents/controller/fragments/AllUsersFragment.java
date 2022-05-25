package com.openevents.controller.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.UserProfile;
import com.openevents.model.adapters.UsersAdapter;
import com.openevents.model.interfaces.OnListUserListener;
import com.openevents.utils.Notification;
import com.openevents.utils.SharedPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AllUsersFragment extends Fragment implements OnListUserListener {
    // Constants
    public static final String TAG_ALL_USERS = "ALL_USERS";

    // UI Components
    private RecyclerView allUsersRecyclerView;
    private UsersAdapter allUsersAdapter;

    // Variables
    private ArrayList<UserProfile> users;
    private ArrayList<UserProfile> usersFiltered;
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;

    public AllUsersFragment() {
        this.users = new ArrayList<>();
        this.usersFiltered = new ArrayList<>();
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

        // Create an instance of APIManager and SharedPreferences
        this.apiManager = APIManager.getInstance();
        this.sharedPrefs = SharedPrefs.getInstance(view.getContext());

        // Get user authentication token
        this.authenticationToken =
                new AuthenticationToken(this.sharedPrefs.getAuthenticationToken());

        // Get users from API
        this.getUsers();

        // Get all components from view
        EditText searchBar = view.findViewById(R.id.users_search_bar);
        this.allUsersRecyclerView = view.findViewById(R.id.all_users_recycler_view);

        // Configure search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.length() == 0) {
                    usersFiltered = users;
                } else {
                    filter(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Configure horizontal layout for the events recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        this.allUsersRecyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.getUsers();
    }

    private void filter(String text) {
        ArrayList<UserProfile> filteredList = new ArrayList<>();

        for (UserProfile profile : this.users) {
            // Check for profile name
            if (profile.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(profile);
            }

            // Check for profile last name
            if(profile.getLastName().toLowerCase().contains(text.toLowerCase()) &&
                    !filteredList.contains(profile)) {
                filteredList.add(profile);
            }

            // Check for profile email
            if(profile.getEmail().toLowerCase().contains(text.toLowerCase()) &&
                    !filteredList.contains(profile)) {
                filteredList.add(profile);
            }
        }

        // Save list of filtered popular events
        this.usersFiltered = filteredList;

        // Update adapter
        allUsersAdapter.filter(this.usersFiltered);
    }

    private void getUsers() {
        // Get logged in user ID
        final int loggedInUserID = this.sharedPrefs.getUser().getId();

        this.apiManager.getUsers(this.authenticationToken.getAccessToken(),
                new Callback<ArrayList<UserProfile>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ArrayList<UserProfile>> call,
                                   @NonNull Response<ArrayList<UserProfile>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        // Get users from API
                        users = response.body();

                        // Remove logged in user from the list of users
                        users.removeIf(userProfile -> userProfile.getId() == loggedInUserID);
                        usersFiltered = users;

                        // Create UsersAdapter and pass it to the users recycler view
                        allUsersAdapter = new UsersAdapter(usersFiltered, AllUsersFragment.this,
                                TAG_ALL_USERS);
                        allUsersRecyclerView.setAdapter(allUsersAdapter);

                        // Update UI
                        allUsersAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<UserProfile>> call,
                                  @NonNull Throwable t) {
                Notification.showDialogNotification(getContext(),
                        getText(R.string.cannotConnectToServerError).toString());
            }
        });
    }

    @Override
    public void onUserClicked(int index) {
        requireActivity().getSupportFragmentManager().beginTransaction().
                add(R.id.home_fragment_container,
                        new UserProfileFragment(this.usersFiltered.get(index))).
                addToBackStack(this.getClass().getName()).
                commit();
    }
}