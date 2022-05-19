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

import com.openevents.model.adapters.EventsRecyclerViewAdapter;
import com.openevents.R;

import java.util.ArrayList;


public class EventsFragment extends Fragment implements EventsRecyclerViewAdapter.ItemClickListener {
    private EventsRecyclerViewAdapter adapter;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_events, container, false);

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

        // set up the vertical RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.AllEventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new EventsRecyclerViewAdapter(getContext(), eventsImage, eventsTitle, eventsLocation);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getContext(), "You clicked " + adapter.getItem(position), Toast.LENGTH_SHORT).show();
    }
}