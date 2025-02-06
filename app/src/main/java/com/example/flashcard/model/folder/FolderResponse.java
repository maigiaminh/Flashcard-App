package com.example.flashcard.model.folder;

import com.example.flashcard.model.topic.Topic;
import com.google.gson.annotations.SerializedName;

public class FolderResponse {
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
