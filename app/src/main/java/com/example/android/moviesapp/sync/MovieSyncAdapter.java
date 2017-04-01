package com.example.android.moviesapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.android.moviesapp.Objects.Movie;
import com.example.android.moviesapp.Objects.Review;
import com.example.android.moviesapp.R;
import com.example.android.moviesapp.database.DatabaseUtilMethods;

import org.json.JSONException;

import java.util.ArrayList;

import static com.example.android.moviesapp.database.MovieContract.FavoriteMovieEntry;
import static com.example.android.moviesapp.database.MovieContract.ReviewsTableEntry;
import static com.example.android.moviesapp.utilities.JsonParser.getReviewDataFromJson;
import static com.example.android.moviesapp.utilities.JsonParser.getSingleMovieFromJson;
import static com.example.android.moviesapp.utilities.MDBConnection.LOAD_MOVIE_BY_ID;
import static com.example.android.moviesapp.utilities.MDBConnection.LOAD_REVIEWS;
import static com.example.android.moviesapp.utilities.MDBConnection.getJsonResponse;


public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60*1140;// sync every 24 hours
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");
        //query movies in database
        Cursor cursor = getContext().getContentResolver().query(FavoriteMovieEntry.CONTENT_URI, null, null, null, null, null);
        /*go through cursor, compare database and server movies one by one, update movie if data was changed
        * and update reviews*/
        if(cursor != null) {
            int position = -1;
            cursor.moveToPosition(position);
            while (cursor.moveToNext()) {
                position++;
                Movie dbMovie = DatabaseUtilMethods.getMovieFromCursor(position, cursor);
                long tmdb_id = dbMovie.getTmdb_id();
                Movie serverMovie = loadMovieFromServer(tmdb_id);
                updateMovie(dbMovie, serverMovie);
                updateMovieReviews(tmdb_id);
            }
        }
        cursor.close();
    }

    private Movie loadMovieFromServer(long tmdb_id){
        String movieJsonStr = getJsonResponse(LOAD_MOVIE_BY_ID, tmdb_id);
        try {
            return getSingleMovieFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    /*Compares movie from the database and movie from server and updates the database
    * with the fields that are changed*/
    private void updateMovie(Movie m1, Movie m2){
        ContentValues values = new ContentValues();

        values.put(FavoriteMovieEntry.COLUMN_NAME_TITLE, m2.getTitle());
        values.put(FavoriteMovieEntry.COLUMN_NAME_TMDB_ID, m2.getTmdb_id());
        if(!m1.getOverview().equals(m2.getOverview()))
            values.put(FavoriteMovieEntry.COLUMN_NAME_OVERVIEW, m2.getOverview());
        if(!m1.getGenre().equals(m2.getGenre()))
            values.put(FavoriteMovieEntry.COLUMN_NAME_GENRE, m2.getGenre());
        if(!m1.getReleaseDate().equals(m2.getReleaseDate()))
            values.put(FavoriteMovieEntry.COLUMN_NAME_RELEASE, m2.getReleaseDate());
        if(m1.getRating() != m2.getRating())
            values.put(FavoriteMovieEntry.COLUMN_NAME_RATING, m2.getRating());
        if(m1.getVoteCount() != m2.getVoteCount())
            values.put(FavoriteMovieEntry.COLUMN_NAME_VOTE_COUNT, m2.getVoteCount());
        if(!m1.getPosterAddress().equals(m2.getPosterAddress())) {
            values.put(FavoriteMovieEntry.COLUMN_NAME_POSTER_ADDRESS, m2.getPosterAddress());
        }
        if(!m1.getBackdropAddress().equals(m2.getBackdropAddress())) {
            values.put(FavoriteMovieEntry.COLUMN_NAME_BACKDROP_ADDRESS, m2.getBackdropAddress());
        }
        if(values.size() > 1) {
            Uri uri = FavoriteMovieEntry.CONTENT_URI.buildUpon().appendPath
                    (Long.toString(m1.getTmdb_id())).build();
            getContext().getContentResolver().update(uri, values, null, null);
        }
    }

    /*removes old reviews and calls a method to update database with new reviews*/
    private void updateMovieReviews(long tmdb_id){
        ArrayList<Review> arr = loadReviewsFromServer(tmdb_id);
        if(arr != null){
            String selection = ReviewsTableEntry.COLUMN_NAME_TMDB_ID + "=?";
            String[] selectionArgs = new String[]{Long.toString(tmdb_id)};
            getContext().getContentResolver().delete
                    (ReviewsTableEntry.CONTENT_URI,
                    selection,
                    selectionArgs);

            DatabaseUtilMethods.saveMovieReview(arr, tmdb_id, getContext());
        }
    }

    private ArrayList<Review> loadReviewsFromServer(long tmdb_id){
            String reviewJsonStr = getJsonResponse(LOAD_REVIEWS, tmdb_id);
            try {
                return getReviewDataFromJson(reviewJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // enable inexact timers in the periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
