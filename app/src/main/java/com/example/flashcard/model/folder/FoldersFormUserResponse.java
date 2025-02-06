package com.example.flashcard.model.folder;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;
import java.util.List;

public class FoldersFormUserResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<Folder> data;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public List<Folder> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
