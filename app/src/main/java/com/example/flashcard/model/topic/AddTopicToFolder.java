package com.example.flashcard.model.topic;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddTopicToFolder {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<Topic> data;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public List<Topic> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
