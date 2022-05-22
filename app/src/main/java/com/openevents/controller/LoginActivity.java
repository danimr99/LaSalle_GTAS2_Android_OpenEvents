package com.openevents.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.constants.Constants;
import com.openevents.api.requests.UserSession;
import com.openevents.utils.SharedPrefs;
import com.openevents.utils.ToastNotification;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    // UI Components
    private TextInputLayout emailLayout;
    private TextInputEditText email;
    private TextInputLayout passwordLayout;
    private TextInputEditText password;
    private Button signInButton;
    private TextView signUpTextView;

    // Variables
    private APIManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create an instance of APIManager
        this.apiManager = APIManager.getInstance();

        // Email input
        this.emailLayout = findViewById(R.id.email_input_layout);
        this.email = findViewById(R.id.email_input);

        // Password input
        this.passwordLayout = findViewById(R.id.password_input_layout);
        this.password = findViewById(R.id.password_input);

        // Sign in button
        this.signInButton = findViewById(R.id.sign_in_button);
        this.signInButton.setOnClickListener(view -> this.attemptSignIn());

        // Sign up textview to navigate to RegisterActivity
        this.signUpTextView = findViewById(R.id.sign_up_text);
        this.signUpTextView.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.resetFields();
    }

    private void resetFields() {
        this.emailLayout.setError(null);
        this.passwordLayout.setError(null);
    }

    private boolean checkEmail(String email) {
        if(email.isEmpty()) {
            this.emailLayout.setError(this.getText(R.string.requiredFieldError));
            return false;
        }

        return true;
    }

    private boolean checkPassword(String password) {
        if(password.isEmpty()) {
            this.passwordLayout.setError(this.getText(R.string.requiredFieldError));
            return false;
        }

        return true;
    }

    private boolean isFormValid(String email, String password) {
        boolean isEmailValid, isPasswordValid;

        // Reset error on each field in case exists
        this.resetFields();

        // Check if email is valid
        isEmailValid = this.checkEmail(email);

        // Check if password is valid
        isPasswordValid = this.checkPassword(password);

        return isEmailValid && isPasswordValid;
    }

    private void attemptSignIn() {
        // Get text from form fields
        String email = Objects.requireNonNull(this.email.getText()).toString();
        String password = Objects.requireNonNull(this.password.getText()).toString();
        UserSession userSession = new UserSession(email, password);

        // Check if all fields are filled and well formed
        if(this.isFormValid(email, password)) {
            // Attempt login to the API
            this.apiManager.login(userSession, new Callback<AuthenticationToken>() {
                @Override
                public void onResponse(@NonNull Call<AuthenticationToken> call,
                                       @NonNull Response<AuthenticationToken> response) {
                    if(response.isSuccessful()) {
                        if(response.body() != null) {
                            // Get response body parsing it to AuthenticationToken
                            AuthenticationToken authenticationToken = response.body();

                            // Create an instance of SharedPreferences
                            SharedPrefs sharedPrefs = SharedPrefs.getInstance(getApplicationContext());

                            // Save AuthenticationToken and user to SharedPreferences
                            sharedPrefs.addStringEntry(Constants.AUTHENTICATION_TOKEN_SHARED_PREFERENCES,
                                    authenticationToken.getAccessToken());
                            sharedPrefs.addStringEntry(Constants.USER_EMAIL_SHARED_PREFERENCES,
                                    userSession.getEmail());

                            // Redirect user to HomeActivity
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        ToastNotification.showNotification(getApplicationContext(), R.string.invalidCredentialsError);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AuthenticationToken> call, @NonNull Throwable t) {
                    ToastNotification.showServerConnectionError(getApplicationContext());
                }
            });
        }
    }
}