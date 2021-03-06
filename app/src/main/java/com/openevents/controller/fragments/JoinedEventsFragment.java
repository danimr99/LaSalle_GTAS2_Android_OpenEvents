package com.openevents.controller.fragments;

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
import com.openevents.api.responses.Event;
import com.openevents.model.adapters.EventsAdapter;
import com.openevents.model.interfaces.OnListEventListener;
import com.openevents.utils.SharedPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class JoinedEventsFragment extends Fragment implements OnListEventListener, ActivityState {
    // UI Components
    private TextView joinedStatusText;
    private RecyclerView joinedRecyclerView;
    private EventsAdapter joinedAdapter;

    // Variables
    private ArrayList<Event> joinedEvents;
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;

    public JoinedEventsFragment() {
        this.joinedEvents = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_joined_events, container, false);

        // Create an instance of APIManager and SharedPreferences
        this.apiManager = APIManager.getInstance();
        this.sharedPrefs = SharedPrefs.getInstance(view.getContext());

        // Get user authentication token
        this.authenticationToken =
                new AuthenticationToken(this.sharedPrefs.getAuthenticationToken());

        // Get events joined by the user from API
        this.getJoinedEvents();

        // Get all components from view
        this.joinedRecyclerView = view.findViewById(R.id.joined_events_recycler_view);
        this.joinedStatusText = view.findViewById(R.id.joined_events_status_text);

        // Set activity status to loading
        this.loading();

        // Configure horizontal layout for the events created recycler view
        LinearLayoutManager linearLayoutManagerEventsCreated = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        this.joinedRecyclerView.setLayoutManager(linearLayoutManagerEventsCreated);

        return view;
    }

    private void getJoinedEvents() {
        final int loggedInUserID = this.sharedPrefs.getUser().getId();

        // Get events where the logged in user is an assistance
        this.apiManager.getUserJoinedEvents(this.authenticationToken.getAccessToken(),
                loggedInUserID, new Callback<ArrayList<Event>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Event>> call, @NonNull Response<ArrayList<Event>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        // Get all events where the user is an assistant
                        joinedEvents = response.body();

                        // Set adapter for the events joined recycler view
                        joinedAdapter = new EventsAdapter(joinedEvents,
                                JoinedEventsFragment.this);
                        joinedRecyclerView.setAdapter(joinedAdapter);

                        // Update UI
                        onDataReceived();
                    } else {
                        onNoDataReceived();
                    }
                } else {
                    onNoDataReceived();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Event>> call, @NonNull Throwable t) {
                onConnectionFailure();
            }
        });
    }

    @Override
    public void onEventClicked(int index) {
        requireActivity().getSupportFragmentManager().beginTransaction().
                add(R.id.home_fragment_container,
                        new EventDetailsFragment(this.joinedEvents.get(index), false)).
                addToBackStack(this.getClass().getName()).
                commit();
    }

    @Override
    public void loading() {
        this.joinedStatusText.setVisibility(View.VISIBLE);
        this.joinedStatusText.setText(getText(R.string.loading));
        this.joinedRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDataReceived() {
        if(this.joinedEvents.isEmpty()) {
            this.onNoDataReceived();
        } else {
            this.joinedStatusText.setVisibility(View.GONE);
            this.joinedRecyclerView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onNoDataReceived() {
        this.joinedStatusText.setVisibility(View.VISIBLE);
        this.joinedStatusText.setText(getText(R.string.noJoinedEvents));
        this.joinedRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionFailure() {
        this.joinedStatusText.setVisibility(View.VISIBLE);
        this.joinedStatusText.setText(getText(R.string.serverConnectionFailed));
        this.joinedRecyclerView.setVisibility(View.GONE);
    }
}