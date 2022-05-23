package com.openevents.api.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreatedEvent implements Serializable {
    private String name;
    private String image;
    private String location;
    private String description;
    @SerializedName("eventStart_date")
    private String eventStartDate;
    @SerializedName("eventEnd_date")
    private String eventEndDate;
    @SerializedName("n_participators")
    private int numberParticipants;
    @SerializedName("type")
    private String category;

    public CreatedEvent(String name, String image, String location, String description,
                        String eventStartDate, String eventEndDate, int numberParticipants, String category) {
        this.name = name;
        this.image = image;
        this.location = location;
        this.description = description;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.numberParticipants = numberParticipants;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getEventStartDate() {
        return eventStartDate;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public int getNumberParticipants() {
        return numberParticipants;
    }

    public String getCategory() {
        return category;
    }
}
