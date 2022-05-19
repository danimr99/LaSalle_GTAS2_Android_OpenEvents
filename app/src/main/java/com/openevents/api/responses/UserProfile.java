package com.openevents.api.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private int id;
    private String name;
    @SerializedName("last_name")
    private String lastName;
    private String email;
    private String image;

    public UserProfile(int id, String name, String lastName, String email, String image) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }
}
