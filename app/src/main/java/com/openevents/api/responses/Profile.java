package com.openevents.api.responses;

import java.io.Serializable;

public class Profile implements Serializable {
    private String name;
    private String last_name;
    private String email;
    private String image;

    public String getName() {
        return name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }
}
