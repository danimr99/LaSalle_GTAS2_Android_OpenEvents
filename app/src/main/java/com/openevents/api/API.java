package com.openevents.api;

import com.openevents.api.responses.AuthToken;
import com.openevents.api.responses.Profile;
import com.openevents.model.User;
import com.openevents.model.UserSession;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface API {
    @POST("users/login")
    Call<AuthToken> login(@Body UserSession userSession);

    @POST("users")
    Call<Profile> register(@Body User user);
}