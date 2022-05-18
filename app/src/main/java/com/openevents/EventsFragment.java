package com.openevents;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsFragment extends Fragment implements EventsRecyclerViewAdapter.ItemClickListener{
    private EventsRecyclerViewAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EventsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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