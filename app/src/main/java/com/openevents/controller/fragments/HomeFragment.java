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
import com.openevents.model.adapters.RecyclerViewAdapter;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener {
    private RecyclerViewAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // data to populate the RecyclerView with
        ArrayList<Integer> eventsImage = new ArrayList<>();
        eventsImage.add(Color.BLUE);
        eventsImage.add(Color.RED);

        ArrayList<String> eventsTitle = new ArrayList<>();
        eventsTitle.add("Evento Palomero para palomas");
        eventsTitle.add("Evento Palomero para palomas");

        // set up the horizontal RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.EventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new RecyclerViewAdapter(getContext(), eventsImage, eventsTitle);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getContext(), "You clicked " + adapter.getItem(position), Toast.LENGTH_SHORT).show();
    }
}