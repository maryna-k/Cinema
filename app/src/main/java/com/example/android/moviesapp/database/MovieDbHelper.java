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
                MovieContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_MDB_ID + " LONG NOT NULL," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_COUNTRY + " TEXT," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_GENRE + " TEXT," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_RELEASE + " TEXT," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_RATING + " DOUBLE," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_OVERVIEW + " TEXT," +
                MovieContract.FavoriteMovieEntry.COLUMN_NAME_IMAGE_ADDRESS + " TEXT)";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1){
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteMovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
