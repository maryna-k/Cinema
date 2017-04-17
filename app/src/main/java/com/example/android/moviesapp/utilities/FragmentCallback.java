package com.example.android.moviesapp.utilities;

import com.example.android.moviesapp.models.Movie;

//DetailFragmentCallback for when an item has been selected.
public interface FragmentCallback {
    public void onItemSelected(Movie selectedMovie);
}
