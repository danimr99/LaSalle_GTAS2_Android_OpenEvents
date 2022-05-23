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
import com.openevents.api.APIManager;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.FriendshipResponse;
import com.openevents.api.responses.UserProfile;
import com.openevents.controller.fragments.AllUsersFragment;
import com.openevents.controller.fragments.FriendRequestsFragment;
import com.openevents.controller.fragments.MyFriendsFragment;
import com.openevents.model.interfaces.OnListEventListener;
import com.openevents.model.interfaces.OnListUserListener;
import com.openevents.utils.Notification;
import com.openevents.utils.SharedPrefs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    // Variables
    private ArrayList<UserProfile> users;
    private OnListUserListener usersListener;
    private String parentFragment;
    private Integer userItemResource;
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;
    private boolean isFriend;

    public UsersAdapter(ArrayList<UserProfile> users, OnListUserListener usersListener, String parentFragment) {
        this.users = users;
        this.usersListener = usersListener;
        this.isFriend = false;

        // Sort users by name and last name
        this.users.sort(Comparator.comparing(UserProfile::getName)
                .thenComparing(UserProfile::getLastName));

        // Get an instance of APIManager and get the user and his/her stats from API
        this.apiManager = APIManager.getInstance();

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

    public void filter(ArrayList<UserProfile> filteredList) {
        this.users = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.userItemResource,
                parent, false);

        // Get instance of SharedPrefs
        this.sharedPrefs = SharedPrefs.getInstance(view.getContext());

        // Get user authentication token
        this.authenticationToken =
                new AuthenticationToken(this.sharedPrefs.getAuthenticationToken());

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
        holder.userName.setText(user.getName() + " " + user.getLastName());
        holder.userEmail.setText(user.getEmail());

        if(this.parentFragment.equals(FriendRequestsFragment.TAG_FRIEND_REQUESTS)) {
            holder.acceptRequest.setOnClickListener(v -> acceptFriendRequest(v, position));
            holder.declineRequest.setOnClickListener(v -> declineFriendRequest(v, position));
        }
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    private void acceptFriendRequest(View view, int itemPosition) {
        // Get user from the item
        final UserProfile profile = this.users.get(itemPosition);

        this.apiManager.acceptFriendRequest(this.authenticationToken.getAccessToken(),
                profile.getId(), new Callback<FriendshipResponse>() {
            @Override
            public void onResponse(@NonNull Call<FriendshipResponse> call,
                                   @NonNull Response<FriendshipResponse> response) {
                if(response.isSuccessful()) {
                    Notification.showDialogNotification(view.getContext(),
                            profile.getName() + " " +
                                    view.getContext().getText(R.string.isNowYourFriend).toString());

                    // Update adapter
                    users.removeIf(userProfile -> userProfile.getId() == profile.getId());
                    notifyItemRemoved(itemPosition);
                } else {
                    Notification.showDialogNotification(view.getContext(),
                            view.getContext().getText(R.string.serverConnectionFailed).toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FriendshipResponse> call, @NonNull Throwable t) {
                Notification.showDialogNotification(view.getContext(),
                        view.getContext().getText(R.string.cannotConnectToServerError).toString());
            }
        });
    }

    private void declineFriendRequest(View view, int itemPosition) {
        // Get user from the item
        final UserProfile profile = this.users.get(itemPosition);

        this.apiManager.declineFriendRequest(this.authenticationToken.getAccessToken(),
                profile.getId(), new Callback<FriendshipResponse>() {
            @Override
            public void onResponse(@NonNull Call<FriendshipResponse> call, @NonNull Response<FriendshipResponse> response) {
                if(response.isSuccessful()) {
                    Notification.showDialogNotification(view.getContext(),
                            view.getContext().getText(R.string.friendRequestDeclined).toString());

                    // Update adapter
                    users.removeIf(userProfile -> userProfile.getId() == profile.getId());
                    notifyItemRemoved(itemPosition);
                } else {
                    Notification.showDialogNotification(view.getContext(),
                            view.getContext().getText(R.string.serverConnectionFailed).toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FriendshipResponse> call, @NonNull Throwable t) {
                Notification.showDialogNotification(view.getContext(),
                        view.getContext().getText(R.string.cannotConnectToServerError).toString());
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // UI Components
        public ImageView userImage;
        public TextView userName;
        public TextView userEmail;
        public FloatingActionButton acceptRequest;
        public FloatingActionButton declineRequest;

        // Variables
        private final OnListUserListener userListener;
        private final String parentFragment;

        public ViewHolder(View view, OnListUserListener userListener, String parentFragment) {
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
                    this.userEmail = view.findViewById(R.id.user_email);
                    break;
                case MyFriendsFragment.TAG_MY_FRIENDS:
                    this.userImage = view.findViewById(R.id.friend_image);
                    this.userName = view.findViewById(R.id.friend_name);
                    this.userEmail = view.findViewById(R.id.friend_email);
                    break;
                case FriendRequestsFragment.TAG_FRIEND_REQUESTS:
                    this.userImage = view.findViewById(R.id.friend_request_image);
                    this.userName = view.findViewById(R.id.friend_request_name);
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
            this.userListener.onUserClicked(getAdapterPosition());
        }
    }
}
