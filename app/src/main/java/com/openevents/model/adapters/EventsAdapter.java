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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private ArrayList<Event> events;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventTitle;
        public TextView eventLocation;
        public ImageView eventImage;

        public ViewHolder(View view) {
            super(view);

            // Get elements of the view for each item of the RecyclerView
            this.eventTitle = view.findViewById(R.id.event_title);
            this.eventLocation = view.findViewById(R.id.event_location_title);
            this.eventImage = view.findViewById(R.id.event_image);
        }
    }

    public EventsAdapter(ArrayList<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,
                parent, false);

        return new ViewHolder(view);
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
    }

    @Override
    public int getItemCount() {
        return this.events.size();
    }
}
