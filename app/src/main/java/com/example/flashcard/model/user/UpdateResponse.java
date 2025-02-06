package com.example.flashcard.model.user;

import com.google.gson.annotations.SerializedName;

public class UpdateResponse {
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
