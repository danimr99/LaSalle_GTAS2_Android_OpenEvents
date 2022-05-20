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
import com.openevents.api.responses.Event;
import com.openevents.model.adapters.PopularEventsAdapter;
import com.openevents.model.interfaces.OnEventListener;
import com.openevents.utils.SharedPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements ActivityState, OnEventListener {
    private TextView seeAll;
    private TextView popularEventsStatusText;
    private RecyclerView popularEventsRecyclerView;
    private RecyclerView.Adapter popularEventsAdapter;
    private APIManager apiManager;
    private SharedPrefs sharedPrefs;
    private ArrayList<Event> popularEvents;

    public HomeFragment() {
        this.popularEvents = new ArrayList<>();
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
        this.seeAll = view.findViewById(R.id.see_all_label);
        this.popularEventsRecyclerView = view.findViewById(R.id.popular_events_recycler_view);
        this.popularEventsStatusText = view.findViewById(R.id.events_status_text);

        // Set activity status to loading
        this.loading();

        // Set on click listener to "See all" label
        this.seeAll.setOnClickListener(v -> getParentFragmentManager().beginTransaction().
                replace(R.id.home_fragment_container, new EventsFragment(this.popularEvents)).commit());

        // Configure horizontal layout for the events recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        this.popularEventsRecyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    private void getPopularEvents() {
        this.apiManager.getPopularEvents(this.sharedPrefs.getAuthenticationToken(),
                new Callback<ArrayList<Event>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Event>> call, @NonNull Response<ArrayList<Event>> response) {
                if (response.isSuccessful()) {
                    if(response.body() != null) {
                        // Get events from response
                        popularEvents = response.body();

                        // Create EventsAdapter and pass it to the events recycler view
                        popularEventsAdapter = new PopularEventsAdapter(popularEvents, HomeFragment.this);
                        popularEventsRecyclerView.setAdapter(popularEventsAdapter);
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
    public void onEventClick(int eventPosition) {
        getParentFragmentManager().beginTransaction().
                replace(R.id.home_fragment_container,
                        new EventDetailsFragment(this.popularEvents.get(eventPosition))).
                commit();
    }
}