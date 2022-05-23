package com.openevents.controller.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.QueryResponse;
import com.openevents.api.responses.UserProfile;
import com.openevents.api.responses.UserStats;
import com.openevents.constants.Constants;
import com.openevents.utils.Notification;
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
    private LinearLayout sendFriendRequestLinearLayout;
    private View profileSeparator;
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
        this.sendFriendRequestLinearLayout = view.findViewById(R.id.user_profile_send_friend_request_linear_layout);
        this.profileSeparator = view.findViewById(R.id.user_profile_separator);

        // Set image from the user
        if(user.getImage() != null && user.getImage().trim().length() != 0) {
            Picasso.get()
                    .load(user.getImage())
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder)
                    .resize(Constants.MAX_IMAGE_WIDTH, Constants.MAX_IMAGE_HEIGHT)
                    .into(this.profileImage);
        } else {
            Picasso.get().load(R.drawable.user_placeholder).into(this.profileImage);
        }

        // Set data to each component
        this.profileName.setText(this.user.getName() + " " + this.user.getLastName());
        this.profileEmail.setText(this.user.getEmail());

        // Configure back arrow on click
        this.backArrow.setOnClickListener(v -> this.navigateBack());

        // Configure friendship buttons
        this.sendFriendRequestButton.setOnClickListener(v -> {
            // Check if is a friend
            if(!isFriend) {
                // Accept friend request made previously by the profile user if exists
                if (existsFriendRequestSentByProfileUser) {
                    acceptFriendRequest();
                } else {
                    sendFriendRequest();
                }
            }
        });

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
                new Callback<ArrayList<UserProfile>>() {
                    @Override
                    public void onResponse(@NonNull Call<ArrayList<UserProfile>> call,
                                           @NonNull Response<ArrayList<UserProfile>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                // Get all friends from the logged in user
                                ArrayList<UserProfile> friends = response.body();

                                // Check if exists the user from the profile in the list of friends
                                for (UserProfile friend : friends) {
                                    if (friend.getId() == user.getId()) {
                                        isFriend = true;
                                        break;
                                    }
                                }

                                // Set visibility off to send request button if profile user is a friend
                                if (isFriend) {
                                    sendFriendRequestLinearLayout.setVisibility(View.GONE);
                                    profileSeparator.setVisibility(View.GONE);
                                    sendFriendRequestButton.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArrayList<UserProfile>> call,
                                          @NonNull Throwable t) {
                        Notification.showDialogNotification(getContext(),
                                getText(R.string.cannotConnectToServerError).toString());
                    }
                });

        // Check if the user from the profile has sent a friend request to the user logged in
        this.apiManager.getFriendRequests(this.authenticationToken.getAccessToken(),
                new Callback<ArrayList<UserProfile>>() {
                    @Override
                    public void onResponse(@NonNull Call<ArrayList<UserProfile>> call,
                                           @NonNull Response<ArrayList<UserProfile>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                // Getting all friend requests
                                ArrayList<UserProfile> friendRequests = response.body();

                                // Check if exists a friend request made by the user from the profile
                                for (UserProfile friendRequest : friendRequests) {
                                    if (friendRequest.getId() == user.getId()) {
                                        existsFriendRequestSentByProfileUser = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArrayList<UserProfile>> call,
                                          @NonNull Throwable t) {
                        Notification.showDialogNotification(getContext(),
                                getText(R.string.cannotConnectToServerError).toString());
                    }
                });
    }

    private void sendFriendRequest() {
        this.apiManager.sendFriendRequest(this.authenticationToken.getAccessToken(), this.user.getId(),
                new Callback<QueryResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<QueryResponse> call,
                                           @NonNull Response<QueryResponse> response) {
                        if (response.isSuccessful()) {
                            // Display dialog informing that friend request has been sent
                            Notification.showDialogNotification(getContext(),
                                    getText(R.string.friendRequestSent).toString());
                        } else {
                            // Display dialog informing that friend request has been sent
                            Notification.showDialogNotification(getContext(),
                                    getText(R.string.friendRequestAlreadySent).toString());
                        }

                        // Disable button
                        sendFriendRequestLinearLayout.setVisibility(View.GONE);
                        profileSeparator.setVisibility(View.GONE);
                        sendFriendRequestButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(@NonNull Call<QueryResponse> call, @NonNull Throwable t) {
                        Notification.showDialogNotification(getContext(),
                                requireContext().getText(R.string.cannotConnectToServerError).toString());
                    }
                });
    }

    private void acceptFriendRequest() {
        this.apiManager.acceptFriendRequest(this.authenticationToken.getAccessToken(),
                this.user.getId(), new Callback<QueryResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<QueryResponse> call,
                                           @NonNull Response<QueryResponse> response) {
                        if (response.isSuccessful()) {
                            Notification.showDialogNotification(getContext(),
                                    user.getName() + " " + getText(R.string.isNowYourFriend));
                        } else {
                            // Display dialog informing that friend request has been sent
                            Notification.showDialogNotification(getContext(),
                                    getText(R.string.friendRequestSent).toString());
                        }

                        // Disable button
                        sendFriendRequestLinearLayout.setVisibility(View.GONE);
                        profileSeparator.setVisibility(View.GONE);
                        sendFriendRequestButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(@NonNull Call<QueryResponse> call,
                                          @NonNull Throwable t) {
                        Notification.showDialogNotification(getContext(),
                                requireContext().getText(R.string.cannotConnectToServerError).toString());
                    }
                });
    }

    private void navigateBack() {
        getParentFragmentManager().popBackStack();
    }
}