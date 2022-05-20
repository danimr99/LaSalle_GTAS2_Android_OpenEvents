package com.openevents.controller.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.User;
import com.openevents.api.responses.UserStats;
import com.openevents.constants.Constants;
import com.openevents.controller.components.ImageSelectorFragment;
import com.openevents.utils.SharedPrefs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    private ImageSelectorFragment fragment;
    private CircleImageView profileImage;
    private TextView profileNameTitle;
    private TextView profileLastNameTitle;
    private TextView profileName;
    private TextView profileLastName;
    private TextView profileEmail;
    private TextView profilePassword;
    private TextView profileAverageScore;
    private TextView profileNumberOfComments;
    private TextView profilePercentageLessComments;
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Get instance of SharedPrefs
        this.sharedPrefs = SharedPrefs.getInstance(getContext());

        // Get user authentication token
        this.authenticationToken =
                new AuthenticationToken(this.sharedPrefs.getAuthenticationToken());

        // Get an instance of APIManager and get the user and his/her stats from API
        this.apiManager = APIManager.getInstance();
        this.getUserLoggedIn();
        this.getUserStats();

        // Create ImageSelectorFragment
        FragmentManager fm = this.getChildFragmentManager();
        this.fragment = (ImageSelectorFragment) fm.findFragmentById(R.id.image_selector_fragment_container);

        // Inflate view with the ImageSelectorFragment
        if (this.fragment == null) {
            this.fragment = new ImageSelectorFragment();
            fm.beginTransaction().add(R.id.image_selector_fragment_container, this.fragment).commit();
        }

        // Get all text views from the layout
        this.profileNameTitle = view.findViewById(R.id.user_profile_name);
        this.profileLastNameTitle = view.findViewById(R.id.user_profile_last_name);
        this.profileName = view.findViewById(R.id.user_name);
        this.profileLastName = view.findViewById(R.id.user_last_name);
        this.profileEmail = view.findViewById(R.id.user_email);
        this.profilePassword = view.findViewById(R.id.user_password);
        this.profileAverageScore = view.findViewById(R.id.user_average_score);
        this.profileNumberOfComments = view.findViewById(R.id.user_number_comments);
        this.profilePercentageLessComments = view.findViewById(R.id.user_percentage_users_less_comments);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get profile image view once the fragment has been loaded
        View fragmentView = this.fragment.getView();
        this.profileImage = fragmentView != null ?
                fragmentView.findViewById(R.id.image_selector) : null;
    }

    private void getUserLoggedIn() {
        // Get user's email address
        String email = this.sharedPrefs.getStringEntry(Constants.USER_EMAIL_SHARED_PREFERENCES);

        // Get user from API
        this.apiManager.getUserByEmail(this.authenticationToken.getAccessToken(), email,
                new Callback<ArrayList<User>>() {
                    @Override
                    public void onResponse(@NonNull Call<ArrayList<User>> call, @NonNull Response<ArrayList<User>> response) {
                        if (response.isSuccessful()) {
                            if(response.body() != null) {
                                // Get user from response
                                User user = response.body().get(0);

                                // Save user to SharedPreferences
                                sharedPrefs.saveUser(user);

                                // Update UI with user data
                                updateUserDataUI(user);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArrayList<User>> call, @NonNull Throwable t) {
                        // Get user from SharedPreferences
                        User user = sharedPrefs.getUser();

                        // Update UI with user data
                        updateUserDataUI(user);
                    }
                });
    }

    private void updateUserDataUI(User user) {
        // Set image from the user
        Picasso.get()
                .load(user.getImage())
                .placeholder(R.drawable.user_placeholder)
                .into(this.profileImage);

        // Set data to corresponding field
        this.profileNameTitle.setText(user.getName());
        this.profileLastNameTitle.setText(user.getLastName());
        this.profileName.setText(user.getName());
        this.profileLastName.setText(user.getLastName());
        this.profileEmail.setText(user.getEmail());
        this.profilePassword.setText(user.getPassword().substring(0, Constants.MIN_LENGTH_PASSWORD));
    }

    private void getUserStats() {
        // Get user ID from SharedPreferences
        int userID = sharedPrefs.getUser().getId();

        this.apiManager.getUserStats(this.authenticationToken.getAccessToken(), userID, new Callback<UserStats>() {
            @Override
            public void onResponse(Call<UserStats> call, Response<UserStats> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        UserStats stats = response.body();

                        // Update UI with user stats
                        updateUserStatsUI(stats);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserStats> call, Throwable t) {}
        });
    }

    private void updateUserStatsUI(UserStats stats) {
        String averageScore = stats.getAverageScore();
        String percentageCommentersBelow = stats.getPercentageCommentersBelow();

        // Check if user has average score
        if(averageScore == null) {
            averageScore = (String) getText(R.string.noAverageScore);
            percentageCommentersBelow = (String) getText(R.string.noPercentageCommentersBelow);
        }

        this.profileAverageScore.setText(averageScore);
        this.profileNumberOfComments.setText(stats.getNumberOfComments());
        this.profilePercentageLessComments.setText(percentageCommentersBelow);
    }
}