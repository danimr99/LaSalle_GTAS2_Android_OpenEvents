package com.openevents.model.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openevents.R;
import com.openevents.api.responses.Event;
import com.openevents.model.interfaces.OnEventListener;
import com.openevents.utils.DateParser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private final ArrayList<Event> events;
    private OnEventListener eventListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView eventTitle;
        public TextView eventLocation;
        public TextView eventStartDate;
        public ImageView eventImage;

        private OnEventListener eventListener;

        public ViewHolder(View view, OnEventListener eventListener) {
            super(view);

            // Get elements of the view for each item of the RecyclerView
            this.eventTitle = view.findViewById(R.id.event_title);
            this.eventLocation = view.findViewById(R.id.event_location);
            this.eventStartDate = view.findViewById(R.id.event_start_date);
            this.eventImage = view.findViewById(R.id.event_image);

            // Get event listener
            this.eventListener = eventListener;

            // Configure on click listener of the view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.eventListener.onEventClick(getAdapterPosition());
        }
    }

    public EventsAdapter(ArrayList<Event> events, OnEventListener eventListener) {
        this.events = events;
        this.eventListener = eventListener;
    }

    @NonNull
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,
                parent, false);

        return new ViewHolder(view, this.eventListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.ViewHolder holder, int position) {
        final Event item = this.events.get(position);

        // Set image from the event
        Picasso.get()
                .load(item.getImage())
                .placeholder(R.drawable.event_placeholder)
                .error(R.drawable.event_placeholder)
                .into(holder.eventImage);

        // Set event name
        holder.eventTitle.setText(item.getName());

        // Set event location
        holder.eventLocation.setText(item.getLocation());

        // Set event start date
        holder.eventStartDate.setText(DateParser.toDate(item.getEventStartDate()));
    }

    @Override
    public int getItemCount() {
        return this.events.size();
    }
}