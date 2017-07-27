package com.example.android.moviesapp.mvp.movieGrid;

import com.example.android.moviesapp.models.Movie;

import java.util.ArrayList;

public interface MovieGridContract {
    interface Presenter{
        void loadMovies(String movieType);
    }

    interface View {
        void showMovies(ArrayList<Movie> list);
        void setEmptyGridViewVisible(boolean visible);
        void loadNextDataFromApi();
        void updateMovieList();
    }
}
