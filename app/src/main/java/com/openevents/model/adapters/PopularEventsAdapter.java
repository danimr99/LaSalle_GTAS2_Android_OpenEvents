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

public class PopularEventsAdapter extends RecyclerView.Adapter<PopularEventsAdapter.ViewHolder> {
    private static final int TOP_10_POPULAR_EVENTS = 10;
    private ArrayList<Event> popularEvents;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventTitle;
        public ImageView eventImage;

        public ViewHolder(View view) {
            super(view);

            // Get elements of the view for each item of the RecyclerView
            this.eventTitle = view.findViewById(R.id.event_card_title);
            this.eventImage = view.findViewById(R.id.event_card_image);
        }
    }

    public PopularEventsAdapter(ArrayList<Event> popularEvents) {
        this.popularEvents = popularEvents;
    }

    @NonNull
    @Override
    public PopularEventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card_item,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularEventsAdapter.ViewHolder holder, int position) {
        final Event item = this.popularEvents.get(position);

        // Set image from the event
        Picasso.get()
                .load(item.getImage())
                .placeholder(R.drawable.event_placeholder)
                .error(R.drawable.event_placeholder)
                .into(holder.eventImage);

        // Set event name
        holder.eventTitle.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return TOP_10_POPULAR_EVENTS;
    }
}
