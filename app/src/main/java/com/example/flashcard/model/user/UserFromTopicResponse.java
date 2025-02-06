package com.example.flashcard.model.user;

import com.example.flashcard.model.topic.Topic;
import com.google.gson.annotations.SerializedName;

public class UserFromTopicResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private User data;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public User getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
