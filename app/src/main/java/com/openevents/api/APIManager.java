package com.openevents.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.openevents.R;
import com.openevents.api.responses.Profile;
import com.openevents.constants.Constants;
import com.openevents.api.responses.AuthToken;
import com.openevents.controller.LoginActivity;
import com.openevents.controller.MainActivity;
import com.openevents.model.User;
import com.openevents.model.UserSession;
import com.openevents.utils.SharedPrefs;
import com.openevents.utils.ToastNotification;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIManager {
    private static APIManager apiManager;
    private Retrofit retrofit;
    private API service;

    public static APIManager getInstance() {
        if(apiManager == null) {
            apiManager = new APIManager();
        }

        return apiManager;
    }

    private APIManager() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.service = this.retrofit.create(API.class);
    }

    public void login(Context context, UserSession userSession) {
        this.service.login(userSession).enqueue(new Callback<AuthToken>() {
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                if(response.body() != null) {
                    if(response.code() == 200) {
                        // Get response body parsing it to AuthToken
                        AuthToken authToken = response.body();

                        // Save AuthToken to SharedPreferences
                        SharedPrefs sharedPrefs = SharedPrefs.getInstance(context);
                        sharedPrefs.addStringEntry(Constants.AUTHENTICATION_TOKEN_SHARED_PREFERENCES, authToken.getAccessToken());

                        // Redirect user to HomeActivity
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                } else {
                    ToastNotification.showNotification(context, R.string.invalidCredentialsError);
                }
            }

            @Override
            public void onFailure(Call<AuthToken> call, Throwable t) {
                ToastNotification.showServerConnectionError(context);
            }
        });
    }

    public void register(User user, Callback<Profile> callback) {
        this.service.register(user).enqueue(callback);
    }
}
