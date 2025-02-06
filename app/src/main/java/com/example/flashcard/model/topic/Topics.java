package com.example.flashcard.model.topic;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Topics {
    @SerializedName("TopicID")
    private int TopicID;

    @SerializedName("UserID")
    private int UserID;

    @SerializedName("additionalInfo")
    private List<Topic> additionalInfo;

    public int getTopicID() {
        return TopicID;
    }

    public int getUserID() {
        return UserID;
    }

    public List<Topic> getAdditionalInfo() {
        return additionalInfo;
    }
}
