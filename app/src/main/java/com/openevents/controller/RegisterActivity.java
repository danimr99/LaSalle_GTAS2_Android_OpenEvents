package com.openevents.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.requests.CreatedUser;
import com.openevents.api.responses.RegisteredUser;
import com.openevents.constants.Constants;
import com.openevents.controller.components.ImageSelectorFragment;
import com.openevents.utils.Numbers;
import com.openevents.utils.Notification;

import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    // UI Components
    private ImageSelectorFragment fragment;
    private CircleImageView profileImage;
    private TextInputLayout emailLayout;
    private EditText email;
    private TextInputLayout nameLayout;
    private TextInputEditText name;
    private TextInputLayout lastNameLayout;
    private TextInputEditText lastName;
    private TextInputLayout passwordLayout;
    private TextInputEditText password;
    private TextInputLayout repeatPasswordLayout;
    private TextInputEditText repeatPassword;
    private Button createAccountButton;

    // Variables
    private APIManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Create an instance of APIManager
        this.apiManager = APIManager.getInstance();

        // Create ImageSelectorFragment
        FragmentManager fm = this.getSupportFragmentManager();
        this.fragment = (ImageSelectorFragment) fm.findFragmentById(R.id.image_selector_fragment_container);

        // Inflate view with the ImageSelectorFragment
        if (this.fragment == null) {
            this.fragment = new ImageSelectorFragment();
            fm.beginTransaction().add(R.id.image_selector_fragment_container, this.fragment).commit();
        }

        // Get each component from the view
        this.emailLayout = findViewById(R.id.email_input_layout);
        this.email = findViewById(R.id.email_input);
        this.nameLayout = findViewById(R.id.first_name_input_layout);
        this.name = findViewById(R.id.first_name_input);
        this.lastNameLayout = findViewById(R.id.last_name_input_layout);
        this.lastName = findViewById(R.id.last_name_input);
        this.passwordLayout = findViewById(R.id.password_input_layout);
        this.password = findViewById(R.id.password_input);
        this.repeatPasswordLayout = findViewById(R.id.repeat_password_input_layout);
        this.repeatPassword = findViewById(R.id.repeat_password_input);
        this.createAccountButton = findViewById(R.id.register_button);

        // Set onClickListener to button
        this.createAccountButton.setOnClickListener(view -> this.createAccount());
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get profile image view once the fragment has been loaded
        View fragmentView = this.fragment.getView();
        this.profileImage = fragmentView != null ?
                fragmentView.findViewById(R.id.image_selector) : null;
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
                this.repeatPasswordLayout.setError(this.getText(R.string.passwordMinLengthError));
            }
        } else {
            this.passwordLayout.setError(this.getText(R.string.passwordsNotMatchError));
            this.repeatPasswordLayout.setError(this.getText(R.string.passwordsNotMatchError));
        }

        return false;
    }

    private boolean isFormValid(String email, String name, String lastName, String password,
                                String repeatedPassword) {
        boolean isEmailValid = false, arePasswordsValid = false;

        // Reset error on each field in case exists
        this.emailLayout.setError(null);
        this.nameLayout.setError(null);
        this.lastNameLayout.setError(null);
        this.passwordLayout.setError(null);
        this.repeatPasswordLayout.setError(null);

        // Check if all fields from the form are filled
        if (!email.isEmpty()) {
            isEmailValid = this.checkEmail(email);
        } else {
            this.emailLayout.setError(getText(R.string.requiredFieldError));
        }

        if (name.isEmpty()) {
            this.nameLayout.setError(getText(R.string.requiredFieldError));
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
                this.repeatPasswordLayout.setError(getText(R.string.requiredFieldError));
            }
        }

        return isEmailValid && !name.isEmpty() && !lastName.isEmpty() && arePasswordsValid;
    }

    private void createAccount() {
        // Get random image
        int imageIndex = Numbers.generateRandomNumber(0,
                Constants.EXAMPLE_PROFILE_IMAGES_URL.length - 1);
        String image = Constants.EXAMPLE_PROFILE_IMAGES_URL[imageIndex];

        // Get text from form fields
        String email = Objects.requireNonNull(this.email.getText()).toString();
        String name = Objects.requireNonNull(this.name.getText()).toString();
        String lastName = Objects.requireNonNull(this.lastName.getText()).toString();
        String password = Objects.requireNonNull(this.password.getText()).toString();
        String repeatedPassword = Objects.requireNonNull(this.repeatPassword.getText()).toString();

        // Check if all fields are filled and well formed
        if (this.isFormValid(email, name, lastName, password, repeatedPassword)) {
            // Create a new user
            CreatedUser user = new CreatedUser(name, lastName, email, password, image);

            // Register new user to API
            this.apiManager.register(user, new Callback<RegisteredUser>() {
                @Override
                public void onResponse(@NonNull Call<RegisteredUser> call,
                                       @NonNull Response<RegisteredUser> response) {
                    if (response.isSuccessful()) {
                        if(response.body() != null) {
                            showRegisterSuccessfulDialog();
                        }
                    } else {
                        try {
                            // Get error message from API
                            String errorBody = Objects.requireNonNull(response.errorBody()).string();
                            JsonObject element = JsonParser.parseString(errorBody).getAsJsonObject();

                            // Try to get the type of the email error
                            String errorMsg = "";
                            String sqlError = "";
                            try {
                                errorMsg = element.
                                        get("stackTrace").getAsJsonObject().
                                        get("details").getAsJsonArray().get(0).getAsJsonObject().
                                        get("message").getAsString();
                            } catch (NullPointerException exception) {
                                sqlError = element.
                                        get("stackTrace").getAsJsonObject().
                                        get("code").getAsString();
                            }

                            // Check if email is not valid
                            if (!errorMsg.isEmpty()) {
                                emailLayout.setError(getText(R.string.invalidEmailError));
                            }

                            // Check if user with the same email already exists
                            if (!sqlError.isEmpty()) {
                                emailLayout.setError(getText(R.string.alreadyExistsEmailError));
                            }
                        } catch (IOException e) {
                            Notification.showDialogNotification(getApplicationContext(),
                                    getText(R.string.registerError).toString());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RegisteredUser> call, @NonNull Throwable t) {
                    Notification.showDialogNotification(getApplicationContext(),
                            getText(R.string.cannotConnectToServerError).toString());
                }
            });
        }
    }

    private void showRegisterSuccessfulDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getText(R.string.registerSuccessful));
        builder.setCancelable(true);

        builder.setPositiveButton(R.string.acceptLabel, (dialog, button) -> {
            // Dismiss dialog
            dialog.dismiss();
        });

        builder.setOnDismissListener(dialog -> {
            // Redirect user to LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}