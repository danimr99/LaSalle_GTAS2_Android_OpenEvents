package com.openevents.controller.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.openevents.R;

import java.util.Calendar;

public class EventCreationFragment extends Fragment {
    DatePickerDialog startPicker;
    EditText startText;
    DatePickerDialog endPicker;
    EditText endText;
    EditText numberText;

    public EventCreationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_creation, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startText = view.findViewById(R.id.event_start_date_input);
        startText.setInputType(InputType.TYPE_NULL);
        endText = view.findViewById(R.id.event_end_date_input);
        endText.setInputType(InputType.TYPE_NULL);
        numberText = view.findViewById(R.id.event_number_input);
        numberText.setInputType(InputType.TYPE_NULL);

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
}