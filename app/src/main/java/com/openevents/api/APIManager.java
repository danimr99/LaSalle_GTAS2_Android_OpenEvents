package com.openevents.api;

import com.openevents.api.responses.Profile;
import com.openevents.constants.Constants;
import com.openevents.api.responses.AuthToken;

import com.openevents.model.User;
import com.openevents.model.UserSession;

import retrofit2.Callback;
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

    public void login(UserSession userSession, Callback<AuthToken> callback) {
        this.service.login(userSession).enqueue(callback);
    }

    public void register(User user, Callback<Profile> callback) {
        this.service.register(user).enqueue(callback);
    }
}
