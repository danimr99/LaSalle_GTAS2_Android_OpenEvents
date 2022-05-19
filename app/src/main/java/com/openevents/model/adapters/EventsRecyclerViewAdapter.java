package com.openevents.model.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openevents.R;

import java.util.List;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {

    private List<Integer> mImage;
    private List<String> mTitle;
    private List<String> mLocation;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;


    public EventsRecyclerViewAdapter(Context context, List<Integer> images, List<String> titles, List<String> locations) {
        this.mInflater = LayoutInflater.from(context);
        this.mImage = images;
        this.mTitle = titles;
        this.mLocation = locations;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.events_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int i = mImage.get(position);
        String title = mTitle.get(position);
        String location = mLocation.get(position);
        holder.myEventsView.setBackgroundColor(i);
        holder.myTextView.setText(title);
        holder.myLocationView.setText(location);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mTitle.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View myEventsView;
        TextView myTextView;
        TextView myLocationView;

        ViewHolder(View itemView) {
            super(itemView);
            myEventsView = itemView.findViewById(R.id.EventsRecyclerViewEventImage);
            myTextView = itemView.findViewById(R.id.EventsRecyclerViewEventTitle);
            myLocationView = itemView.findViewById(R.id.EventsRecyclerViewEventLocation);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mTitle.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}