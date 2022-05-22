package com.openevents.controller.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.FriendshipResponse;
import com.openevents.api.responses.User;
import com.openevents.api.responses.UserProfile;
import com.openevents.api.responses.UserStats;
import com.openevents.utils.SharedPrefs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileFragment extends Fragment {
    // Components UI
    private ImageView backArrow;
    private ImageView profileImage;
    private TextView profileName;
    private TextView profileEmail;
    private TextView profileAverageScore;
    private TextView profileNumberOfComments;
    private TextView profilePercentageLessComments;
    private TextView sendFriendRequestButton;

    // Variables
    private final UserProfile user;
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;
    private boolean isFriend;
    private boolean existsFriendRequestSentByProfileUser;

    public UserProfileFragment(UserProfile user) {
        this.user = user;

        this.isFriend = false;
        this.existsFriendRequestSentByProfileUser = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Get instance of SharedPrefs
        this.sharedPrefs = SharedPrefs.getInstance(getContext());

        // Get user authentication token
        this.authenticationToken =
                new AuthenticationToken(this.sharedPrefs.getAuthenticationToken());

        // Get an instance of APIManager and get the user and his/her stats from API
        this.apiManager = APIManager.getInstance();
        this.getUserStats();
        this.checkFriendshipStatus();

        // Get all components from view
        this.backArrow = view.findViewById(R.id.profile_back_arrow);
        this.profileImage = view.findViewById(R.id.profile_image);
        this.profileName = view.findViewById(R.id.profile_name);
        this.profileEmail = view.findViewById(R.id.profile_email);
        this.profileAverageScore = view.findViewById(R.id.profile_average_score);
        this.profileNumberOfComments = view.findViewById(R.id.profile_number_comments);
        this.profilePercentageLessComments = view.findViewById(R.id.profile_percentage_users_less_comments);
        this.sendFriendRequestButton = view.findViewById(R.id.send_friend_request_button);

        // Set image from the user
        Picasso.get()
                .load(user.getImage())
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder)
                .into(this.profileImage);

        // Set data to each component
        this.profileName.setText(this.user.getName() + " " + this.user.getLastName());
        this.profileEmail.setText(this.user.getEmail());

        // Configure back arrow on click
        this.backArrow.setOnClickListener(v -> this.navigateBack());

        // Configure friendship buttons
        this.sendFriendRequestButton.setOnClickListener(v -> this.sendFriendRequest());

        return view;
    }

    private void getUserStats() {
        this.apiManager.getUserStats(this.authenticationToken.getAccessToken(), this.user.getId(),
                new Callback<UserStats>() {
                    @Override
                    public void onResponse(@NonNull Call<UserStats> call, @NonNull Response<UserStats> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                UserStats stats = response.body();

                                // Update UI with user stats
                                updateUserStatsUI(stats);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserStats> call, @NonNull Throwable t) {
                    }
                });
    }

    private void updateUserStatsUI(UserStats stats) {
        String averageScore = stats.getAverageScore();
        String percentageCommentersBelow = stats.getPercentageCommentersBelow();

        // Check if user has average score
        if (averageScore == null) {
            averageScore = (String) getText(R.string.noAverageScore);
            percentageCommentersBelow = (String) getText(R.string.noPercentageCommentersBelow);
        }

        this.profileAverageScore.setText(averageScore);
        this.profileNumberOfComments.setText(stats.getNumberOfComments());
        this.profilePercentageLessComments.setText(percentageCommentersBelow);
    }

    private void checkFriendshipStatus() {
        // Get logged in user ID
        int loggedInUserID = this.sharedPrefs.getUser().getId();

        // Check if the user from the profile is a friend of the user logged in
        this.apiManager.getUserFriends(this.authenticationToken.getAccessToken(), loggedInUserID,
                new Callback<ArrayList<User>>() {
                    @Override
                    public void onResponse(@NonNull Call<ArrayList<User>> call,
                                           @NonNull Response<ArrayList<User>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                // Get all friends from the logged in user
                                ArrayList<User> friends = response.body();

                                // Check if exists the user from the profile in the list of friends
                                for (User friend : friends) {
                                    if (friend.getId().equals(user.getId())) {
                                        isFriend = true;
                                    }
                                }

                                // Set visibility off to send request button if profile user is a friend
                                if (isFriend) {
                                    sendFriendRequestButton.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArrayList<User>> call, @NonNull Throwable t) {}
                });

        // Check if the user from the profile has sent a friend request to the user logged in
        this.apiManager.getFriendRequests(this.authenticationToken.getAccessToken(),
                new Callback<ArrayList<User>>() {
                    @Override
                    public void onResponse(@NonNull Call<ArrayList<User>> call,
                                           @NonNull Response<ArrayList<User>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                // Getting all friend requests
                                ArrayList<User> friendRequests = response.body();

                                // Check if exists a friend request made by the user from the profile
                                for (User friendRequest : friendRequests) {
                                    if (friendRequest.getId().equals(user.getId())) {
                                        existsFriendRequestSentByProfileUser = true;
                                    }
                                }

                                // Accept friend request made previously by the profile user
                                if (!isFriend && existsFriendRequestSentByProfileUser) {
                                    acceptFriendRequest();
                                } else {
                                    sendFriendRequest();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArrayList<User>> call, @NonNull Throwable t) {
                    }
                });
    }

    private void sendFriendRequest() {
        this.apiManager.sendFriendRequest(this.authenticationToken.getAccessToken(), this.user.getId(),
                new Callback<FriendshipResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FriendshipResponse> call,
                                           @NonNull Response<FriendshipResponse> response) {
                        if (response.isSuccessful()) {
                            // Display dialog informing that friend request has been sent
                            showDialogNotification(getText(R.string.friendRequestSent).toString());
                        } else {
                            // Display dialog informing that friend request has been sent
                            showDialogNotification(getText(R.string.friendRequestNotSent).toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FriendshipResponse> call, @NonNull Throwable t) {}
                });
    }

    private void acceptFriendRequest() {
        this.apiManager.acceptFriendRequest(this.authenticationToken.getAccessToken(),
                this.user.getId(), new Callback<FriendshipResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FriendshipResponse> call,
                                           @NonNull Response<FriendshipResponse> response) {
                        if (response.isSuccessful()) {
                            showDialogNotification(user.getName() + " " +
                                    getText(R.string.isNowYourFriend));
                        } else {
                            // Display dialog informing that friend request has been sent
                            showDialogNotification(getText(R.string.friendRequestSent).toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FriendshipResponse> call, @NonNull Throwable t) {}
                });
    }

    private void showDialogNotification(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.requireContext());
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(R.string.acceptLabel, (dialog, button) -> dialog.dismiss());

        builder.setOnDismissListener(DialogInterface::dismiss);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void navigateBack() {
        getParentFragmentManager().popBackStack();
    }
}