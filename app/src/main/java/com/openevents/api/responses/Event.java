package com.openevents.api.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Event implements Serializable {
    @SerializedName("id")
    private int id;
    private String name;
    @SerializedName("owner_id")
    private int ownerId;
    private String date;
    private String image;
    private String location;
    private String description;
    @SerializedName("eventStart_date")
    private String eventStartDate;
    @SerializedName("eventEnd_date")
    private String eventEndDate;
    @SerializedName("n_participators")
    private int participatorsQuantity;
    private String slug;
    private String type;
    @SerializedName("avg_score")
    private String averageScore;

    public Event(int id, String name, int ownerId, String date, String image, String location,
                 String description, String eventStartDate, String eventEndDate,
                 int participatorsQuantity, String slug, String type, String averageScore) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.date = date;
        this.image = image;
        this.location = location;
        this.description = description;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.participatorsQuantity = participatorsQuantity;
        this.slug = slug;
        this.type = type;
        this.averageScore = averageScore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getDate() {
        return date;
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

    public int getParticipatorsQuantity() {
        return participatorsQuantity;
    }

    public String getSlug() {
        return slug;
    }

    public String getType() {
        return type;
    }

    public String getAverageScore() {
        return averageScore;
    }
}
