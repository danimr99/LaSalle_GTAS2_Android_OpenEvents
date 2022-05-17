package com.openevents.api.responses;

import java.io.Serializable;

public class AuthToken implements Serializable {
    private String accessToken;

    public AuthToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
