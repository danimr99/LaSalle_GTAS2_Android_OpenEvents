package com.openevents.controller.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
    private ImageView backArrow;
    private ImageView eventImage;
    private TextView eventTitle;
    private TextView eventOwner;
    private TextView eventLocation;
    private TextView eventStartDate;
    private TextView eventEndDate;
    private TextView eventCategory;
    private TextView eventParticipants;
    private TextView eventDescription;

    // Variables
    private final Event event;
    private User owner;
    private ArrayList<Assistance> assistants;
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;

    public EventDetailsFragment(Event event) {
        this.event = event;
        this.assistants = new ArrayList<>();
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
        this.backArrow = view.findViewById(R.id.event_details_back_arrow);
        this.eventImage = view.findViewById(R.id.event_details_image);
        this.eventTitle = view.findViewById(R.id.event_details_title);
        this.eventOwner = view.findViewById(R.id.event_details_owner);
        this.eventLocation = view.findViewById(R.id.event_details_location);
        this.eventStartDate = view.findViewById(R.id.event_details_start_date);
        this.eventEndDate = view.findViewById(R.id.event_details_end_date);
        this.eventCategory = view.findViewById(R.id.event_details_category);
        this.eventParticipants = view.findViewById(R.id.event_details_participants);
        this.eventDescription = view.findViewById(R.id.event_details_description);

        // Configure back arrow on click
        this.backArrow.setOnClickListener(v -> this.navigateBack());

        // Update event details UI
        this.updateEventDetailsUI(this.event, null);

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void updateEventDetailsUI(Event event, User eventOwner) {
        // Set image from the event
        if(event.getImage() != null && event.getImage().trim().length() != 0) {
            Picasso.get()
                    .load(event.getImage())
                    .placeholder(R.drawable.event_placeholder)
                    .error(R.drawable.event_placeholder)
                    .into(this.eventImage);
        } else {
            Picasso.get().load(R.drawable.event_placeholder).into(this.eventImage);
        }

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

        if(assistants.isEmpty()) {
            this.eventParticipants.setText("0/" + event.getParticipatorsQuantity());
        } else {
            this.eventParticipants.setText(this.assistants.size() + "/" +
                    event.getParticipatorsQuantity());
        }

        this.eventDescription.setText(event.getDescription());
    }

    private void getEventOwner(int userID) {
        this.apiManager.getUserById(this.authenticationToken.getAccessToken(), userID,
                new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<User>> call, @NonNull Response<ArrayList<User>> response) {
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
            public void onFailure(@NonNull Call<ArrayList<User>> call, @NonNull Throwable t) { }
        });
    }

    private void getEventParticipants(int eventID) {
        this.apiManager.getEventAssistants(this.authenticationToken.getAccessToken(), eventID,
                new Callback<ArrayList<Assistance>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Assistance>> call, @NonNull Response<ArrayList<Assistance>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        // Get list of assistance
                        assistants = response.body();

                        // Update event details UI
                        updateEventDetailsUI(event, owner);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Assistance>> call, @NonNull Throwable t) {

            }
        });
    }

    private void navigateBack() {
        getParentFragmentManager().popBackStack();
    }
}