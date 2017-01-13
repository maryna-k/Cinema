package com.example.android.moviesapp.database;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import static com.example.android.moviesapp.database.MovieContract.CONTENT_AUTHORITY;
import static com.example.android.moviesapp.database.MovieContract.FavoriteMovieEntry;
import static com.example.android.moviesapp.database.MovieContract.PATH_FAVORITE_MOVIES;


public class MovieProvider extends ContentProvider {

    public static final String LOG_TAG = MovieProvider.class.getSimpleName();

    //Database helper object
    private MovieDbHelper mDbHelper;
    public static UriMatcher sUriMatcher = buildUriMatcher();

    //URI matcher code for the content URI for the favorites table
    private static final int FAVORITE_MOVIES = 100;

    //URI matcher code of the content URI for a single row in a favorite table
    private static final int FAVORITE_ID = 101;

    //URI matcher code of the content URI for a single row with a specific TMDb_id
    private static final int FAVORITE_MOVIE_TMDB_ID = 103;

    //static initializer
    static{

    }

    public static UriMatcher buildUriMatcher(){
        //UriMatcher object to match a content URI to a corresponding code
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_FAVORITE_MOVIES, FAVORITE_MOVIES);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_FAVORITE_MOVIES + "/#", FAVORITE_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY,
                PATH_FAVORITE_MOVIES + "/" + FavoriteMovieEntry.PATH_FAVORITE_MOVIES_TMDB_ID + "/#",
                FAVORITE_MOVIE_TMDB_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate(){
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    //query the whole database, query by the database _ID or tmdb_id
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor returnCursor;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match){
            case FAVORITE_MOVIES:
                returnCursor = db.query(
                                FavoriteMovieEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case FAVORITE_ID:
                String id = uri.getPathSegments().get(1);
                String Selection = FavoriteMovieEntry._ID + "=?";
                String[] SelectionArgs = new String[]{id};
                returnCursor = db.query(
                        FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE_MOVIE_TMDB_ID:
                String tmdb_id = uri.getPathSegments().get(2);
                String tmdb_selection = FavoriteMovieEntry.COLUMN_NAME_MDB_ID + "=?";
                String[] tmdb_selectionArgs = new String[]{tmdb_id};
                returnCursor = db.query(
                        FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        tmdb_selection,
                        tmdb_selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri returnedUri;

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        switch (match){
            case FAVORITE_MOVIES:
                String title = values.getAsString(MovieContract.FavoriteMovieEntry.COLUMN_NAME_TITLE);
                if(title == null || title.equals("")) throw new IllegalArgumentException("Movie has no title");

                long id = db.insert(MovieContract.FavoriteMovieEntry.TABLE_NAME, null, values);

                if(id > 0) {
                    returnedUri = ContentUris.withAppendedId(FavoriteMovieEntry.CONTENT_URI, id);
                } else {
                    Log.v(LOG_TAG, "Failed to insert a row for the uri " + uri);
                    throw new android.database.SQLException("Failed to insert row for the uri " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);
        return returnedUri;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int moviesDeleted;

        switch (match){
            case FAVORITE_ID:
                String id = uri.getPathSegments().get(1);
                String idSelection = FavoriteMovieEntry._ID + "=?";
                String[] idSelectionArgs = new String[]{id};
                moviesDeleted = db.delete(FavoriteMovieEntry.TABLE_NAME,
                        idSelection,
                        idSelectionArgs);
                break;

            case FAVORITE_MOVIE_TMDB_ID:
                String tmdb_id = uri.getPathSegments().get(2);
                String tmdb_Selection = FavoriteMovieEntry.COLUMN_NAME_MDB_ID + "=?";
                String[] tmdb_SelectionArgs = new String[]{tmdb_id};
                moviesDeleted = db.delete(FavoriteMovieEntry.TABLE_NAME,
                        tmdb_Selection,
                        tmdb_SelectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (moviesDeleted > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return moviesDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return " ";
    }
}






















