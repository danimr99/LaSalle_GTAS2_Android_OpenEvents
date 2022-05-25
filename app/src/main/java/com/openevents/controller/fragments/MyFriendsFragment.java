package com.openevents.controller.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.ActivityState;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.UserProfile;
import com.openevents.model.adapters.UsersAdapter;
import com.openevents.model.interfaces.OnListUserListener;
import com.openevents.utils.SharedPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFriendsFragment extends Fragment implements OnListUserListener, ActivityState {
    // Constants
    public static final String TAG_MY_FRIENDS = "MY_FRIENDS";

    // UI Components
    private RecyclerView friendsRecyclerView;
    private UsersAdapter friendsAdapter;
    private TextView friendStatusText;

    // Variables
    private ArrayList<UserProfile> friends;
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;

    public MyFriendsFragment() {
        this.friends = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_friends, container, false);

        // Get instance of SharedPrefs
        this.sharedPrefs = SharedPrefs.getInstance(getContext());

        // Get user authentication token
        this.authenticationToken =
                new AuthenticationToken(this.sharedPrefs.getAuthenticationToken());

        // Get an instance of APIManager and get friends from API
        this.apiManager = APIManager.getInstance();
        this.getFriends();

        // Get all components from view
        this.friendsRecyclerView = view.findViewById(R.id.my_friends_recycler_view);
        this.friendStatusText = view.findViewById(R.id.my_friends_status_text);

        // Set activity status to loading
        this.loading();

        // Configure horizontal layout for the events recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),
            LinearLayoutManager.VERTICAL, false);
        this.friendsRecyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.getFriends();
    }

    private void getFriends() {
        // Get logged in user ID
        final int loggedInUserID = this.sharedPrefs.getUser().getId();

        this.apiManager.getUserFriends(this.authenticationToken.getAccessToken(), loggedInUserID,
                new Callback<ArrayList<UserProfile>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ArrayList<UserProfile>> call,
                                   @NonNull Response<ArrayList<UserProfile>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        // Get friends from API
                        friends = response.body();

                        // Delete null user profiles (account deleted)
                        friends.removeIf(userProfile -> userProfile.getEmail() == null);

                        // Create UsersAdapter and pass it to the users recycler view
                        friendsAdapter = new UsersAdapter(friends, MyFriendsFragment.this,
                                TAG_MY_FRIENDS);
                        friendsRecyclerView.setAdapter(friendsAdapter);

                        // Update UI
                        friendsAdapter.notifyDataSetChanged();
                        onDataReceived();
                    } else {
                        // Set activity status to no data received
                        onNoDataReceived();
                    }
                } else {
                    // Set activity status to no data received
                    onNoDataReceived();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<UserProfile>> call,
                                  @NonNull Throwable t) {
                // Set activity status to connection failure
                onConnectionFailure();
            }
        });
    }

    @Override
    public void onUserClicked(int index) {
        requireActivity().getSupportFragmentManager().beginTransaction().
                add(R.id.home_fragment_container,
                        new UserProfileFragment(this.friends.get(index))).
                addToBackStack(this.getClass().getName()).
                commit();
    }

    @Override
    public void loading() {
        this.friendStatusText.setVisibility(View.VISIBLE);
        this.friendStatusText.setText(getText(R.string.loading));
        this.friendsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDataReceived() {
        if(this.friends.isEmpty()) {
            this.onNoDataReceived();
        } else {
            this.friendStatusText.setVisibility(View.GONE);
            this.friendsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNoDataReceived() {
        this.friendStatusText.setVisibility(View.VISIBLE);
        this.friendStatusText.setText(getText(R.string.noFriends));
        this.friendsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionFailure() {
        this.friendStatusText.setVisibility(View.VISIBLE);
        this.friendStatusText.setText(getText(R.string.serverConnectionFailed));
        this.friendsRecyclerView.setVisibility(View.GONE);
    }
}