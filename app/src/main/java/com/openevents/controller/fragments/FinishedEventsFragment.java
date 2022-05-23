package com.openevents.controller.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.Event;
import com.openevents.model.adapters.EventsAdapter;
import com.openevents.model.adapters.UsersAdapter;
import com.openevents.model.interfaces.OnListEventListener;
import com.openevents.utils.Notification;
import com.openevents.utils.SharedPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinishedEventsFragment extends Fragment implements OnListEventListener {
    // UI Components
    private RecyclerView pastAssistancesEvents;
    private EventsAdapter pastAssistancesEventsAdapter;

    // Variables
    private ArrayList<Event> pastAssistances;
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;

    public FinishedEventsFragment() {
        this.pastAssistances = new ArrayList<>();
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

        // Get past assistances from API
        this.getPastAssistances();

        // Get all components from view
        this.pastAssistancesEvents = view.findViewById(R.id.finished_events_recycler_view);

        // Configure horizontal layout for the events recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        this.pastAssistancesEvents.setLayoutManager(linearLayoutManager);

        return view;
    }

    private void getPastAssistances() {
        // Get logged in user ID from SharedPreferences
        final int loggedInUserID = this.sharedPrefs.getUser().getId();

        this.apiManager.getUserPastAssistances(this.authenticationToken.getAccessToken(),
                loggedInUserID, new Callback<ArrayList<Event>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Event>> call,
                                   @NonNull Response<ArrayList<Event>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        // Get past assistances from API
                        pastAssistances = response.body();

                        // Create EventsAdapter and pass it to the past assistances recycler view
                        pastAssistancesEventsAdapter = new EventsAdapter(pastAssistances,
                                FinishedEventsFragment.this);
                        pastAssistancesEvents.setAdapter(pastAssistancesEventsAdapter);

                        // Update dataset and view
                        pastAssistancesEventsAdapter.updateDataset(pastAssistances);
                    } else {
                        Notification.showDialogNotification(getContext(),
                                getText(R.string.serverConnectionFailed).toString());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Event>> call, @NonNull Throwable t) {
                Notification.showDialogNotification(getContext(),
                        getText(R.string.cannotConnectToServerError).toString());
            }
        });
    }

    @Override
    public void onEventClicked(int index) {
        requireActivity().getSupportFragmentManager().beginTransaction().
                add(R.id.home_fragment_container,
                        new EventDetailsFragment(this.pastAssistances.get(index), true)).
                addToBackStack(this.getClass().getName()).
                commit();
    }
}