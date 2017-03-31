package com.example.android.moviesapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Cinema.db";

    public MovieDbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        final String SQL_CREATE_FAVORITES_TABLE =
                "CREATE TABLE " + MovieContract.FavoriteMovieEntry.TABLE_NAME + " (" +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_TMDB_ID + " LONG PRIMARY KEY," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_GENRE + " TEXT," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_RELEASE + " TEXT," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_RATING + " DOUBLE," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_VOTE_COUNT + " INTEGER," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_OVERVIEW + " TEXT," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_POSTER_ADDRESS + " TEXT," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_POSTER_STORAGE_PATH + " TEXT," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_BACKDROP_ADDRESS + " TEXT)";

        final String SQL_CREATE_REVIEWS_TABLE =
                "CREATE TABLE " + MovieContract.ReviewsTableEntry.TABLE_NAME + " (" +
                        MovieContract.ReviewsTableEntry._ID + " INTEGER PRIMARY KEY," +
                        MovieContract.ReviewsTableEntry.COLUMN_NAME_TMDB_ID + " LONG NOT NULL," +
                        MovieContract.ReviewsTableEntry.COLUMN_NAME_REVIEWER_NAME + " TEXT," +
                        MovieContract.ReviewsTableEntry.COLUMN_NAME_REVIEW_TEXT + " TEXT NOT NULL," +
                        "FOREIGN KEY (" + MovieContract.ReviewsTableEntry.COLUMN_NAME_TMDB_ID + ") REFERENCES " +
                        MovieContract.FavoriteMovieEntry.TABLE_NAME + " (" +
                        MovieContract.FavoriteMovieEntry.COLUMN_NAME_TMDB_ID + ") ON DELETE CASCADE)";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1){
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteMovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewsTableEntry.TABLE_NAME);
        onCreate(db);
    }
}
