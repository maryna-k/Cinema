package com.example.android.moviesapp.mvp.rest;


import com.example.android.moviesapp.models.MovieResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDBService {

    @GET("movie/{popularity}?")
    Single<MovieResponse> getMoviesByPopularity(@Path("popularity") String popularity, @Query("api_key") String apiKey,
                                                @Query("page") int pageNum);

    @GET("discover/movie?")
    Single<MovieResponse> getMoviesByGenre(@Query("api_key") String apiKey, @Query("with_genres") String genre,
                                           @Query("page") int pageNum);

}
