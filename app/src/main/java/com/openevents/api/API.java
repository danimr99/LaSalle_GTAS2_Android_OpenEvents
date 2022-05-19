package com.openevents.api;

import com.openevents.api.responses.AuthenticationToken;
import com.openevents.api.responses.RegisteredUser;
import com.openevents.api.responses.UserProfile;
import com.openevents.model.User;
import com.openevents.model.UserSession;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {
    @POST("users/login")
    Call<AuthenticationToken> login(@Body UserSession userSession);

    @POST("users")
    Call<RegisteredUser> register(@Body User user);

    @GET("users")
    Call<ArrayList<UserProfile>> getUsers(@Header("Authorization") String authenticationToken);

    @GET("users/search")
    Call<ArrayList<User>> getUserByEmail(@Header("Authorization") String authenticationToken,
                              @Query("s") String search);

}