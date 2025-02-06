package com.example.flashcard.model.folder;

import com.example.flashcard.model.topic.TopicDetail;
import com.google.gson.annotations.SerializedName;

public class DeleteFolder {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private String data;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public String getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
