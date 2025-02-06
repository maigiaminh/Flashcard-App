package com.example.flashcard.utils;

import com.google.gson.annotations.SerializedName;

public class UnsplashPhoto {
    String id;
    @SerializedName("urls")

    UnsplashUrls urls;

    public String getId() {
        return id;
    }

    public UnsplashUrls getUrls() {
        return urls;
    }
}