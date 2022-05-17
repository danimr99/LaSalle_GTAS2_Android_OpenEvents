package com.openevents.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.responses.AuthToken;
import com.openevents.constants.Constants;
import com.openevents.model.UserSession;
import com.openevents.utils.SharedPrefs;
import com.openevents.utils.ToastNotification;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout emailLayout;
    private TextInputEditText email;
    private TextInputLayout passwordLayout;
    private TextInputEditText password;
    private Button signInButton;
    private TextView signUpTextView;
    private APIManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create an instance of APIManager
        this.apiManager = APIManager.getInstance();

        // Email input
        this.emailLayout = findViewById(R.id.emailInputLayout);
        this.email = findViewById(R.id.emailInput);

        // Password input
        this.passwordLayout = findViewById(R.id.passwordInputLayout);
        this.password = findViewById(R.id.passwordInput);

        // Sign in button
        this.signInButton = findViewById(R.id.signInButton);
        this.signInButton.setOnClickListener(view -> this.attemptSignIn());

        // Sign up textview to navigate to RegisterActivity
        this.signUpTextView = findViewById(R.id.signUpText);
        this.signUpTextView.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
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
        this.emailLayout.setError(null);
        this.passwordLayout.setError(null);

        // Check if email is valid
        isEmailValid = this.checkEmail(email);

        // Check if password is valid
        isPasswordValid = this.checkPassword(password);

        return isEmailValid && isPasswordValid;
    }

    private void attemptSignIn() {
        // Get text from form fields
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        UserSession userSession = new UserSession(email, password);

        // Check if all fields are filled and well formed
        if(this.isFormValid(email, password)) {
            // Attempt login to the API
            this.apiManager.login(userSession, new Callback<AuthToken>() {
                @Override
                public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                    if(response.body() != null) {
                        if(response.isSuccessful()) {
                            // Get response body parsing it to AuthToken
                            AuthToken authToken = response.body();

                            // Save AuthToken to SharedPreferences
                            SharedPrefs sharedPrefs = SharedPrefs.getInstance(getApplicationContext());
                            sharedPrefs.addStringEntry(Constants.AUTHENTICATION_TOKEN_SHARED_PREFERENCES,
                                    authToken.getAccessToken());

                            // Redirect user to HomeActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        ToastNotification.showNotification(getApplicationContext(),
                                R.string.invalidCredentialsError);
                    }
                }

                @Override
                public void onFailure(Call<AuthToken> call, Throwable t) {
                    ToastNotification.showServerConnectionError(getApplicationContext());
                }
            });
        }
    }
}