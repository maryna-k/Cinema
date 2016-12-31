package com.example.android.moviesapp.database;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class MovieProvider extends ContentProvider {

    public static final String LOG_TAG = MovieProvider.class.getSimpleName();

    //Database helper object
    private MovieDbHelper mDbHelper;

    //URI matcher code for the content URI for the favorites table
    private static final int FAVORITE_MOVIES = 100;

    //UriMatcher object to match a content URI to a corresponding code
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer
    static{
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITE_MOVIES, FAVORITE_MOVIES);
    }

    @Override
    public boolean onCreate(){
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor cursor;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        if(match == FAVORITE_MOVIES) {
            cursor = db.query
                    (MovieContract.FavoriteMovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        } else throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        if(match == FAVORITE_MOVIES){
            return insertFavoriteMovie(uri, values);
        } else{
            throw new IllegalArgumentException("Insertion is not supported for uri " + uri);
        }
    }

    private Uri insertFavoriteMovie(Uri uri, ContentValues values){
        String title = values.getAsString(MovieContract.FavoriteMovieEntry.COLUMN_NAME_TITLE);
        /*String genre = values.getAsString(MovieContract.FavoriteMovieEntry.COLUMN_NAME_GENRE);
        String imageAddress = values.getAsString(MovieContract.FavoriteMovieEntry.COLUMN_NAME_IMAGE_ADDRESS);
        Double rating = values.getAsDouble(MovieContract.FavoriteMovieEntry.COLUMN_NAME_RATING);
        String releaseDate = values.getAsString(MovieContract.FavoriteMovieEntry.COLUMN_NAME_RELEASE);
        String overview = values.getAsString(MovieContract.FavoriteMovieEntry.COLUMN_NAME_RELEASE);*/
        Long mdbId = values.getAsLong(MovieContract.FavoriteMovieEntry.COLUMN_NAME_MDB_ID);

        if(title == null || title.equals("")) throw new IllegalArgumentException("Movie has no title");

        /**TODO: Check if mdbId is not null */

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(MovieContract.FavoriteMovieEntry.TABLE_NAME, null, values);

        if(id == -1) {
            Log.v(LOG_TAG, "Failed to insert row for uri " + uri);
            Toast.makeText(getContext(), "Movie was not saved to Favorites", Toast.LENGTH_SHORT).show();
            return null;
        }

        Toast.makeText(getContext(), "Movie was succesfully saved", Toast.LENGTH_SHORT).show();
        Uri itemUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(uri, null);
        return itemUri;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return " ";
    }
}






















