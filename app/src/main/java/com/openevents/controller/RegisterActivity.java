package com.openevents.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.api.responses.Profile;
import com.openevents.constants.Constants;
import com.openevents.controller.fragments.ImageSelectorFragment;
import com.openevents.model.User;
import com.openevents.utils.Numbers;
import com.openevents.utils.ToastNotification;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private APIManager apiManager;
    private ImageSelectorFragment fragment;
    private CircleImageView profileImage;
    private EditText email;
    private EditText name;
    private EditText lastName;
    private EditText password;
    private EditText repeatPassword;
    private Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Create ImageSelectorFragment
        FragmentManager fm = this.getSupportFragmentManager();
        this.fragment = (ImageSelectorFragment) fm.findFragmentById(R.id.fragment_container);

        // Inflate view with the ImageSelectorFragment
        if (this.fragment == null) {
            this.fragment = new ImageSelectorFragment();
            fm.beginTransaction().add(R.id.fragment_container, this.fragment).commit();
        }

        // Get each component from the view
        this.email = findViewById(R.id.emailInput);
        this.name = findViewById(R.id.firstNameInput);
        this.lastName = findViewById(R.id.lastNameInput);
        this.password = findViewById(R.id.passwordInput);
        this.repeatPassword = findViewById(R.id.repeatPasswordInput);
        this.createAccountButton = findViewById(R.id.registerButton);

        // Create an instance of APIManager
        this.apiManager = APIManager.getInstance();

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

    private void createAccount() {
        // Get random image
        int imageIndex = Numbers.generateRandomNumber(0, Constants.EXAMPLE_PROFILE_IMAGES_URL.length - 1);
        String image = Constants.EXAMPLE_PROFILE_IMAGES_URL[imageIndex];

        // Get text from form fields
        String email = this.email.getText().toString();
        String name = this.name.getText().toString();
        String lastName = this.lastName.getText().toString();
        String password = this.password.getText().toString();
        String repeatedPassword = this.repeatPassword.getText().toString();

        // Check if all fields are filled and well formed
        if(this.isFormValid(email, name, lastName, password, repeatedPassword)) {
            // Create a new user
            User user = new User(name, lastName, email, password, image);

            // Register new user to API
            this.apiManager.register(user, new Callback<Profile>() {
                @Override
                public void onResponse(Call<Profile> call, Response<Profile> response) {
                    if (response.code() == 201) {
                        ToastNotification.showNotification(getApplicationContext(), R.string.registerSuccessful);

                        // Redirect user to LoginActivity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastNotification.showNotification(getApplicationContext(),
                                R.string.emailAlreadyExistsError);
                    }
                }

                @Override
                public void onFailure(Call<Profile> call, Throwable t) {
                    ToastNotification.showServerConnectionError(getApplicationContext());
                }
            });
        }
    }

    private boolean isFormValid(String email, String name, String lastName, String password,
                                String repeatedPassword) {
        if (!email.isEmpty() && !name.isEmpty() && !lastName.isEmpty() && !password.isEmpty() &&
                !repeatedPassword.isEmpty()) {
            // Check if email is valid
            if (email.contains("@")) {
                // Check if passwords match
                if (password.equals(repeatedPassword)) {
                    // Check if password are longer than min length requirement
                    if (password.length() >= Constants.MIN_LENGTH_PASSWORD) {
                        return true;
                    } else {
                        ToastNotification.showNotification(getApplicationContext(), R.string.passwordMinLengthError);
                    }
                } else {
                    ToastNotification.showNotification(getApplicationContext(), R.string.passwordsNotMatchError);
                }
            } else {
                ToastNotification.showNotification(getApplicationContext(), R.string.invalidEmailError);
            }
        } else {
            ToastNotification.showNotification(getApplicationContext(), R.string.formNotFilledError);
        }

        return false;
    }
}