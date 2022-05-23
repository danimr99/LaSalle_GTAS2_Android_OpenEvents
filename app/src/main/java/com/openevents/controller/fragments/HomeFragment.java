package com.openevents.controller.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.ActivityState;
import com.openevents.api.responses.Event;
import com.openevents.constants.Constants;
import com.openevents.model.adapters.PillAdapter;
import com.openevents.model.adapters.PopularEventsAdapter;
import com.openevents.model.interfaces.OnListEventListener;
import com.openevents.model.interfaces.OnListPillListener;
import com.openevents.utils.DateHandler;
import com.openevents.utils.Notification;
import com.openevents.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements ActivityState, OnListEventListener, OnListPillListener {
    // UI Components
    private EditText searchBar;
    private TextView seeAll;
    private TextView popularEventsStatusText;
    private RecyclerView popularEventsRecyclerView;
    private RecyclerView categoriesRecyclerView;
    private PopularEventsAdapter popularEventsAdapter;

    // Variables
    private APIManager apiManager;
    private SharedPrefs sharedPrefs;
    private ArrayList<Event> popularEvents;
    private ArrayList<Event> popularEventsFiltered;
    private ArrayList<Boolean> categoriesStatus;
    private String searchInput;

    public HomeFragment() {
        this.popularEvents = new ArrayList<>();
        this.popularEventsFiltered = new ArrayList<>();

        this.searchInput = "";

        this.categoriesStatus = new ArrayList<>();
        Arrays.asList(Constants.CATEGORIES).forEach(category -> categoriesStatus.add(true));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Create an instance of APIManager and SharedPreferences
        this.apiManager = APIManager.getInstance();
        this.sharedPrefs = SharedPrefs.getInstance(view.getContext());

        // Get popular events from API
        this.getPopularEvents();

        // Get components from view
        this.searchBar = view.findViewById(R.id.users_search_bar);
        this.seeAll = view.findViewById(R.id.see_all_label);
        this.popularEventsRecyclerView = view.findViewById(R.id.popular_events_recycler_view);
        this.popularEventsStatusText = view.findViewById(R.id.events_status_text);
        this.categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);

        // Set activity status to loading
        this.loading();

        // Set on click listener to "See all" label
        this.seeAll.setOnClickListener(v -> getParentFragmentManager().beginTransaction().
                replace(R.id.home_fragment_container, new EventsFragment()).commit());

        // Configure search bar
        this.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.length() == 0) {
                    popularEventsFiltered = popularEvents;
                    searchInput = "";
                } else {
                    searchInput = charSequence.toString();
                    filter(searchInput);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Configure horizontal layout for the events recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        this.popularEventsRecyclerView.setLayoutManager(linearLayoutManager);
        this.categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Set adapter to the categories recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoriesRecyclerView.setLayoutManager(layoutManager);
        PillAdapter pillAdapter = new PillAdapter(Constants.CATEGORIES, this.categoriesStatus,
                HomeFragment.this);
        categoriesRecyclerView.setAdapter(pillAdapter);

        return view;
    }

    private void filter(String text) {
        ArrayList<Event> filteredList = new ArrayList<>();

        for (Event event : this.popularEvents) {
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

        // Check if events from filtered list matches with selected categories
        ArrayList<Event> matchingFilters = new ArrayList<>();

        for(Event popular : filteredList) {
            for(int i = 0; i < Constants.CATEGORIES.length; i++) {
                if(this.categoriesStatus.get(i)) {
                    if(popular.getType().toLowerCase().contains(Constants.CATEGORIES[i].toLowerCase())) {
                        matchingFilters.add(popular);
                    }
                }
            }
        }

        // Save list of filtered popular events
        this.popularEventsFiltered = matchingFilters;

        // Update adapter
        this.popularEventsAdapter.updateDataset(this.popularEventsFiltered);
    }

    private void getPopularEvents() {
        this.apiManager.getPopularEvents(this.sharedPrefs.getAuthenticationToken(),
                new Callback<ArrayList<Event>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<ArrayList<Event>> call,
                                           @NonNull Response<ArrayList<Event>> response) {
                        if (response.isSuccessful()) {
                            if(response.body() != null) {
                                // Get events from response
                                popularEvents = response.body();
                                popularEventsFiltered = popularEvents;

                                // Create EventsAdapter and pass it to the events recycler view
                                popularEventsAdapter = new PopularEventsAdapter(popularEventsFiltered, HomeFragment.this);
                                popularEventsRecyclerView.setAdapter(popularEventsAdapter);

                                // Update dataset and view
                                popularEventsAdapter.notifyDataSetChanged();
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
        this.popularEventsStatusText.setVisibility(View.VISIBLE);
        this.popularEventsStatusText.setText(getText(R.string.loading));
        this.popularEventsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDataReceived() {
        this.popularEventsStatusText.setVisibility(View.GONE);
        this.popularEventsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoDataReceived() {
        this.popularEventsStatusText.setVisibility(View.VISIBLE);
        this.popularEventsStatusText.setText(getText(R.string.noPopularEvents));
        this.popularEventsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionFailure() {
        this.popularEventsStatusText.setVisibility(View.VISIBLE);
        this.popularEventsStatusText.setText(getText(R.string.serverConnectionFailed));
        this.popularEventsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onEventClicked(int index) {
        getParentFragmentManager().beginTransaction().
                add(R.id.home_fragment_container,
                        new EventDetailsFragment(this.popularEventsFiltered.get(index))).
                addToBackStack(this.getClass().getName()).
                commit();
    }

    @Override
    public void onPillClicked(int index, boolean status) {
        this.categoriesStatus.set(index, status);
        this.filter(this.searchInput);
    }
}