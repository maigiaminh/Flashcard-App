package com.example.flashcard.repository;

import com.example.flashcard.utils.UnsplashResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UnsplashApiService {
    @GET("search/photos")
    Call<UnsplashResponse> searchPhotos(
            @Query("client_id") String clientId,
            @Query("query") String query
    );
}
