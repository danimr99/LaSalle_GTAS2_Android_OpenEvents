package com.openevents.api.responses;

import java.io.Serializable;

public class AuthenticationToken implements Serializable {
    private String accessToken;

    public AuthenticationToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
