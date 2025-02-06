package com.example.flashcard.model.topic;

import com.google.gson.annotations.SerializedName;

public class UpdateTopicResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private TopicDetail data;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public TopicDetail getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
