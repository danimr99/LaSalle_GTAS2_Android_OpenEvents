package com.openevents.controller.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.openevents.R;
import com.openevents.api.APIManager;
import com.openevents.constants.Constants;
import com.openevents.utils.SharedPrefs;

public class EditUserInfoFragment extends Fragment {
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
    private Button submitInfo;
    private SharedPrefs sharedPrefs;

    //API Manager
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

        // Get user from shared preferences
        this.sharedPrefs = SharedPrefs.getInstance(getContext());

        // Get each component from the view
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
        this.submitInfo = view.findViewById(R.id.submit_info_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set the email field to the user's email
        this.email.setText(this.sharedPrefs.getUser().getEmail());

        //set the first name field to the user's first name
        this.firstName.setText(this.sharedPrefs.getUser().getName());

        //set the last name field to the user's last name
        this.lastName.setText(this.sharedPrefs.getUser().getLastName());

        //set the password field to 'change password' text
        this.password.setText(getString(R.string.changePasswordHint));

        // Set the onClickListener for the submitInfo button
        this.submitInfo.setOnClickListener(v -> {
            // Get the user input
            String emailInput = email.getText().toString();
            String firstNameInput = firstName.getText().toString();
            String lastNameInput = lastName.getText().toString();
            String passwordInput = password.getText().toString();
            String passwordConfirmInput = passwordConfirm.getText().toString();

            isFormValid(emailInput, firstNameInput, lastNameInput, passwordInput, passwordConfirmInput);
        });

        this.submitInfo.setOnClickListener(v -> editUser());
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

    }
}