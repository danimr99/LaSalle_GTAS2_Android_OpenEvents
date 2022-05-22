package com.openevents.model.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openevents.R;
import com.openevents.api.responses.Event;
import com.openevents.controller.fragments.EventDetailsFragment;
import com.openevents.model.interfaces.OnListItemListener;
import com.openevents.utils.DateParser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PopularEventsAdapter extends RecyclerView.Adapter<PopularEventsAdapter.ViewHolder> {
    // Constants
    private static final int TOP_10_POPULAR_EVENTS = 10;

    // Variables
    private ArrayList<Event> popularEvents;
    private final OnListItemListener eventListener;


    public PopularEventsAdapter(ArrayList<Event> popularEvents, OnListItemListener eventListener) {
        this.popularEvents = popularEvents;
        this.eventListener = eventListener;
    }

    public void filter(ArrayList<Event> filteredList) {
        this.popularEvents = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PopularEventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card_item,
                parent, false);

        return new ViewHolder(view, this.eventListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularEventsAdapter.ViewHolder holder, int position) {
        final Event item = this.popularEvents.get(position);

        // Set image from the event
        if(item.getImage() != null && item.getImage().trim().length() != 0) {
            Picasso.get()
                    .load(item.getImage())
                    .placeholder(R.drawable.event_placeholder)
                    .error(R.drawable.event_placeholder)
                    .into(holder.eventImage);
        } else {
            Picasso.get().load(R.drawable.event_placeholder).into(holder.eventImage);
        }

        // Set event name
        holder.eventStartDate.setText(DateParser.toDate(item.getEventStartDate()));

        // Set event name
        holder.eventTitle.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return Math.min(this.popularEvents.size(), TOP_10_POPULAR_EVENTS);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView eventTitle;
        public TextView eventStartDate;
        public ImageView eventImage;

        private final OnListItemListener eventListener;

        public ViewHolder(View view, OnListItemListener eventListener) {
            super(view);

            // Get elements of the view for each item of the RecyclerView
            this.eventTitle = view.findViewById(R.id.event_card_title);
            this.eventStartDate = view.findViewById(R.id.event_card_start_date);
            this.eventImage = view.findViewById(R.id.event_card_image);

            // Get event listener
            this.eventListener = eventListener;

            // Configure on click listener of the view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.eventListener.onListItemClicked(getAdapterPosition());
        }
    }
}
