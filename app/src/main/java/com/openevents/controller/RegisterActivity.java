package com.openevents.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.openevents.utils.Base64Image;
import com.openevents.utils.JsonManager;
import com.openevents.utils.SharedPrefs;
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
        // Encode profile image
        String image = "";
        Bitmap bitmap = ((BitmapDrawable) this.profileImage.getDrawable()).getBitmap();
        image = Base64Image.encode(bitmap);

        // Get text from form fields
        String email = this.email.getText().toString();
        String name = this.email.getText().toString();
        String lastName = this.lastName.getText().toString();
        String password = this.password.getText().toString();
        String repeatedPassword = this.repeatPassword.getText().toString();

        // Check if all fields are filled
        if (!email.isEmpty() && !name.isEmpty() && !lastName.isEmpty() && !password.isEmpty() &&
                !repeatedPassword.isEmpty()) {
            // Check if passwords match
            if (password.equals(repeatedPassword)) {
                // Create a new user
                User user = new User(name, lastName, email, password, image);

                // Save user to SharedPreferences
                SharedPrefs sharedPrefs = SharedPrefs.getInstance(this);
                sharedPrefs.addStringEntry(Constants.USER_SHARED_PREFERENCES, JsonManager.toJSON(user));

                // Register new user to API
                this.apiManager.register(user, new Callback<Profile>() {
                    @Override
                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                        if (response.isSuccessful()) {
                            ToastNotification.showNotification(getApplicationContext(), R.string.registerSuccessful);

                            // Redirect user to LoginActivity
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            ToastNotification.showNotification(getApplicationContext(), R.string.registerError);
                        }
                    }

                    @Override
                    public void onFailure(Call<Profile> call, Throwable t) {
                        ToastNotification.showServerConnectionError(getApplicationContext());
                    }
                });
            } else {
                ToastNotification.showNotification(this, R.string.passwordsNotMatchError);
            }
        } else {
            ToastNotification.showNotification(this, R.string.formNotFilledError);
        }
    }
}