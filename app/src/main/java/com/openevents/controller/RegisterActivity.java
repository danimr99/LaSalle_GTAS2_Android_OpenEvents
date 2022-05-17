package com.openevents.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.responses.Profile;
import com.openevents.constants.Constants;
import com.openevents.controller.fragments.ImageSelectorFragment;
import com.openevents.model.User;
import com.openevents.utils.Numbers;
import com.openevents.utils.ToastNotification;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private APIManager apiManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Create an instance of APIManager
        this.apiManager = APIManager.getInstance();

        // Create ImageSelectorFragment
        FragmentManager fm = this.getSupportFragmentManager();
        this.fragment = (ImageSelectorFragment) fm.findFragmentById(R.id.fragment_container);

        // Inflate view with the ImageSelectorFragment
        if (this.fragment == null) {
            this.fragment = new ImageSelectorFragment();
            fm.beginTransaction().add(R.id.fragment_container, this.fragment).commit();
        }

        // Get each component from the view
        this.emailLayout = findViewById(R.id.emailInputLayout);
        this.email = findViewById(R.id.emailInput);
        this.nameLayout = findViewById(R.id.firstNameLayout);
        this.name = findViewById(R.id.firstNameInput);
        this.lastNameLayout = findViewById(R.id.lastNameLayout);
        this.lastName = findViewById(R.id.lastNameInput);
        this.passwordLayout = findViewById(R.id.passwordInputLayout);
        this.password = findViewById(R.id.passwordInput);
        this.repeatPasswordLayout = findViewById(R.id.repeatPasswordInputLayout);
        this.repeatPassword = findViewById(R.id.repeatPasswordInput);
        this.createAccountButton = findViewById(R.id.registerButton);

        // Set onClickListener to button
        this.createAccountButton.setOnClickListener(view -> this.createAccount());
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get profile image view once the fragment has been loaded
        View fragmentView = this.fragment.getView();
        this.profileImage = fragmentView.findViewById(R.id.imageSelector);
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
        String email = this.email.getText().toString();
        String name = this.name.getText().toString();
        String lastName = this.lastName.getText().toString();
        String password = this.password.getText().toString();
        String repeatedPassword = this.repeatPassword.getText().toString();

        // Check if all fields are filled and well formed
        if (this.isFormValid(email, name, lastName, password, repeatedPassword)) {
            // Create a new user
            User user = new User(name, lastName, email, password, image);

            // Register new user to API
            this.apiManager.register(user, new Callback<Profile>() {
                @Override
                public void onResponse(Call<Profile> call, Response<Profile> response) {
                    if (response.isSuccessful()) {
                        ToastNotification.showNotification(getApplicationContext(),
                                R.string.registerSuccessful);

                        // Redirect user to LoginActivity
                        Intent intent = new Intent(RegisterActivity.this,
                                LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            // Get error message from API
                            String errorBody = response.errorBody().string();
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
                            ToastNotification.showNotification(getApplicationContext(),
                                    R.string.registerError);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Profile> call, Throwable t) {
                    ToastNotification.showServerConnectionError(getApplicationContext());
                }
            });
        }
    }
}