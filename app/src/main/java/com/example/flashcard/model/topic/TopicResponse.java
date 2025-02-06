package com.example.flashcard.model.topic;

import com.example.flashcard.model.user.User;
import com.example.flashcard.model.vocabulary.Vocabulary;
import com.google.gson.annotations.SerializedName;

public class TopicResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Topic data;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public Topic getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
