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
import com.openevents.api.responses.QueryResponse;
import com.openevents.api.responses.User;
import com.openevents.constants.Constants;
import com.openevents.utils.DateHandler;
import com.openevents.utils.Notification;
import com.openevents.utils.SharedPrefs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

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
    private TextView editButton;
    private TextView deleteButton;
    private TextView joinButton;
    private TextView leaveButton;
    private TextView commentButton;
    private TextView rateButton;

    // Variables
    private final Event event;
    private User owner;
    private ArrayList<Assistance> assistants;
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;
    private boolean mustUpdateOnPopBack;

    public EventDetailsFragment(Event event, boolean fromMyEvents) {
        this.event = event;
        this.assistants = new ArrayList<>();

        this.mustUpdateOnPopBack = fromMyEvents;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        // Get instance of SharedPrefs
        this.sharedPrefs = SharedPrefs.getInstance(getContext());

        // Get user authentication token
        this.authenticationToken =
                new AuthenticationToken(this.sharedPrefs.getAuthenticationToken());

        // Get an instance of APIManager
        this.apiManager = APIManager.getInstance();

        // Get event owner and participants from API
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

        // Get all buttons from view
        this.deleteButton = view.findViewById(R.id.delete_text_view);
        this.editButton = view.findViewById(R.id.edit_text_view);
        this.joinButton = view.findViewById(R.id.join_text_view);
        this.leaveButton = view.findViewById(R.id.leave_text_view);
        this.commentButton = view.findViewById(R.id.comment_text_view);
        this.rateButton = view.findViewById(R.id.rate_text_view);

        // Configure back arrow on click
        this.backArrow.setOnClickListener(v -> this.navigateBack());

        // Set on click listener to delete button
        this.deleteButton.setOnClickListener(v -> deleteEvent());

        // Set on click listener to edit button
        this.editButton.setOnClickListener(v -> editEvent());

        // Set on click listener to join button
        this.joinButton.setOnClickListener(v -> joinEvent());

        // Set on click listener to leave button
        this.leaveButton.setOnClickListener(v -> leaveEvent());

        // Set on click listener to join button
        this.commentButton.setOnClickListener(v -> commentEvent());

        // Set on click listener to join button
        this.rateButton.setOnClickListener(v -> rateEvent());

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void updateEventDetailsUI(Event event, User eventOwner) {
        // Get logged in user
        final User loggedInUser = this.sharedPrefs.getUser();

        // Set image from the event
        if (event.getImage() != null && event.getImage().trim().length() != 0) {
            Picasso.get()
                    .load(event.getImage())
                    .placeholder(R.drawable.event_placeholder)
                    .error(R.drawable.event_placeholder)
                    .resize(Constants.MAX_IMAGE_WIDTH, Constants.MAX_IMAGE_HEIGHT)
                    .into(this.eventImage);
        } else {
            Picasso.get().load(R.drawable.event_placeholder).into(this.eventImage);
        }

        // Set data to corresponding field
        this.eventTitle.setText(event.getName());

        // Check for event owner name
        if (eventOwner == null) {
            this.eventOwner.setText("Owner ID: " + event.getOwnerId());
        } else {
            this.owner = eventOwner;
            this.eventOwner.setText(eventOwner.getName() + " " + eventOwner.getLastName());
        }

        // Set event location
        this.eventLocation.setText(event.getLocation());

        // Set start and end event dates
        try {
            this.eventStartDate.setText(DateHandler.toDateTime(event.getEventStartDate()));
            this.eventEndDate.setText(DateHandler.toDateTime(event.getEventEndDate()));
        } catch (NullPointerException exception) {
            this.eventStartDate.setText(event.getEventStartDate());
            this.eventEndDate.setText(event.getEventEndDate());
        }

        // Set event category
        this.eventCategory.setText(event.getType());

        // Set event assistants
        if (assistants.isEmpty()) {
            this.eventParticipants.setText("0/" + event.getParticipatorsQuantity());
        } else {
            this.eventParticipants.setText(this.assistants.size() + "/" +
                    event.getParticipatorsQuantity());
        }

        // Set event description
        this.eventDescription.setText(event.getDescription());

        // Check if logged in user is the owner
        if (Objects.equals(owner != null ? owner.getId() : null, loggedInUser.getId())) {
            this.editButton.setVisibility(View.VISIBLE);
            this.deleteButton.setVisibility(View.VISIBLE);
            this.joinButton.setVisibility(View.GONE);
        }

        // Check if there are still places to assist
        if(assistants.size() < event.getParticipatorsQuantity()
                && !Objects.equals(owner.getId(), loggedInUser.getId())) {
            // Check if logged in user is a participant
            if (assistants.stream().anyMatch(assistance ->
                    assistance.getAssistantID() == loggedInUser.getId())) {
                // Logged in user is a participant
                this.joinButton.setVisibility(View.GONE);
                this.leaveButton.setVisibility(View.VISIBLE);
            } else {
                this.joinButton.setVisibility(View.VISIBLE);
                this.leaveButton.setVisibility(View.GONE);
            }
        } else {
            this.joinButton.setVisibility(View.GONE);
        }
    }

    private void getEventOwner(int userID) {
        this.apiManager.getUserById(this.authenticationToken.getAccessToken(), userID,
                new Callback<ArrayList<User>>() {
                    @Override
                    public void onResponse(@NonNull Call<ArrayList<User>> call,
                                           @NonNull Response<ArrayList<User>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                // Get event owner
                                User owner = response.body().get(0);

                                // Update event details UI
                                updateEventDetailsUI(event, owner);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArrayList<User>> call,
                                          @NonNull Throwable t) {}
                });
    }

    private void getEventParticipants(int eventID) {
        this.apiManager.getEventAssistants(this.authenticationToken.getAccessToken(), eventID,
                new Callback<ArrayList<Assistance>>() {
                    @Override
                    public void onResponse(@NonNull Call<ArrayList<Assistance>> call,
                                           @NonNull Response<ArrayList<Assistance>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                // Get list of assistance
                                assistants = response.body();

                                // Update event details UI
                                updateEventDetailsUI(event, owner);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArrayList<Assistance>> call,
                                          @NonNull Throwable t) {
                    }
                });
    }

    private void deleteEvent() {
        this.apiManager.deleteEvent(this.authenticationToken.getAccessToken(), event.getId(),
                new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Notification.showDialogNotification(getContext(),
                                    getText(R.string.eventDeletedSuccessfully).toString());

                            // Redirect user to previous page
                            navigateBack();
                        } else {
                            Notification.showDialogNotification(getContext(),
                                    getText(R.string.serverConnectionFailed).toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Notification.showDialogNotification(getContext(),
                                getText(R.string.cannotConnectToServerError).toString());
                    }
                });
    }

    private void editEvent() {
        /*requireActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.home_fragment_container, new EditUserInfoFragment()).commit();*/
    }

    private void joinEvent() {
        // Get user ID of the logged in user
        final int loggedInUserID = this.sharedPrefs.getUser().getId();

        // Attend to event
        this.apiManager.attendEvent(this.authenticationToken.getAccessToken(), loggedInUserID,
                event.getId(), new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Notification.showDialogNotification(getContext(),
                                    getText(R.string.joinedEventSuccessfully).toString());

                            // Disable join event button
                            joinButton.setVisibility(View.GONE);

                            // Enable leave event button
                            leaveButton.setVisibility(View.VISIBLE);
                        } else {
                            Notification.showDialogNotification(getContext(),
                                    getText(R.string.serverConnectionFailed).toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Notification.showDialogNotification(getContext(),
                                getText(R.string.cannotConnectToServerError).toString());
                    }
                });
    }

    private void leaveEvent() {
        // Get user ID of the logged in user
        final int loggedInUserID = this.sharedPrefs.getUser().getId();

        // Attend to event
        this.apiManager.unattendEvent(this.authenticationToken.getAccessToken(), loggedInUserID,
                event.getId(), new Callback<QueryResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<QueryResponse> call,
                                           @NonNull Response<QueryResponse> response) {
                        if (response.isSuccessful()) {
                            Notification.showDialogNotification(getContext(),
                                    getText(R.string.leaveEventSuccessfully).toString());

                            // Enable join event button
                            joinButton.setVisibility(View.VISIBLE);

                            // Disable leave event button
                            leaveButton.setVisibility(View.GONE);
                        } else {
                            Notification.showDialogNotification(getContext(),
                                    getText(R.string.serverConnectionFailed).toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<QueryResponse> call, @NonNull Throwable t) {
                        Notification.showDialogNotification(getContext(),
                                getText(R.string.cannotConnectToServerError).toString());
                    }
                });
    }

    private void commentEvent() {

    }

    private void rateEvent() {

    }

    private void navigateBack() {
        if(mustUpdateOnPopBack) {
            requireActivity().getSupportFragmentManager().beginTransaction().
                    replace(R.id.home_fragment_container, new MyEventsTabFragment()).commit();
        } else {
            getParentFragmentManager().popBackStack();
        }
    }
}