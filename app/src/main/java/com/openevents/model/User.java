package com.openevents.model;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String image;

    public User(String name, String lastName, String email, String password) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public User(String name, String lastName, String email, String password, String image) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.image = image;
    }


}
