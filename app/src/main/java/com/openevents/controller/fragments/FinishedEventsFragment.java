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
import com.openevents.utils.Notification;
import com.openevents.utils.SharedPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinishedEventsFragment extends Fragment implements OnListEventListener, ActivityState {
    // UI Components
    private TextView finishedEventsStatusText;
    private RecyclerView pastAssistantsEventsRecyclerView;
    private EventsAdapter pastAssistantsEventsAdapter;

    // Variables
    private ArrayList<Event> pastAssistants;
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;

    public FinishedEventsFragment() {
        this.pastAssistants = new ArrayList<>();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finished_events, container, false);

        // Get instance of SharedPrefs
        this.sharedPrefs = SharedPrefs.getInstance(getContext());

        // Get user authentication token
        this.authenticationToken =
                new AuthenticationToken(this.sharedPrefs.getAuthenticationToken());

        // Get an instance of APIManager
        this.apiManager = APIManager.getInstance();

        // Get past assistants from API
        this.getPastAssistants();

        // Get all components from view
        this.pastAssistantsEventsRecyclerView = view.findViewById(R.id.finished_events_recycler_view);
        this.finishedEventsStatusText = view.findViewById(R.id.finished_events_status_text);



        // Configure horizontal layout for the events recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        this.pastAssistantsEventsRecyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    private void getPastAssistants() {
        // Get logged in user ID from SharedPreferences
        final int loggedInUserID = this.sharedPrefs.getUser().getId();

        this.apiManager.getUserPastAssistants(this.authenticationToken.getAccessToken(),
                loggedInUserID, new Callback<ArrayList<Event>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Event>> call,
                                   @NonNull Response<ArrayList<Event>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        // Get past assistants from API
                        pastAssistants = response.body();

                        // Create EventsAdapter and pass it to the past assistants recycler view
                        pastAssistantsEventsAdapter = new EventsAdapter(pastAssistants,
                                FinishedEventsFragment.this);
                        pastAssistantsEventsRecyclerView.setAdapter(pastAssistantsEventsAdapter);

                        // Update dataset and view
                        pastAssistantsEventsAdapter.updateDataset(pastAssistants);
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
            public void onFailure(@NonNull Call<ArrayList<Event>> call, @NonNull Throwable t) {
                // Set activity status to connection failure
                onConnectionFailure();
            }
        });
    }

    @Override
    public void onEventClicked(int index) {
        requireActivity().getSupportFragmentManager().beginTransaction().
                add(R.id.home_fragment_container,
                        new EventDetailsFragment(this.pastAssistants.get(index), true)).
                addToBackStack(this.getClass().getName()).
                commit();
    }

    @Override
    public void loading() {
        this.finishedEventsStatusText.setVisibility(View.VISIBLE);
        this.finishedEventsStatusText.setText(getText(R.string.loading));
        this.pastAssistantsEventsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDataReceived() {
        if(this.pastAssistants.isEmpty()) {
            this.onNoDataReceived();
        } else {
            this.finishedEventsStatusText.setVisibility(View.GONE);
            this.pastAssistantsEventsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNoDataReceived() {
        this.finishedEventsStatusText.setVisibility(View.VISIBLE);
        this.finishedEventsStatusText.setText(getText(R.string.noFinishedEvents));
        this.pastAssistantsEventsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionFailure() {
        this.finishedEventsStatusText.setVisibility(View.VISIBLE);
        this.finishedEventsStatusText.setText(getText(R.string.serverConnectionFailed));
        this.pastAssistantsEventsRecyclerView.setVisibility(View.GONE);
    }
}