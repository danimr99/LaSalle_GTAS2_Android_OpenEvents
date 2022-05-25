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
import com.openevents.utils.Notification;
import com.openevents.utils.SharedPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendRequestsFragment extends Fragment implements OnListUserListener, ActivityState {
    // Constants
    public static final String TAG_FRIEND_REQUESTS = "FRIEND_REQUESTS";

    // UI Components
    private RecyclerView friendRequestsRecyclerView;
    private UsersAdapter friendRequestsAdapter;
    private TextView friendRequestsStatusText;

    // Variables
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;
    private ArrayList<UserProfile> friendRequests;

    public FriendRequestsFragment() {
        this.friendRequests = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);

        // Get instance of SharedPrefs
        // Variables
        SharedPrefs sharedPrefs = SharedPrefs.getInstance(getContext());

        // Get user authentication token
        this.authenticationToken =
                new AuthenticationToken(sharedPrefs.getAuthenticationToken());

        // Get an instance of APIManager and get friends from API
        this.apiManager = APIManager.getInstance();
        this.getFriendRequests();

        // Get all components from view
        this.friendRequestsRecyclerView = view.findViewById(R.id.friend_requests_recycler_view);
        this.friendRequestsStatusText = view.findViewById(R.id.friend_requests_status_text);

        // Set activity status to loading
        this.loading();

        // Configure horizontal layout for the events recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        this.friendRequestsRecyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.getFriendRequests();
    }

    private void getFriendRequests() {
        this.apiManager.getFriendRequests(this.authenticationToken.getAccessToken(), new Callback<ArrayList<UserProfile>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ArrayList<UserProfile>> call,
                                   @NonNull Response<ArrayList<UserProfile>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        // Get friend requests from API
                        friendRequests = response.body();

                        // Create UsersAdapter and pass it to the users recycler view
                        friendRequestsAdapter = new UsersAdapter(friendRequests, FriendRequestsFragment.this,
                                TAG_FRIEND_REQUESTS);
                        friendRequestsRecyclerView.setAdapter(friendRequestsAdapter);

                        // Update UI
                        friendRequestsAdapter.notifyDataSetChanged();
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
    public void loading() {
        this.friendRequestsStatusText.setVisibility(View.VISIBLE);
        this.friendRequestsStatusText.setText(getText(R.string.loading));
        this.friendRequestsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDataReceived() {
        if(this.friendRequests.isEmpty()) {
            this.onNoDataReceived();
        } else {
            this.friendRequestsStatusText.setVisibility(View.GONE);
            this.friendRequestsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNoDataReceived() {
        this.friendRequestsStatusText.setVisibility(View.VISIBLE);
        this.friendRequestsStatusText.setText(getText(R.string.noFriendRequests));
        this.friendRequestsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionFailure() {
        this.friendRequestsStatusText.setVisibility(View.VISIBLE);
        this.friendRequestsStatusText.setText(getText(R.string.serverConnectionFailed));
        this.friendRequestsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onUserClicked(int index) {
        requireActivity().getSupportFragmentManager().beginTransaction().
                add(R.id.home_fragment_container,
                        new UserProfileFragment(this.friendRequests.get(index))).
                addToBackStack(this.getClass().getName()).
                commit();
    }
}