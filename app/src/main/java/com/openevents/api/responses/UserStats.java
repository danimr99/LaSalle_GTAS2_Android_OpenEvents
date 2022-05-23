package com.openevents.api.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserStats implements Serializable {
    @SerializedName("avg_score")
    private String averageScore;
    @SerializedName("num_comments")
    private String numberOfComments;
    @SerializedName("percentage_commenters_below")
    private String percentageCommentersBelow;

    public UserStats(String averageScore, String numberOfComments, String percentageCommentersBelow) {
        this.averageScore = averageScore;
        this.numberOfComments = numberOfComments;
        this.percentageCommentersBelow = percentageCommentersBelow;
    }

    public String getAverageScore() {
        return averageScore;
    }

    public String getNumberOfComments() {
        return numberOfComments;
    }

    public String getPercentageCommentersBelow() {
        return percentageCommentersBelow;
    }
}
