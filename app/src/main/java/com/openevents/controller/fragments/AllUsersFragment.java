package com.openevents.controller.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.UserProfile;
import com.openevents.model.adapters.EventsAdapter;
import com.openevents.model.adapters.UsersAdapter;
import com.openevents.model.interfaces.OnListItemListener;
import com.openevents.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AllUsersFragment extends Fragment implements OnListItemListener {
    // Constants
    public static final String TAG_ALL_USERS = "ALL_USERS";

    // UI Components
    private EditText usersSearchBar;
    private RecyclerView allUsersRecyclerView;
    private RecyclerView.Adapter allUsersAdapter;

    // Variables
    private ArrayList<UserProfile> users;
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;

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

        // Create an instance of APIManager and SharedPreferences
        this.apiManager = APIManager.getInstance();
        this.sharedPrefs = SharedPrefs.getInstance(view.getContext());

        // Get user authentication token
        this.authenticationToken =
                new AuthenticationToken(this.sharedPrefs.getAuthenticationToken());

        // Get users from API
        this.getUsers();

        // Get all components from view
        this.usersSearchBar = view.findViewById(R.id.users_search_bar);
        this.allUsersRecyclerView = view.findViewById(R.id.all_users_recycler_view);

        // Configure horizontal layout for the events recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        this.allUsersRecyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    private void getUsers() {
        this.apiManager.getUsers(this.authenticationToken.getAccessToken(),
                new Callback<ArrayList<UserProfile>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<UserProfile>> call,
                                   @NonNull Response<ArrayList<UserProfile>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        // Get users from API
                        users = response.body();

                        // Create UsersAdapter and pass it to the users recycler view
                        allUsersAdapter = new UsersAdapter(users, AllUsersFragment.this,
                                TAG_ALL_USERS);
                        allUsersRecyclerView.setAdapter(allUsersAdapter);

                        // Update UI
                        allUsersAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<UserProfile>> call, @NonNull Throwable t) {}
        });
    }

    @Override
    public void onListItemClicked(int index) {
        requireActivity().getSupportFragmentManager().beginTransaction().
                add(R.id.home_fragment_container,
                        new UserProfileFragment(this.users.get(index))).
                addToBackStack(this.getClass().getName()).
                commit();
    }
}