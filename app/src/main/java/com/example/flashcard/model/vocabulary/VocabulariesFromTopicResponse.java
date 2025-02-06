package com.example.flashcard.model.vocabulary;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VocabulariesFromTopicResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<Vocabulary> data;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public List<Vocabulary> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
