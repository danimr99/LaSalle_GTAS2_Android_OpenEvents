package com.openevents.model;

import java.io.Serializable;

public class UserSession implements Serializable {
    private String email;
    private String password;

    public UserSession(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
