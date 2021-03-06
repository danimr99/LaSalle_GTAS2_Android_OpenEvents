package com.openevents.controller.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.ActivityState;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.Event;
import com.openevents.constants.Constants;
import com.openevents.model.adapters.EventsAdapter;
import com.openevents.model.adapters.PillAdapter;
import com.openevents.model.interfaces.OnListEventListener;
import com.openevents.model.interfaces.OnListPillListener;
import com.openevents.utils.Notification;
import com.openevents.utils.SharedPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyCreatedEventsFragment extends Fragment implements OnListEventListener,
        OnListPillListener, ActivityState {
    // UI Components
    private RecyclerView myCreatedEventsRecyclerView;
    private EventsAdapter myCreatedEventsAdapter;
    private TextView myCreatedEventsStatusText;

    // Variables
    private final ArrayList<Event> myCreatedEvents;
    private ArrayList<Event> myCreatedFinishedEvents;
    private ArrayList<Event> myCreatedActiveEvents;
    private ArrayList<Event> myCreatedFutureEvents;
    private final ArrayList<Boolean> timeStatesStatus;
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;


    public MyCreatedEventsFragment() {
        this.myCreatedEvents = new ArrayList<>();
        this.myCreatedFinishedEvents = new ArrayList<>();
        this.myCreatedActiveEvents = new ArrayList<>();
        this.myCreatedFutureEvents = new ArrayList<>();

        this.timeStatesStatus = new ArrayList<>();

        // Set default status of each time state button [0 -> Finished, 1 -> Active, 2 -> Future]
        this.timeStatesStatus.add(false); // 0
        this.timeStatesStatus.add(true);  // 1
        this.timeStatesStatus.add(true);  // 2
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_created_events, container, false);

        // Create an instance of APIManager and SharedPreferences
        this.apiManager = APIManager.getInstance();
        this.sharedPrefs = SharedPrefs.getInstance(view.getContext());

        // Get user authentication token
        this.authenticationToken =
                new AuthenticationToken(this.sharedPrefs.getAuthenticationToken());

        // Get events created by the user from API
        this.getCreatedEvents();

        // Get all components from view
        RecyclerView timeStateRecyclerView = view.findViewById(R.id.time_state_recycler_view);
        this.myCreatedEventsRecyclerView = view.findViewById(R.id.my_created_events_recycler_view);
        FloatingActionButton createEvent = view.findViewById(R.id.fab_create_event_button);
        this.myCreatedEventsStatusText = view.findViewById(R.id.my_events_created_status_text);

        // Set activity status to loading
        this.loading();

        // Set on click listener to create event fab button
        createEvent.setOnClickListener(v -> createNewEvent());

        // Configure horizontal layout for the time states recycler view
        LinearLayoutManager linearLayoutManagerTimeStates = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        timeStateRecyclerView.setLayoutManager(linearLayoutManagerTimeStates);

        // Configure horizontal layout for the events created recycler view
        LinearLayoutManager linearLayoutManagerEventsCreated = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        myCreatedEventsRecyclerView.setLayoutManager(linearLayoutManagerEventsCreated);

        // Set adapter for the events recycler view
        this.myCreatedEventsAdapter = new EventsAdapter(this.myCreatedEvents,
                MyCreatedEventsFragment.this);
        myCreatedEventsRecyclerView.setAdapter(this.myCreatedEventsAdapter);

        // Handle scrolling issues between RecyclerView and ViewPager2
        timeStateRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                int action = e.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    rv.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });

        // Set adapter for the time states recycler view
        PillAdapter timeStateAdapter = new PillAdapter(Constants.EVENT_TIME_STATES, this.timeStatesStatus,
                MyCreatedEventsFragment.this);
        timeStateRecyclerView.setAdapter(timeStateAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.getCreatedEvents();
    }

    private void getCreatedEvents() {
        final int loggedInUserID = this.sharedPrefs.getUser().getId();

        // Get finished events created by the logged in user
        this.apiManager.getFinishedEventsCreatedByUser(this.authenticationToken.getAccessToken(),
                loggedInUserID, new Callback<ArrayList<Event>>() {
                    @Override
                    public void onResponse(@NonNull Call<ArrayList<Event>> call, @NonNull Response<ArrayList<Event>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                // Get finished events created by the logged in user
                                myCreatedFinishedEvents = response.body();

                                // Update adapter
                                updateEventsList();
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

        // Get active events created by the logged in user
        this.apiManager.getActiveEventsCreatedByUser(this.authenticationToken.getAccessToken(),
                loggedInUserID, new Callback<ArrayList<Event>>() {
                    @Override
                    public void onResponse(@NonNull Call<ArrayList<Event>> call,
                                           @NonNull Response<ArrayList<Event>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                // Get active events created by the logged in user
                                myCreatedActiveEvents = response.body();

                                // Update adapter
                                updateEventsList();
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

        // Get future events created by the logged in user
        this.apiManager.getFutureEventsCreatedByUser(this.authenticationToken.getAccessToken(),
                loggedInUserID, new Callback<ArrayList<Event>>() {
                    @Override
                    public void onResponse(@NonNull Call<ArrayList<Event>> call, @NonNull Response<ArrayList<Event>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                // Get future events created by the logged in user
                                myCreatedFutureEvents = response.body();

                                // Update adapter
                                updateEventsList();
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

    private void updateEventsList() {
        // Add to created events all the time states selected
        this.myCreatedEvents.clear();
        for(int i = 0; i < this.timeStatesStatus.size(); i++) {
            if(this.timeStatesStatus.get(i)) {
                if(i == 0) {
                    this.myCreatedEvents.addAll(this.myCreatedFinishedEvents);
                } else if(i == 1) {
                    this.myCreatedEvents.addAll(this.myCreatedActiveEvents);
                } else {
                    this.myCreatedEvents.addAll(this.myCreatedFutureEvents);
                }
            }
        }

        this.myCreatedEventsAdapter.updateDataset(this.myCreatedEvents);
    }

    private void createNewEvent() {
        requireActivity().getSupportFragmentManager().beginTransaction().
                add(R.id.home_fragment_container,
                        new EventCreationFragment()).
                addToBackStack(this.getClass().getName()).
                commit();
    }

    @Override
    public void onEventClicked(int index) {
        requireActivity().getSupportFragmentManager().beginTransaction().
                add(R.id.home_fragment_container,
                        new EventDetailsFragment(this.myCreatedEvents.get(index), true)).
                addToBackStack(this.getClass().getName()).
                commit();
    }

    @Override
    public void onPillClicked(int index, boolean status) {
        this.timeStatesStatus.set(index, status);
        this.updateEventsList();
    }

    @Override
    public void loading() {
        this.myCreatedEventsStatusText.setVisibility(View.VISIBLE);
        this.myCreatedEventsStatusText.setText(getText(R.string.loading));
        this.myCreatedEventsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDataReceived() {
        if(this.myCreatedEvents.isEmpty()) {
            onNoDataReceived();
        } else {
            this.myCreatedEventsStatusText.setVisibility(View.GONE);
            this.myCreatedEventsRecyclerView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onNoDataReceived() {
        this.myCreatedEventsStatusText.setVisibility(View.VISIBLE);
        this.myCreatedEventsStatusText.setText(getText(R.string.noCreatedEvents));
        this.myCreatedEventsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionFailure() {
        this.myCreatedEventsStatusText.setVisibility(View.VISIBLE);
        this.myCreatedEventsStatusText.setText(getText(R.string.serverConnectionFailed));
        this.myCreatedEventsRecyclerView.setVisibility(View.GONE);
    }
}