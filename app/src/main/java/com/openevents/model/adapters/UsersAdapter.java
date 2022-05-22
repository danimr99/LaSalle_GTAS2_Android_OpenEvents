package com.openevents.model.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openevents.R;
import com.openevents.api.responses.UserProfile;
import com.openevents.controller.fragments.AllUsersFragment;
import com.openevents.controller.fragments.FriendRequestsFragment;
import com.openevents.controller.fragments.MyFriendsFragment;
import com.openevents.model.interfaces.OnListItemListener;
import com.openevents.utils.ToastNotification;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    // Variables
    private ArrayList<UserProfile> users;
    private OnListItemListener usersListener;
    private String parentFragment;
    private Integer userItemResource;

    public UsersAdapter(ArrayList<UserProfile> users, OnListItemListener usersListener, String parentFragment) {
        this.users = users;
        this.usersListener = usersListener;

        // Check which user item layout must be used
        this.parentFragment = parentFragment;
        switch (parentFragment) {
            case AllUsersFragment.TAG_ALL_USERS:
                this.userItemResource = R.layout.user_item;
                break;
            case MyFriendsFragment.TAG_MY_FRIENDS:
                this.userItemResource = R.layout.friend_item;
                break;
            case FriendRequestsFragment.TAG_FRIEND_REQUESTS:
                this.userItemResource = R.layout.friend_request_user_item;
                break;
        }
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.userItemResource,
                parent, false);
        return new ViewHolder(view, this.usersListener, this.parentFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {
        final UserProfile user = this.users.get(position);

        // Set image from the user
        if(user.getImage() != null && user.getImage().trim().length() != 0) {
            Picasso.get()
                    .load(user.getImage())
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder)
                    .into(holder.userImage);
        } else {
            Picasso.get().load(R.drawable.user_placeholder).into(holder.userImage);
        }

        // Set data to corresponding field
        holder.userName.setText(user.getName());
        holder.userLastName.setText(user.getLastName());
        holder.userEmail.setText(user.getEmail());

        switch (this.parentFragment) {
            case AllUsersFragment.TAG_ALL_USERS:
                break;
            case MyFriendsFragment.TAG_MY_FRIENDS:
                holder.deleteFriend.setOnClickListener(v -> deleteFriend(v));
                break;
            case FriendRequestsFragment.TAG_FRIEND_REQUESTS:
                holder.acceptRequest.setOnClickListener(v -> acceptFriendRequest(v));
                holder.declineRequest.setOnClickListener(v -> declineFriendRequest(v));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    private void deleteFriend(View view) {
        ToastNotification.showError(view.getContext(), "Delete friend");
    }

    private void acceptFriendRequest(View view) {
        ToastNotification.showError(view.getContext(), "Accept friend request");
    }

    private void declineFriendRequest(View view) {
        ToastNotification.showError(view.getContext(), "Delete friend request");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // UI Components
        public ImageView userImage;
        public TextView userName;
        public TextView userLastName;
        public TextView userEmail;
        public FloatingActionButton deleteFriend;
        public FloatingActionButton acceptRequest;
        public FloatingActionButton declineRequest;

        // Variables
        private final OnListItemListener userListener;
        private final String parentFragment;

        public ViewHolder(View view, OnListItemListener userListener, String parentFragment) {
            super(view);

            // Get user listener
            this.userListener = userListener;

            // Get parent fragment
            this.parentFragment = parentFragment;

            // Get elements of the view for each item of the RecyclerView
            switch (this.parentFragment) {
                case AllUsersFragment.TAG_ALL_USERS:
                    this.userImage = view.findViewById(R.id.user_image);
                    this.userName = view.findViewById(R.id.user_name);
                    this.userLastName = view.findViewById(R.id.user_last_name);
                    this.userEmail = view.findViewById(R.id.user_email);
                    break;
                case MyFriendsFragment.TAG_MY_FRIENDS:
                    this.userImage = view.findViewById(R.id.friend_image);
                    this.userName = view.findViewById(R.id.friend_name);
                    this.userLastName = view.findViewById(R.id.friend_last_name);
                    this.userEmail = view.findViewById(R.id.friend_email);
                    this.deleteFriend = view.findViewById(R.id.delete_friend_button);
                    break;
                case FriendRequestsFragment.TAG_FRIEND_REQUESTS:
                    this.userImage = view.findViewById(R.id.friend_request_image);
                    this.userName = view.findViewById(R.id.friend_request_name);
                    this.userLastName = view.findViewById(R.id.friend_request_last_name);
                    this.userEmail = view.findViewById(R.id.friend_request_email);
                    this.acceptRequest = view.findViewById(R.id.accept_request_button);
                    this.declineRequest = view.findViewById(R.id.decline_request_button);
                    break;
            }

            // Configure on click listener of the view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.userListener.onListItemClicked(getAdapterPosition());
        }
    }
}
