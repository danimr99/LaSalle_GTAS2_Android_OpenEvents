package com.openevents.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.constants.Constants;
import com.openevents.api.responses.AuthToken;
import com.openevents.utils.SharedPrefs;
import com.openevents.utils.ToastNotification;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button signInButton;
    private TextView signUpTextView;
    private APIManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Email EditText
        this.email = findViewById(R.id.emailInput);

        // Password EditText
        this.password = findViewById(R.id.passwordInput);

        // SignInButton
        this.signInButton = findViewById(R.id.signInButton);
        this.signInButton.setOnClickListener(view -> {
            this.attemptSignIn();
        });

        // SignUpTextView
        this.signUpTextView = findViewById(R.id.signUpText);
        this.signUpTextView.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Create an instance of APIManager
        this.apiManager = APIManager.getInstance();
    }

    private void attemptSignIn() {
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            ToastNotification.showNotification(getApplicationContext(), R.string.formNotFilledError);
        } else {
            // Attempt login to the API
            this.apiManager.login(email, password, new Callback<AuthToken>() {
                @Override
                public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                    if(response.body() != null) {
                        if(response.code() == 200) {
                            // Get response body parsing it to AuthToken
                            AuthToken authToken = response.body();

                            // Save AuthToken to SharedPreferences
                            SharedPrefs sharedPrefs = SharedPrefs.getInstance(getApplicationContext());
                            sharedPrefs.addStringEntry(Constants.AUTH_TOKEN, authToken.getAccessToken());

                            // Redirect user to HomeActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        ToastNotification.showNotification(getApplicationContext(), R.string.invalidCredentialsError);
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