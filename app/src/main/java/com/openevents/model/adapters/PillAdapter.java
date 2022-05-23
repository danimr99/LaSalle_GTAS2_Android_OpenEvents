package com.openevents.model.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.openevents.R;
import com.openevents.model.interfaces.OnListPillListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PillAdapter extends RecyclerView.Adapter<PillAdapter.ViewHolder> {
    // Variables
    private String[] categories;
    private OnListPillListener pillListener;
    private ArrayList<Boolean> status;

    public PillAdapter(String[] categories, ArrayList<Boolean> status, OnListPillListener pillListener) {
        this.categories = categories;
        this.pillListener = pillListener;
        this.status = status;
    }

    @NonNull
    @Override
    public PillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pill_item,
                parent, false);

        return new PillAdapter.ViewHolder(view, this.pillListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set text to pill
        holder.pill.setText(categories[position]);

        holder.setStatus(this.status.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.length;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // UI Components
        public ExtendedFloatingActionButton pill;

        // Variables
        private final OnListPillListener pillListener;
        private boolean active;

        public ViewHolder(View view, OnListPillListener pillListener) {
            super(view);

            // Get elements of the view for each item of the RecyclerView
            this.pill = itemView.findViewById(R.id.pill_button);

            // Get event listener
            this.pillListener = pillListener;

            // Set initial status color
            if(active) {
                this.pill.setBackgroundColor(itemView.getResources().
                        getColor(R.color.clear_blue,
                                itemView.getContext().getTheme()));
            } else {
                this.pill.setBackgroundColor(itemView.getResources().
                        getColor(R.color.secondary_background,
                                itemView.getContext().getTheme()));
            }

            // Set on click listener to the extended fab
            this.pill.setOnClickListener(v -> {
                this.toggleStatus();
                this.updateColor();
                this.pillListener.onPillClicked(getAdapterPosition(), active);
            });
        }

        private void toggleStatus() {
            this.active = !this.active;
        }

        private void updateColor() {
            if(this.active) {
                // Set color to clicked status
                this.pill.setBackgroundColor(itemView.getResources().getColor(R.color.clear_blue,
                        itemView.getContext().getTheme()));
            } else {
                // Set color to not clicked status
                this.pill.setBackgroundColor(itemView.getResources().
                        getColor(R.color.secondary_background,
                        itemView.getContext().getTheme()));
            }
        }

        public void setStatus(boolean status) {
            this.active = status;
            this.updateColor();
        }
    }
}