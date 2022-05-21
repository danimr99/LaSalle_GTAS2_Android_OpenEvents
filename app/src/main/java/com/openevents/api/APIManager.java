package com.openevents.api;

import com.openevents.api.requests.CreatedUser;
import com.openevents.api.responses.Event;
import com.openevents.api.responses.RegisteredUser;
import com.openevents.api.responses.UserProfile;
import com.openevents.api.responses.UserStats;
import com.openevents.constants.Constants;
import com.openevents.api.responses.AuthenticationToken;

import com.openevents.api.responses.User;
import com.openevents.api.requests.UserSession;

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

    /*
     * Authentication
     */
    public void login(UserSession userSession, Callback<AuthenticationToken> callback) {
        this.service.login(userSession).enqueue(callback);
    }

    public void register(CreatedUser user, Callback<RegisteredUser> callback) {
        this.service.register(user).enqueue(callback);
    }

    /*
     * Users
     */
    public void getUsers(String authenticationToken, Callback<ArrayList<UserProfile>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getUsers(authentication).enqueue(callback);
    }

    public void getUserById(String authenticationToken, int userID,
                               Callback<ArrayList<User>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getUserById(authentication, userID).enqueue(callback);
    }

    public void getUserByEmail(String authenticationToken, String search,
                               Callback<ArrayList<User>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getUserByEmail(authentication, search).enqueue(callback);
    }

    public void getUserStats(String authenticationToken, int userID, Callback<UserStats> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getUserStats(authentication, userID).enqueue(callback);
    }

    /*
     * Events
     */
    public void getEvents(String authenticationToken, Callback<ArrayList<Event>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getEvents(authentication).enqueue(callback);
    }

    public void getPopularEvents(String authenticationToken, Callback<ArrayList<Event>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getPopularEvents(authentication).enqueue(callback);
    }

    public void getUserFutureEvents(String authenticationToken, int userID,
                                    Callback<ArrayList<Event>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getUserFutureEvents(authentication, userID).enqueue(callback);
    }

    public void getUserFinishedEvents(String authenticationToken, int userID,
                                    Callback<ArrayList<Event>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getUserFinishedEvents(authentication, userID).enqueue(callback);
    }

    public void getUserCurrentEvents(String authenticationToken, int userID,
                                    Callback<ArrayList<Event>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getUserCurrentEvents(authentication, userID).enqueue(callback);
    }

    /*
     * Friends
     */
    public void getFriends(String authenticationToken, Callback<ArrayList<User>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getFriends(authentication).enqueue(callback);
    }

    public void getFriendRequests(String authenticationToken, Callback<ArrayList<User>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getFriendRequests(authentication).enqueue(callback);
    }

    public void addFriend(String authenticationToken, int userID, Callback<User> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.addFriend(authentication, userID).enqueue(callback);
    }

    public void acceptFriend(String authenticationToken, int userID, Callback<User> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.acceptFriend(authentication, userID).enqueue(callback);
    }

    public void declineFriend(String authenticationToken, int userID, Callback<User> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.declineFriend(authentication, userID).enqueue(callback);
    }
}
