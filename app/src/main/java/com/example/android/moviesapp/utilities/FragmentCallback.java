package com.example.android.moviesapp.utilities;

import com.example.android.moviesapp.models.Movie;

/** Interface that sends a callback to MainActivity when a movie from the movie grid is clicked
 * in MainGridFragment or FavoriteGridFragment*/
public interface FragmentCallback {
    public void onItemSelected(Movie selectedMovie);
}
