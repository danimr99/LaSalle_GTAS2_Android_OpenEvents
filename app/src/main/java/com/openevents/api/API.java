package com.openevents.api;

import com.openevents.api.requests.CreatedUser;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.Event;
import com.openevents.api.responses.RegisteredUser;
import com.openevents.api.responses.UserProfile;
import com.openevents.api.responses.User;
import com.openevents.api.requests.UserSession;
import com.openevents.api.responses.UserStats;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {
    /*
     * Authentication
     */
    @POST("users/login")
    Call<AuthenticationToken> login(@Body UserSession userSession);

    @POST("users")
    Call<RegisteredUser> register(@Body CreatedUser user);

    /*
     * Users
     */
    @GET("users")
    Call<ArrayList<UserProfile>> getUsers(@Header("Authorization") String authenticationToken);

    @GET("users/search")
    Call<ArrayList<User>> getUserByEmail(@Header("Authorization") String authenticationToken,
                              @Query("s") String search);

    @GET("users/{userID}")
    Call<ArrayList<User>> getUserById(@Header("Authorization") String authenticationToken,
                                 @Path("userID") int userID);

    @GET("users/{userID}/statistics")
    Call<UserStats> getUserStats(@Header("Authorization") String authenticationToken,
                                 @Path("userID") int userID);

    @GET("friends")
    Call<ArrayList<User>> getFriends(@Header("Authorization") String authenticationToken);

    @GET("friends/requests")
    Call<ArrayList<User>> getFriendRequests(@Header("Authorization") String authenticationToken);

    /*
     * Events
     */
    @GET("events")
    Call<ArrayList<Event>> getEvents(@Header("Authorization") String authenticationToken);

    @GET("events/best")
    Call<ArrayList<Event>> getPopularEvents(@Header("Authorization") String authenticationToken);

    @GET("users/{userID}/events/future")
    Call<ArrayList<Event>> getUserFutureEvents(@Header("Authorization") String authenticationToken,
                                               @Path("userID") int userID);

    @GET("users/{userID}/events/finished")
    Call<ArrayList<Event>> getUserFinishedEvents(@Header("Authorization") String authenticationToken,
                                                 @Path("userID") int userID);

    @GET("users/{userID}/events/current")
    Call<ArrayList<Event>> getUserCurrentEvents(@Header("Authorization") String authenticationToken,
                                                @Path("userID") int userID);

}