package com.openevents.api;

import com.openevents.api.requests.CreatedEvent;
import com.openevents.api.requests.CreatedUser;
import com.openevents.api.responses.Assistance;
import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.Event;
import com.openevents.api.responses.FriendshipResponse;
import com.openevents.api.responses.RegisteredUser;
import com.openevents.api.responses.UserProfile;
import com.openevents.api.responses.User;
import com.openevents.api.requests.UserSession;
import com.openevents.api.responses.UserStats;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET("users/{userID}/friends")
    Call<ArrayList<UserProfile>> getUserFriends(@Header("Authorization") String authenticationToken,
                                         @Path("userID") int userID);

    @DELETE("users")
    Call<Void> deleteAccount(@Header("Authorization") String authenticationToken);

    @GET("users/{ownerID}/events/finished")
    Call<ArrayList<Event>> getFinishedEventsCreatedByUser(@Header("Authorization") String authenticationToken,
                                                          @Path("ownerID") int ownerID);

    @GET("users/{ownerID}/events/current")
    Call<ArrayList<Event>> getActiveEventsCreatedByUser(@Header("Authorization") String authenticationToken,
                                                          @Path("ownerID") int ownerID);

    @GET("users/{ownerID}/events/future")
    Call<ArrayList<Event>> getFutureEventsCreatedByUser(@Header("Authorization") String authenticationToken,
                                                          @Path("ownerID") int ownerID);

    @PUT("users")
    Call<RegisteredUser> editAccount(@Header("Authorization") String authenticationToken,
                                                @Body CreatedUser createdUser);

    /*
     * Events
     */
    @GET("events")
    Call<ArrayList<Event>> getEvents(@Header("Authorization") String authenticationToken);

    @GET("events/best")
    Call<ArrayList<Event>> getPopularEvents(@Header("Authorization") String authenticationToken);

    @GET("events/{eventID}/assistances")
    Call<ArrayList<Assistance>> getEventAssistances(@Header("Authorization") String authenticationToken,
                                                    @Path("eventID") int eventID);

    @POST("events")
    Call<Event> createEvent(@Header("Authorization") String authenticationToken,
                            @Body CreatedEvent event);

    @DELETE("events/{eventID}")
    Call<Void> deleteEvent (@Header("Authorization") String authenticationToken,
                            @Path("eventID") int eventID);

    @PUT("events/{eventID}")
    Call<Event> editEvent (@Header("Authorization") String authenticationToken,
                            @Path("eventID") int eventID);

    /*
     * Friends
     */
    @POST("friends/{userID}")
    Call<FriendshipResponse> sendFriendRequest(@Header("Authorization") String authenticationToken,
                                               @Path("userID") int userID);

    @GET("friends/requests")
    Call<ArrayList<UserProfile>> getFriendRequests(@Header("Authorization") String authenticationToken);

    @PUT("friends/{userID}")
    Call<FriendshipResponse> acceptFriendRequest(@Header("Authorization") String authenticationToken,
                            @Path("userID") int userID);

    @DELETE("friends/{userID}")
    Call<FriendshipResponse> declineFriendRequest(@Header("Authorization") String authenticationToken,
                                                  @Path("userID") int userID);
}