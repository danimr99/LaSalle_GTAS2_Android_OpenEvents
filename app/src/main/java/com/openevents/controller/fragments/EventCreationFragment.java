package com.openevents.controller.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.openevents.R;
import com.openevents.controller.components.ImageSelectorFragment;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventCreationFragment extends Fragment {
    // UI Components
    private ImageSelectorFragment fragment;
    private ImageView profileImage;
    private DatePickerDialog startPicker;
    private EditText startText;
    private DatePickerDialog endPicker;
    private EditText endText;
    private EditText numberText;

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
        this.startText = view.findViewById(R.id.event_start_date_input);
        this.startText.setInputType(InputType.TYPE_NULL);
        this.endText = view.findViewById(R.id.event_end_date_input);
        this.endText.setInputType(InputType.TYPE_NULL);
        this.numberText = view.findViewById(R.id.event_number_input);
        this.numberText.setInputType(InputType.TYPE_NULL);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        startText.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            startPicker = new DatePickerDialog(getContext(),
                    (view12, year12, monthOfYear, dayOfMonth) -> startText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year12), year, month, day);
            startPicker.show();
        });

        endText.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);

            // date picker dialog
            endPicker = new DatePickerDialog(getContext(),
                    (view1, year1, monthOfYear, dayOfMonth) -> endText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1), year, month, day);
            endPicker.show();
        });

        numberText.setOnClickListener(v -> {
            final NumberPicker numberPicker = new NumberPicker(getContext());
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(10000);
            numberPicker.setValue(1);
            numberPicker.setWrapSelectorWheel(false);
            numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> numberText.setText(String.valueOf(newVal)));
            new AlertDialog.Builder(getContext())
                    .setTitle("Number of participants")
                    .setView(numberPicker)
                    .setPositiveButton("OK", (dialog, which) -> numberText.setText(String.valueOf(numberPicker.getValue())))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                    .show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get profile image view once the fragment has been loaded
        View fragmentView = this.fragment.getView();
        this.profileImage = fragmentView != null ?
                fragmentView.findViewById(R.id.image_selector) : null;
    }
}