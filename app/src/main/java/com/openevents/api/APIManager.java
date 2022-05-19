package com.openevents.api;

import com.openevents.api.responses.RegisteredUser;
import com.openevents.api.responses.UserProfile;
import com.openevents.constants.Constants;
import com.openevents.api.responses.AuthenticationToken;

import com.openevents.model.User;
import com.openevents.model.UserSession;

import java.util.ArrayList;

import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIManager {
    private static APIManager apiManager;
    private Retrofit retrofit;
    private API service;

    public static APIManager getInstance() {
        if (apiManager == null) {
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

    private String addBearerAuthenticationToken(String authenticationToken) {
        return "Bearer " + authenticationToken;
    }

    public void login(UserSession userSession, Callback<AuthenticationToken> callback) {
        this.service.login(userSession).enqueue(callback);
    }

    public void register(User user, Callback<RegisteredUser> callback) {
        this.service.register(user).enqueue(callback);
    }

    public void getUsers(String authenticationToken, Callback<ArrayList<UserProfile>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getUsers(authentication).enqueue(callback);
    }

    public void getUserByEmail(String authenticationToken, String search,
                               Callback<ArrayList<User>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getUserByEmail(authentication, search).enqueue(callback);
    }
}
