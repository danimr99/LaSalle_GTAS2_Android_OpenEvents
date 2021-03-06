package com.openevents.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.openevents.api.responses.User;
import com.openevents.constants.Constants;

import java.util.HashMap;
import java.util.Map;

public class SharedPrefs {
    private static SharedPrefs sharedPrefs;
    private SharedPreferences sharedPreferences;

    public SharedPrefs(Context context) {
        this.sharedPreferences = context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
    }

    public static SharedPrefs getInstance(Context context) {
        if(sharedPrefs == null) {
            sharedPrefs = new SharedPrefs(context);
        }

        return sharedPrefs;
    }

    public void addStringEntry(String key, String content) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(key, content);
        editor.apply();
    }

    public void removeStringEntry(String key) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void overrideStringEntry(String key, String content) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.remove(key);
        editor.putString(key, content);
        editor.apply();
    }

    public String getStringEntry(String key) {
        return this.sharedPreferences.getString(key, "");
    }

    public String getAuthenticationToken() {
        return this.getStringEntry(Constants.AUTHENTICATION_TOKEN_SHARED_PREFERENCES);
    }

    public User getUser() {
        return JsonManager.getUserFromJson(this.getStringEntry(Constants.USER_SHARED_PREFERENCES));
    }

    public void saveUser(User user) {
        this.addStringEntry(Constants.USER_SHARED_PREFERENCES, JsonManager.toJSON(user));
    }

    public void logout() {
        this.removeStringEntry(Constants.USER_SHARED_PREFERENCES);
        this.removeStringEntry(Constants.USER_EMAIL_SHARED_PREFERENCES);
        this.removeStringEntry(Constants.AUTHENTICATION_TOKEN_SHARED_PREFERENCES);
    }
}
