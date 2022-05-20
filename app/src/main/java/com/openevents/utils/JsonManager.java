package com.openevents.utils;

import com.google.gson.Gson;
import com.openevents.api.responses.User;

import java.io.Serializable;

public class JsonManager {
    public static String toJSON(Serializable object) {
        return new Gson().toJson(object);
    }

    public static User getUserFromJson(String json) {
        return new Gson().fromJson(json, User.class);
    }
}
