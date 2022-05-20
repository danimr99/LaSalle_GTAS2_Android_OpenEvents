package com.openevents.controller.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.openevents.api.APIManager;
import com.openevents.api.ActivityState;
import com.openevents.api.responses.Event;
import com.openevents.R;
import com.openevents.model.adapters.EventsAdapter;
import com.openevents.model.interfaces.OnEventListener;
import com.openevents.utils.SharedPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EventsFragment extends Fragment implements ActivityState, OnEventListener {
    private CheckBox sortByRating;
    private TextView eventsStatusText;
    private RecyclerView eventsRecyclerView;
    private RecyclerView.Adapter eventsAdapter;
    private APIManager apiManager;
    private SharedPrefs sharedPrefs;
    private ArrayList<Event> events;

    public EventsFragment(ArrayList<Event> events) {
        this.events = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        // Create an instance of APIManager and SharedPreferences
        this.apiManager = APIManager.getInstance();
        this.sharedPrefs = SharedPrefs.getInstance(view.getContext());

        // Get popular events from API
        this.getEvents();

        // Get components from view
        this.sortByRating = view.findViewById(R.id.sort_by_rating_checkbox);
        this.eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        this.eventsStatusText = view.findViewById(R.id.events_status_text);

        // Set activity status to loading
        this.loading();

        // TODO Configure on click listener for the checkbox

        // Configure horizontal layout for the events recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        this.eventsRecyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    private void getEvents() {
        this.apiManager.getEvents(this.sharedPrefs.getAuthenticationToken(), new Callback<ArrayList<Event>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Event>> call, @NonNull Response<ArrayList<Event>> response) {
                if (response.isSuccessful()) {
                    if(response.body() != null) {
                        // Get events from response
                        events = response.body();

                        // Create EventsAdapter and pass it to the events recycler view
                        eventsAdapter = new EventsAdapter(events, EventsFragment.this);
                        eventsRecyclerView.setAdapter(eventsAdapter);

                        // Update dataset and view
                        eventsAdapter.notifyDataSetChanged();
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
    public void loading() {
        this.eventsStatusText.setVisibility(View.VISIBLE);
        this.eventsStatusText.setText(getText(R.string.loading));
        this.eventsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDataReceived() {
        this.eventsStatusText.setVisibility(View.GONE);
        this.eventsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoDataReceived() {
        this.eventsStatusText.setVisibility(View.VISIBLE);
        this.eventsStatusText.setText(getText(R.string.noEvents));
        this.eventsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionFailure() {
        this.eventsStatusText.setVisibility(View.VISIBLE);
        this.eventsStatusText.setText(getText(R.string.serverConnectionFailed));
        this.eventsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onEventClick(int eventPosition) {
        getParentFragmentManager().beginTransaction().
                replace(R.id.home_fragment_container,
                        new EventDetailsFragment(this.events.get(eventPosition))).
                commit();
    }
}