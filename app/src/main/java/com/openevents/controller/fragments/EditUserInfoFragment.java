package com.openevents.controller.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;
import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.requests.CreatedUser;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.RegisteredUser;
import com.openevents.constants.Constants;
import com.openevents.controller.components.ImageSelectorFragment;
import com.openevents.utils.Notification;
import com.openevents.utils.Numbers;
import com.openevents.utils.SharedPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserInfoFragment extends Fragment {
    // UI Components
    private TextInputLayout emailLayout;
    private EditText email;
    private TextInputLayout firstNameLayout;
    private EditText firstName;
    private TextInputLayout lastNameLayout;
    private EditText lastName;
    private TextInputLayout passwordLayout;
    private EditText password;
    private TextInputLayout passwordConfirmLayout;
    private EditText passwordConfirm;
    private Button updateInfoButton;

    // Variables
    private SharedPrefs sharedPrefs;
    private AuthenticationToken authenticationToken;
    private APIManager apiManager;

    public EditUserInfoFragment() {
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
        View view = inflater.inflate(R.layout.fragment_edit_user_info, container, false);

        //API Manager
        this.apiManager = APIManager.getInstance();

        // Get instance of SharedPreferences
        this.sharedPrefs = SharedPrefs.getInstance(getContext());

        // Get user authentication token
        this.authenticationToken =
                new AuthenticationToken(this.sharedPrefs.getAuthenticationToken());

        // Create ImageSelectorFragment
        FragmentManager fm = this.getChildFragmentManager();
        ImageSelectorFragment fragment = (ImageSelectorFragment) fm.findFragmentById(R.id.image_selector_fragment_container);

        // Inflate view with the ImageSelectorFragment
        if (fragment == null) {
            fragment = new ImageSelectorFragment(true);
            fm.beginTransaction().add(R.id.image_selector_fragment_container, fragment).commit();
        }

        // Get each component from the view
        ImageView backArrow = view.findViewById(R.id.edit_profile_back_arrow);
        this.emailLayout = view.findViewById(R.id.email_input_layout);
        this.email = view.findViewById(R.id.email_input);
        this.firstNameLayout = view.findViewById(R.id.first_name_input_layout);
        this.firstName = view.findViewById(R.id.first_name_input);
        this.lastNameLayout = view.findViewById(R.id.last_name_input_layout);
        this.lastName = view.findViewById(R.id.last_name_input);
        this.passwordLayout = view.findViewById(R.id.password_input_layout);
        this.password = view.findViewById(R.id.password_input);
        this.passwordConfirmLayout = view.findViewById(R.id.repeat_password_input_layout);
        this.passwordConfirm = view.findViewById(R.id.repeat_password_input);
        this.updateInfoButton = view.findViewById(R.id.update_user_info_button);

        // Set on click listener to the back arrow button
        backArrow.setOnClickListener(v -> navigateBack());

        // Set on click listener to the update information button
        this.updateInfoButton.setOnClickListener(v -> editUser());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the email field to the user's email
        this.email.setText(this.sharedPrefs.getUser().getEmail());

        // Set the first name field to the user's first name
        this.firstName.setText(this.sharedPrefs.getUser().getName());

        // Set the last name field to the user's last name
        this.lastName.setText(this.sharedPrefs.getUser().getLastName());

        // Set the onClickListener for the submitInfo button
        this.updateInfoButton.setOnClickListener(v -> editUser());
    }

    private boolean checkEmail(String email) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        } else {
            this.emailLayout.setError(this.getText(R.string.invalidEmailError));
        }

        return false;
    }

    private boolean checkPassword(String password, String repeatedPassword) {
        // Check if passwords match
        if (password.equals(repeatedPassword)) {
            // Check if password are longer than min length requirement
            if (password.length() >= Constants.MIN_LENGTH_PASSWORD) {
                return true;
            } else {
                this.passwordLayout.setError(this.getText(R.string.passwordMinLengthError));
                this.passwordConfirmLayout.setError(this.getText(R.string.passwordMinLengthError));
            }
        } else {
            this.passwordLayout.setError(this.getText(R.string.passwordsNotMatchError));
            this.passwordConfirmLayout.setError(this.getText(R.string.passwordsNotMatchError));
        }

        return false;
    }

    private boolean isFormValid(String email, String name, String lastName, String password,
                                String repeatedPassword) {
        boolean isEmailValid = false, arePasswordsValid = false;

        // Reset error on each field in case exists
        this.emailLayout.setError(null);
        this.firstNameLayout.setError(null);
        this.lastNameLayout.setError(null);
        this.passwordLayout.setError(null);
        this.passwordConfirmLayout.setError(null);

        // Check if all fields from the form are filled
        if (!email.isEmpty()) {
            isEmailValid = this.checkEmail(email);
        } else {
            this.emailLayout.setError(getText(R.string.requiredFieldError));
        }

        if (name.isEmpty()) {
            this.firstNameLayout.setError(getText(R.string.requiredFieldError));
        }

        if (lastName.isEmpty()) {
            this.lastNameLayout.setError(getText(R.string.requiredFieldError));
        }

        if (!password.isEmpty() && !repeatedPassword.isEmpty()) {
            arePasswordsValid = this.checkPassword(password, repeatedPassword);
        } else {
            if (password.isEmpty()) {
                this.passwordLayout.setError(getText(R.string.requiredFieldError));
            }

            if (repeatedPassword.isEmpty()) {
                this.passwordConfirmLayout.setError(getText(R.string.requiredFieldError));
            }
        }

        return isEmailValid && !name.isEmpty() && !lastName.isEmpty() && arePasswordsValid;
    }

    private void editUser() {
        // Get random image
        int imageIndex = Numbers.generateRandomNumber(0,
                Constants.EXAMPLE_PROFILE_IMAGES_URL.length - 1);
        String image = Constants.EXAMPLE_PROFILE_IMAGES_URL[imageIndex];

        // Get all inputs fields
        String email = this.email.getText().toString();
        String name = this.firstName.getText().toString();
        String lastName = this.lastName.getText().toString();
        String password = this.password.getText().toString();
        String repeatedPassword = this.passwordConfirm.getText().toString();

        // Check if all fields are valid
        if (this.isFormValid(email, name, lastName, password, repeatedPassword)) {
            // Create a new created user to send as request to API
            CreatedUser createdUser = new CreatedUser(name, lastName, email, password, image);

            this.apiManager.editAccount(this.authenticationToken.getAccessToken(), createdUser,
                    new Callback<RegisteredUser>() {
                @Override
                public void onResponse(@NonNull Call<RegisteredUser> call,
                                       @NonNull Response<RegisteredUser> response) {
                    if(response.isSuccessful()) {
                        if(response.body() != null) {
                            // Get registered user
                            RegisteredUser registeredUser = response.body();

                            // Save email from registered user in case logged in user could
                            // have changed it
                            sharedPrefs.overrideStringEntry(Constants.USER_EMAIL_SHARED_PREFERENCES,
                                    registeredUser.getEmail());

                            // Display success notification
                            showRegisterSuccessfulDialog();
                        }
                    } else {
                        Notification.showDialogNotification(getContext(),
                                getText(R.string.serverConnectionFailed).toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RegisteredUser> call,
                                      @NonNull Throwable t) {
                    Notification.showDialogNotification(getContext(),
                            getText(R.string.cannotConnectToServerError).toString());
                }
            });
        }
    }

    private void showRegisterSuccessfulDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(this.getText(R.string.accountUpdatedSuccessfully));
        builder.setCancelable(true);

        builder.setPositiveButton(R.string.acceptLabel, (dialog, button) -> {
            // Dismiss dialog
            dialog.dismiss();
        });

        builder.setOnDismissListener(dialog -> {
            // Redirect user to LoginActivity
            navigateBack();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void navigateBack() {
        requireActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.home_fragment_container, new UserFragment()).commit();
    }
}