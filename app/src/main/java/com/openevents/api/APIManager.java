package com.openevents.api;

import com.openevents.api.requests.CreatedEvent;
import com.openevents.api.requests.CreatedUser;
import com.openevents.api.responses.Assistance;
import com.openevents.api.responses.Event;
import com.openevents.api.responses.QueryResponse;
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

    public void getUserFriends(String authenticationToken, int userID,
                               Callback<ArrayList<UserProfile>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getUserFriends(authentication, userID).enqueue(callback);
    }

    public void deleteAccount(String authenticationToken, Callback<Void> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.deleteAccount(authentication).enqueue(callback);
    }

    public void editAccount(String authenticationToken, CreatedUser createdUser,
                            Callback<RegisteredUser> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.editAccount(authentication, createdUser).enqueue(callback);
    }

    public void getUserPastAssistances(String authenticationToken, int userID,
                                       Callback<ArrayList<Event>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getUserPastAssistances(authentication, userID).enqueue(callback);
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

    public void getEventAssistants(String authenticationToken, int eventID,
                                   Callback<ArrayList<Assistance>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getEventAssistances(authentication, eventID).enqueue(callback);
    }

    public void getFinishedEventsCreatedByUser(String authenticationToken, int ownerID,
                                         Callback<ArrayList<Event>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getFinishedEventsCreatedByUser(authentication, ownerID).enqueue(callback);
    }

    public void getActiveEventsCreatedByUser(String authenticationToken, int ownerID,
                                               Callback<ArrayList<Event>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getActiveEventsCreatedByUser(authentication, ownerID).enqueue(callback);
    }

    public void getFutureEventsCreatedByUser(String authenticationToken, int ownerID,
                                               Callback<ArrayList<Event>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getFutureEventsCreatedByUser(authentication, ownerID).enqueue(callback);
    }

    public void createEvent(String authenticationToken, CreatedEvent event, Callback<Event> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.createEvent(authentication, event).enqueue(callback);
    }

    public void deleteEvent(String authenticationToken, int eventID, Callback<Void> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.deleteEvent(authentication, eventID).enqueue(callback);
    }

    public void editEvent(String authenticationToken, int eventID, CreatedEvent editedEvent,
                          Callback<Event> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.editEvent(authentication, eventID, editedEvent).enqueue(callback);
    }

    /*
     * Friends
     */
    public void sendFriendRequest(String authenticationToken, int userID,
                                  Callback<QueryResponse> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.sendFriendRequest(authentication, userID).enqueue(callback);
    }

    public void getFriendRequests(String authenticationToken, Callback<ArrayList<UserProfile>> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.getFriendRequests(authentication).enqueue(callback);
    }

    public void acceptFriendRequest(String authenticationToken, int userID,
                                    Callback<QueryResponse> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.acceptFriendRequest(authentication, userID).enqueue(callback);
    }

    public void declineFriendRequest(String authenticationToken, int userID,
                                     Callback<QueryResponse> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.declineFriendRequest(authentication, userID).enqueue(callback);
    }

    /*
     * Assistances
     */
    public void attendEvent(String authenticationToken, int userID, int eventID,
                            Callback<Void> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.attendEvent(authentication, userID, eventID).enqueue(callback);
    }

    public void unattendEvent(String authenticationToken, int userID, int eventID,
                            Callback<QueryResponse> callback) {
        String authentication = this.addBearerAuthenticationToken(authenticationToken);
        this.service.unattendEvent(authentication, userID, eventID).enqueue(callback);
    }
}
