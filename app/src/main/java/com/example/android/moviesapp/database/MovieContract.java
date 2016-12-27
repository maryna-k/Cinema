package com.example.android.moviesapp.database;

import android.provider.BaseColumns;

public final class MovieContract {

    //the constructor is private to prevent the contract from being accidentally instantiated
    private MovieContract(){}

    /* Inner class that defines the table contents */
    public static class FavoriteMovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_GENRE = "genre";
        public static final String COLUMN_NAME_RELEASE = "release_date";
        public static final String COLUMN_NAME_COUNTRY = "country";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_IMAGE_ADDRESS = "image_address";
        public static final String COLUMN_NAME_MDB_ID = "mdb_id"; //movie database id
    }
}
