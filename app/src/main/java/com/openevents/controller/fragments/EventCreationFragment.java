package com.openevents.controller.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.google.android.material.textfield.TextInputLayout;
import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.requests.CreatedEvent;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.Event;
import com.openevents.constants.Constants;
import com.openevents.controller.components.ImageSelectorFragment;
import com.openevents.utils.DateHandler;
import com.openevents.utils.Notification;
import com.openevents.utils.Numbers;
import com.openevents.utils.SharedPrefs;

import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventCreationFragment extends Fragment {
    // UI Components
    private ImageView backArrow;
    private ImageSelectorFragment fragment;
    private ImageView profileImage;
    private TextInputLayout titleLayout;
    private EditText eventTitle;
    private TextInputLayout locationLayout;
    private EditText eventLocation;
    private TextInputLayout startDateLayout;
    private EditText startDateText;
    private TextInputLayout endDateLayout;
    private EditText endDateText;
    private TextInputLayout numberParticipantsLayout;
    private EditText numberParticipants;
    private TextInputLayout categoryLayout;
    private EditText category;
    private TextInputLayout descriptionLayout;
    private EditText eventDescription;
    private Button createEventButton;

    // Variables
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;

    public EventCreationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_creation, container, false);

        // Create an instance of APIManager and SharedPreferences
        this.apiManager = APIManager.getInstance();
        this.sharedPrefs = SharedPrefs.getInstance(view.getContext());

        // Get user authentication token
        this.authenticationToken =
                new AuthenticationToken(this.sharedPrefs.getAuthenticationToken());

        // Create ImageSelectorFragment
        FragmentManager fm = this.getChildFragmentManager();
        this.fragment = (ImageSelectorFragment) fm.findFragmentById(R.id.create_event_image_selector);

        // Inflate view with the ImageSelectorFragment
        if (this.fragment == null) {
            this.fragment = new ImageSelectorFragment(false);
            fm.beginTransaction().add(R.id.create_event_image_selector, this.fragment).commit();
        }

        // Get all components from view
        this.backArrow = view.findViewById(R.id.create_event_back_arrow);
        this.titleLayout = view.findViewById(R.id.event_title_input_layout);
        this.eventTitle = view.findViewById(R.id.event_title_input);
        this.locationLayout = view.findViewById(R.id.event_location_input_layout);
        this.eventLocation = view.findViewById(R.id.event_location_input);
        this.startDateLayout = view.findViewById(R.id.event_start_date_input_layout);
        this.endDateLayout = view.findViewById(R.id.event_end_date_input_layout);
        this.numberParticipantsLayout = view.findViewById(R.id.event_participants_input_layout);
        this.categoryLayout = view.findViewById(R.id.event_type_input_layout);
        this.descriptionLayout = view.findViewById(R.id.event_description_input_layout);
        this.eventDescription = view.findViewById(R.id.event_description_input);
        this.createEventButton = view.findViewById(R.id.create_event_button);

        // Configuration to select a start date
        this.startDateText = view.findViewById(R.id.event_start_date_input);
        this.startDateText.setInputType(InputType.TYPE_NULL);

        // Configuration to select a start date
        this.endDateText = view.findViewById(R.id.event_end_date_input);
        this.endDateText.setInputType(InputType.TYPE_NULL);

        // Configuration to select a category
        this.category = view.findViewById(R.id.event_type_input);
        this.category.setInputType(InputType.TYPE_NULL);

        // Configuration to select a max number of participants
        this.numberParticipants = view.findViewById(R.id.event_number_participants_input);
        this.numberParticipants.setInputType(InputType.TYPE_NULL);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set on click listener to the back arrow
        this.backArrow.setOnClickListener(v -> navigateBack());

        // Set on click listener to the start date input
        this.startDateText.setOnClickListener(v -> showDateTimePicker(startDateText));

        // Set on click listener to the start date input
        this.endDateText.setOnClickListener(v -> showDateTimePicker(endDateText));

        // Set on click listener to the participants number
        this.numberParticipants.setOnClickListener(v -> {
            final NumberPicker numberPicker = new NumberPicker(getContext());

            // Configure number picker
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(10000);
            numberPicker.setValue(1);
            numberPicker.setWrapSelectorWheel(false);

            // Set on value changed listener
            numberPicker.setOnValueChangedListener((picker, oldValue, newValue) ->
                    numberParticipants.setText(String.valueOf(newValue)));

            // Show alert dialog to pick the number of participants
            new AlertDialog.Builder(getContext()).
                    setTitle(getText(R.string.eventNumberOfParticipantsTitle)).
                    setView(numberPicker).
                    setPositiveButton(R.string.acceptLabel, ((dialog, which) ->
                            numberParticipants.setText(String.valueOf(numberPicker.getValue())))).
                    setNegativeButton(R.string.cancelLabel, ((dialog, which) -> dialog.cancel())).
                    show();
        });

        // Set on click listener to the participants number
        this.category.setOnClickListener(v -> {
            final NumberPicker categoryPicker = new NumberPicker(getContext());

            // Configure number picker
            categoryPicker.setMinValue(0);
            categoryPicker.setMaxValue(Constants.CATEGORIES.length - 1);
            categoryPicker.setDisplayedValues(Constants.CATEGORIES);
            categoryPicker.setWrapSelectorWheel(true);

            // Set on value changed listener
            categoryPicker.setOnValueChangedListener((picker, oldValue, newValue) ->
                    category.setText(Constants.CATEGORIES[newValue]));

            // Show alert dialog to pick the number of participants
            new AlertDialog.Builder(getContext()).
                    setTitle(getText(R.string.eventTypeTitle)).
                    setView(categoryPicker).
                    setPositiveButton(R.string.acceptLabel, ((dialog, which) ->
                            numberParticipants.setText(String.valueOf(categoryPicker.getValue())))).
                    setNegativeButton(R.string.cancelLabel, ((dialog, which) -> dialog.cancel())).
                    show();
        });

        // Set on click listener to the create event button
        this.createEventButton.setOnClickListener(v -> createEvent());
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get profile image view once the fragment has been loaded
        View fragmentView = this.fragment.getView();
        this.profileImage = fragmentView != null ?
                fragmentView.findViewById(R.id.image_selector) : null;
    }

    public void showDateTimePicker(EditText input) {
        final Calendar currentDate = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (datePicker, year, monthOfYear, dayOfMonth) -> {

                    date.set(year, monthOfYear, dayOfMonth);
                    new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);

                        input.setText(DateHandler.convertDateTimeFromPicker(date.getTime()));
                    }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();

                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private boolean checkEvent(String image, String title, String location, String startDate,
                               String endDate, int numAssistants, String category,
                               String description) {
        boolean isAnyFieldBlank = false, isValidNumberAssistants = true, areValidEventDates = false;

        // Reset all errors
        this.titleLayout.setError(null);
        this.locationLayout.setError(null);
        this.startDateLayout.setError(null);
        this.endDateLayout.setError(null);
        this.numberParticipantsLayout.setError(null);
        this.categoryLayout.setError(null);
        this.descriptionLayout.setError(null);

        // Check event image
        if (image.isEmpty()) {
            isAnyFieldBlank = true;
        }

        // Check event title
        if (title.isEmpty()) {
            isAnyFieldBlank = true;
            this.titleLayout.setError(getText(R.string.requiredFieldError));
        }

        // Check event location
        if (location.length() < Constants.MIN_LENGTH_EVENT_LOCATION) {
            isAnyFieldBlank = true;
            this.locationLayout.setError(getText(R.string.locationMinLengthError));
        }

        // Check event start date
        if (startDate.isEmpty()) {
            isAnyFieldBlank = true;
            this.startDateLayout.setError(getText(R.string.requiredFieldError));
        }

        // Check event end date
        if (endDate.isEmpty()) {
            isAnyFieldBlank = true;
            this.endDateLayout.setError(getText(R.string.requiredFieldError));
        }

        // Check if start and end dates are valid (start is before end)
        if(!startDate.isEmpty() && !endDate.isEmpty()) {
            areValidEventDates = DateHandler.compareDates(startDate, endDate);

            if(!areValidEventDates) {
                this.startDateLayout.setError(getText(R.string.endDateBeforeStartDateEventError));
                this.endDateLayout.setError(getText(R.string.endDateBeforeStartDateEventError));
            }
        }

        // Check event number of assistants
        if (numAssistants < Constants.MIN_NUMBER_ASSISTANTS_EVENT) {
            isValidNumberAssistants = false;
            this.numberParticipantsLayout.setError(getText(R.string.minAssistantsEventError));
        }

        // Check event category
        if(category.isEmpty()) {
            isAnyFieldBlank = true;
            this.categoryLayout.setError(getText(R.string.requiredFieldError));
        }

        // Check event description
        if(description.isEmpty()) {
            isAnyFieldBlank = true;
            this.descriptionLayout.setError(getText(R.string.requiredFieldError));
        }

        return !isAnyFieldBlank && isValidNumberAssistants && areValidEventDates;
    }

    private void createEvent() {
        // Get all inputs fields
        String image = Constants.EXAMPLE_EVENT_IMAGES_URL[Numbers.generateRandomNumber(0,
                Constants.EXAMPLE_EVENT_IMAGES_URL.length - 1)];
        String title = this.eventTitle.getText().toString();
        String location = this.eventLocation.getText().toString();
        String startDate = DateHandler.toAPI(this.startDateText.getText().toString());
        String endDate = DateHandler.toAPI(this.endDateText.getText().toString());

        int numAssistants;
        try {
            numAssistants = Integer.parseInt(this.numberParticipants.getText().toString());
        } catch (NumberFormatException exception) {
            numAssistants = 0;
        }

        String category = this.category.getText().toString();
        String description = this.eventDescription.getText().toString();

        if (this.checkEvent(image, title, location, startDate, endDate, numAssistants, category,
                description)) {
            // Create event
            CreatedEvent event = new CreatedEvent(title, image, location, description, startDate,
                    endDate, numAssistants, category);

            // Send event to API
            this.apiManager.createEvent(this.authenticationToken.getAccessToken(), event, new Callback<Event>() {
                @Override
                public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                    if(response.isSuccessful()) {
                        if(response.body() != null) {
                            Notification.showDialogNotification(getContext(),
                                    getText(R.string.eventCreatedSuccessfully).toString());

                            // Return to MyEvents
                            navigateBack();
                        }
                    } else {
                        Notification.showDialogNotification(getContext(),
                                getText(R.string.serverConnectionFailed).toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                    Notification.showDialogNotification(getContext(),
                            getText(R.string.cannotConnectToServerError).toString());
                }
            });
        }
    }

    private void navigateBack() {
        requireActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.home_fragment_container, new MyEventsTabFragment()).commit();
    }
}