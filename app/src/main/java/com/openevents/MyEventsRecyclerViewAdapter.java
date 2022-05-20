package com.openevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyEventsRecyclerViewAdapter extends RecyclerView.Adapter<MyEventsRecyclerViewAdapter.ViewHolder> {

    private List<Integer> mImage;
    private List<String> mTitle;
    private List<String> mLocation;
    private List<String> mDate;
    private LayoutInflater mInflater;
    private MyEventsRecyclerViewAdapter.ItemClickListener mClickListener;

    public MyEventsRecyclerViewAdapter(Context context, List<Integer> images, List<String> titles, List<String> locations, List<String> dates) {
        this.mInflater = LayoutInflater.from(context);
        this.mImage = images;
        this.mTitle = titles;
        this.mLocation = locations;
        this.mDate = dates;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public MyEventsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.events_recyclerview_item, parent, false);
        return new MyEventsRecyclerViewAdapter.ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull MyEventsRecyclerViewAdapter.ViewHolder holder, int position) {
        int i = mImage.get(position);
        String title = mTitle.get(position);
        String location = mLocation.get(position);
        String date = mDate.get(position);
        holder.myEventsView.setBackgroundColor(i);
        holder.myTextView.setText(title);
        holder.myLocationView.setText(location);
        holder.myDateView.setText(date);
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
        TextView myDateView;

        ViewHolder(View itemView) {
            super(itemView);
            myEventsView = itemView.findViewById(R.id.EventsRecyclerViewEventImage);
            myTextView = itemView.findViewById(R.id.EventsRecyclerViewEventTitle);
            myLocationView = itemView.findViewById(R.id.EventsRecyclerViewEventLocation);
            myDateView = itemView.findViewById(R.id.EventsRecyclerViewEventDate);
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
    public void setClickListener(MyEventsRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}