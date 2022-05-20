package com.openevents.api;

public interface ActivityState {
    void loading();
    void onDataReceived();
    void onNoDataReceived();
    void onConnectionFailure();
}
