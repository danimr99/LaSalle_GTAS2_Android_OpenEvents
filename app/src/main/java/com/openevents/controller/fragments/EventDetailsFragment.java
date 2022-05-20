package com.openevents.controller.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.responses.Assistance;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.Event;
import com.openevents.api.responses.User;
import com.openevents.utils.DateParser;
import com.openevents.utils.SharedPrefs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsFragment extends Fragment {
    // UI Components
    private ImageView eventImage;
    private TextView eventTitle;
    private TextView eventOwner;
    private TextView eventLocation;
    private TextView eventStartDate;
    private TextView eventEndDate;
    private TextView eventCategory;
    private TextView eventParticipants;

    // Variables
    private Event event;
    private User owner;
    private ArrayList<Assistance> participants;
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;

    public EventDetailsFragment(Event event) {
        this.event = event;
        this.participants = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_event_details, container, false);

        // Get instance of SharedPrefs
        this.sharedPrefs = SharedPrefs.getInstance(getContext());

        // Get user authentication token
        this.authenticationToken =
                new AuthenticationToken(this.sharedPrefs.getAuthenticationToken());

        // Get an instance of APIManager and get the event owner from API
        this.apiManager = APIManager.getInstance();
        this.getEventOwner(this.event.getOwnerId());
        this.getEventParticipants(this.event.getId());

        // Get components from view
        this.eventImage = view.findViewById(R.id.event_details_image);
        this.eventTitle = view.findViewById(R.id.event_details_title);
        this.eventOwner = view.findViewById(R.id.event_details_owner);
        this.eventLocation = view.findViewById(R.id.event_details_location);
        this.eventStartDate = view.findViewById(R.id.event_details_start_date);
        this.eventEndDate = view.findViewById(R.id.event_details_end_date);
        this.eventCategory = view.findViewById(R.id.event_details_category);

        // Update event details UI
        this.updateEventDetailsUI(this.event, null);

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void updateEventDetailsUI(Event event, User eventOwner) {
        // Set image from the user
        Picasso.get()
                .load(event.getImage())
                .placeholder(R.drawable.event_placeholder)
                .error(R.drawable.event_placeholder)
                .into(this.eventImage);

        // Set data to corresponding field
        this.eventTitle.setText(event.getName());

        if(eventOwner == null) {
            this.eventOwner.setText("Owner ID: " + event.getOwnerId());
        } else {
            this.owner = eventOwner;
            this.eventOwner.setText(eventOwner.getName() + " " + eventOwner.getLastName());
        }

        this.eventLocation.setText(event.getLocation());
        this.eventStartDate.setText(DateParser.toDateTime(event.getEventStartDate()));
        this.eventEndDate.setText(DateParser.toDateTime(event.getEventEndDate()));
        this.eventCategory.setText(event.getType());
    }

    private void getEventOwner(int userID) {
        this.apiManager.getUserById(this.authenticationToken.getAccessToken(), userID,
                new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        // Get event owner
                        User owner = response.body().get(0);

                        // Update event details UI
                        updateEventDetailsUI(event, owner);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) { }
        });
    }

    private void getEventParticipants(int eventID) {
        // this.apiManager.getEventAssistances()
    }
}