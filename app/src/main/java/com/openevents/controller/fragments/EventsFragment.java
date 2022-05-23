package com.openevents.controller.fragments;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openevents.api.APIManager;
import com.openevents.api.ActivityState;
import com.openevents.api.responses.Event;
import com.openevents.R;
import com.openevents.model.adapters.EventsAdapter;
import com.openevents.model.interfaces.OnListEventListener;
import com.openevents.utils.DateHandler;
import com.openevents.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EventsFragment extends Fragment implements ActivityState, OnListEventListener {
    // UI Components
    private EditText searchBar;
    private LinearLayout sortByStartDate;
    private ImageView sortByStartDateIcon;
    private TextView sortByStartDateText;
    private TextView eventsStatusText;
    private RecyclerView eventsRecyclerView;
    private EventsAdapter eventsAdapter;

    // Variables
    private APIManager apiManager;
    private SharedPrefs sharedPrefs;
    private ArrayList<Event> events;
    private ArrayList<Event> eventsFiltered;
    private boolean ascOrder;

    public EventsFragment() {
        this.events = new ArrayList<>();
        this.events = new ArrayList<>();
        this.ascOrder = true;
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
        this.searchBar = view.findViewById(R.id.events_search_bar);
        this.eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        this.eventsStatusText = view.findViewById(R.id.events_status_text);
        this.sortByStartDate = view.findViewById(R.id.sort_by_start_date);
        this.sortByStartDateIcon = view.findViewById(R.id.sort_by_start_date_icon);
        this.sortByStartDateText = view.findViewById(R.id.sort_by_start_date_label);

        // Set activity status to loading
        this.loading();

        // Configure search bar
        this.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.length() == 0) {
                    eventsFiltered = events;
                } else {
                    filter(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Configure on click listener for the sort toggle
        this.sortByStartDate.setOnClickListener(v -> toggleSortByStartDateOrder());

        // Configure horizontal layout for the events recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        this.eventsRecyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    private void toggleSortByStartDateOrder() {
        // Set icon to the corresponding rotation
        if(this.ascOrder) {
            this.sortByStartDateIcon.setRotation(270);
        } else {
            this.sortByStartDateIcon.setRotation(90);
        }

        // Change value
        this.ascOrder = !this.ascOrder;

        // Update EventsAdapter
        Collections.reverse(this.eventsFiltered);
        this.eventsAdapter.notifyDataSetChanged();
    }

    private void filter(String text) {
        ArrayList<Event> filteredList = new ArrayList<>();

        for (Event event : this.events) {
            // Check for event name
            if (event.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(event);
            }

            // Check for event location
            if(event.getLocation().toLowerCase().contains(text.toLowerCase()) &&
                    !filteredList.contains(event)) {
                filteredList.add(event);
            }

            // Check for start date
            if(DateHandler.toDateTime(event.getEventStartDate()).contains(text.toLowerCase()) &&
            !filteredList.contains(event)) {
                filteredList.add(event);
            }
        }

        // Save list of filtered popular events
        this.eventsFiltered = filteredList;

        // Update adapter
        eventsAdapter.updateDataset(filteredList);
    }

    private void getEvents() {
        this.apiManager.getEvents(this.sharedPrefs.getAuthenticationToken(),
                new Callback<ArrayList<Event>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Event>> call,
                                   @NonNull Response<ArrayList<Event>> response) {
                if (response.isSuccessful()) {
                    if(response.body() != null) {
                        // Get events from response
                        events = response.body();
                        eventsFiltered = events;

                        // Create EventsAdapter and pass it to the events recycler view
                        eventsAdapter = new EventsAdapter(eventsFiltered, EventsFragment.this);
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
    public void onEventClicked(int index) {
        getParentFragmentManager().beginTransaction().
                add(R.id.home_fragment_container,
                        new EventDetailsFragment(this.eventsFiltered.get(index), false)).
                addToBackStack(this.getClass().getName()).
                commit();
    }
}