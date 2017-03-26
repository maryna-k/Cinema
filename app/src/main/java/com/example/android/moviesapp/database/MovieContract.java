package com.example.android.moviesapp.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class MovieContract {

    //the constructor is private to prevent the contract from being accidentally instantiated
    private MovieContract(){}

    /*content authority*/
    public static final String CONTENT_AUTHORITY = "com.example.android.moviesapp";

    /*Base Uri without path*/
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*Path to Favorites table*/
    public static final String PATH_FAVORITE_MOVIES = FavoriteMovieEntry.TABLE_NAME;

    /* Inner class that defines the content of Favorite movies table */
    public static class FavoriteMovieEntry implements BaseColumns {

        /*Content Uri to Favorite movies table*/
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FAVORITE_MOVIES);

        /**The MIME type of the {@link #CONTENT_URI} for a list of favorite movies*/
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_FAVORITE_MOVIES;

        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_VOTE_COUNT = "vote_count";
        public static final String COLUMN_NAME_GENRE = "genre";
        public static final String COLUMN_NAME_RELEASE = "release_date";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_POSTER_ADDRESS = "poster_address";
        public static final String COLUMN_NAME_POSTER_STORAGE_PATH = "poster_storage_path";
        public static final String COLUMN_NAME_BACKDROP_ADDRESS = "backdrop_address";
        public static final String COLUMN_NAME_MDB_ID = "mdb_id"; //movie database id

        public static final String PATH_FAVORITE_MOVIES_TMDB_ID = COLUMN_NAME_MDB_ID;
    }
}
