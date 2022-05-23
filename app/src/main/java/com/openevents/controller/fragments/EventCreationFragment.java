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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.openevents.R;
import com.openevents.constants.Constants;
import com.openevents.controller.components.ImageSelectorFragment;
import com.openevents.utils.DateParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventCreationFragment extends Fragment {
    // UI Components
    private ImageSelectorFragment fragment;
    private ImageView profileImage;
    private EditText eventTitle;
    private EditText eventLocation;
    private DatePickerDialog startPickerDate;
    private TimePickerDialog startPickerTime;
    private EditText startDateText;
    private DatePickerDialog endPickerDate;
    private TimePickerDialog endPickerTime;
    private EditText endDateText;
    private EditText numberParticipants;
    private EditText category;
    private EditText eventDescription;
    private Button createEventButton;

    // Variables

    public EventCreationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_creation, container, false);

        // Create ImageSelectorFragment
        FragmentManager fm = this.getChildFragmentManager();
        this.fragment = (ImageSelectorFragment) fm.findFragmentById(R.id.create_event_image_selector);

        // Inflate view with the ImageSelectorFragment
        if (this.fragment == null) {
            this.fragment = new ImageSelectorFragment(false);
            fm.beginTransaction().add(R.id.create_event_image_selector, this.fragment).commit();
        }

        // Get all components from view
        this.eventTitle = view.findViewById(R.id.event_title_input);
        this.eventLocation = view.findViewById(R.id.event_location_input);
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

        // Set on click listener to the start date input
        this.startDateText.setOnClickListener(v -> showDateTimePicker(startDateText));

        // Set on click listener to the start date input
        this.endDateText.setOnClickListener(v -> showDateTimePicker(endDateText));

        // Set on click listener to the participants number
        this.numberParticipants.setOnClickListener(v ->{
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

                input.setText(DateParser.convertDateTimeFromPicker(date.getTime()));
            },currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();

        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void createEvent() {

    }
}