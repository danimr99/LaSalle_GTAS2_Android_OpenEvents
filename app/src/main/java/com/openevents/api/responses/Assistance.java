package com.openevents.api.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Assistance implements Serializable {
    @SerializedName("id")
    private int assistantID;
    @SerializedName("name")
    private String assistantName;
    @SerializedName("last_name")
    private String assistantLastName;
    @SerializedName("email")
    private String assistantEmail;
    @SerializedName("puntuation")
    private int eventPunctuation;
    @SerializedName("comentary")
    private String eventComment;

    public Assistance(int assistantID, String assistantName, String assistantLastName,
                      String assistantEmail, int eventPunctuation, String eventComment) {
        this.assistantID = assistantID;
        this.assistantName = assistantName;
        this.assistantLastName = assistantLastName;
        this.assistantEmail = assistantEmail;
        this.eventPunctuation = eventPunctuation;
        this.eventComment = eventComment;
    }

    public int getAssistantID() {
        return assistantID;
    }

    public String getAssistantName() {
        return assistantName;
    }

    public String getAssistantLastName() {
        return assistantLastName;
    }

    public String getAssistantEmail() {
        return assistantEmail;
    }

    public int getEventPunctuation() {
        return eventPunctuation;
    }

    public String getEventComment() {
        return eventComment;
    }
}
