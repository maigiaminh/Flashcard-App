package com.example.flashcard.utils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UnsplashResponse {
    @SerializedName("results")

    List<UnsplashPhoto> results;

    public List<UnsplashPhoto> getResults() {
        return results;
    }
}
