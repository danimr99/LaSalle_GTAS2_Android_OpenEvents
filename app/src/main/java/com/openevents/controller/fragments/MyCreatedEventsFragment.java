package com.openevents.controller.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.openevents.R;

import java.util.ArrayList;

public class MyCreatedEventsFragment extends Fragment {

    public MyCreatedEventsFragment() {
        // Required empty public constructor
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

        // data to populate the RecyclerView with
        ArrayList<Integer> eventsImage = new ArrayList<>();
        eventsImage.add(Color.BLUE);
        eventsImage.add(Color.RED);
        eventsImage.add(Color.BLUE);
        eventsImage.add(Color.RED);
        eventsImage.add(Color.BLUE);
        eventsImage.add(Color.RED);

        ArrayList<String> eventsTitle = new ArrayList<>();
        eventsTitle.add("Evento Palomero para palomas");
        eventsTitle.add("Evento Palomero");
        eventsTitle.add("Evento Palomero para palomas");
        eventsTitle.add("Evento Palomero");
        eventsTitle.add("Evento Palomero para palomas");
        eventsTitle.add("Evento Palomero");

        ArrayList<String> eventsLocation = new ArrayList<>();
        eventsLocation.add("Perú");
        eventsLocation.add("Perú");
        eventsLocation.add("Perú");
        eventsLocation.add("Perú");
        eventsLocation.add("Perú");
        eventsLocation.add("Perú");

        ArrayList<String> eventsDate = new ArrayList<>();
        eventsDate.add("12/12/2020");
        eventsDate.add("12/12/2020");
        eventsDate.add("12/12/2020");
        eventsDate.add("12/12/2020");
        eventsDate.add("12/12/2020");
        eventsDate.add("12/12/2020");


        return view;
    }
}