package com.example.android.moviesapp.mvp.data;


import com.example.android.moviesapp.models.MovieResponse;

import io.reactivex.Single;

public interface DataRepository {
    Single<MovieResponse> loadMovies(String requestType);
}
