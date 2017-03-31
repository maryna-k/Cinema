package com.example.android.moviesapp.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.example.android.moviesapp.Objects.Movie;
import com.example.android.moviesapp.database.MovieContract.FavoriteMovieEntry;
import com.example.android.moviesapp.database.MovieContract.ReviewsTableEntry;
import com.example.android.moviesapp.Objects.Review;
import com.example.android.moviesapp.utilities.ImageUtils;
import com.example.android.moviesapp.utilities.Keys;

import java.util.ArrayList;

public class DatabaseUtilMethods {

    //check if the Movie object downloaded from server is already saved in favorite_movies table
    public static boolean movieIsInFavorite(long tmdb_id, Context context) {
        Uri uri = FavoriteMovieEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(tmdb_id)).build();
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount() == 0) {
            return false;
        }
        cursor.moveToFirst();
        return true;
    }

    //insert Movie object to favorite_movies table
    public static boolean saveFavoriteMovie(Movie movie, ArrayList<Review> reviews, Context context) {
        Long tmdb_id = movie.getTmdb_id();
        String posterAddress = movie.getPosterAddress();
        String posterPath;
        if(posterAddress.equals("") || posterAddress.equals("null") || posterAddress == null) {
            posterPath = null;
        }
        else {
            String fullPosterAddress = Keys.SMALL_POSTER_BASE_URL + posterAddress;
            posterPath = ImageUtils.savePosterToInternalStorage(fullPosterAddress, Long.toString(tmdb_id), context);
        }
        ContentValues values = new ContentValues();
        values.put(FavoriteMovieEntry.COLUMN_NAME_TITLE, movie.getTitle());
        values.put(FavoriteMovieEntry.COLUMN_NAME_OVERVIEW, movie.getOverview());
        values.put(FavoriteMovieEntry.COLUMN_NAME_GENRE, movie.getGenre());
        values.put(FavoriteMovieEntry.COLUMN_NAME_RATING, movie.getRating());
        values.put(FavoriteMovieEntry.COLUMN_NAME_VOTE_COUNT, movie.getVoteCount());
        values.put(FavoriteMovieEntry.COLUMN_NAME_RELEASE, movie.getReleaseDate());
        values.put(FavoriteMovieEntry.COLUMN_NAME_POSTER_ADDRESS, posterAddress);
        values.put(FavoriteMovieEntry.COLUMN_NAME_POSTER_STORAGE_PATH, posterPath);
        values.put(FavoriteMovieEntry.COLUMN_NAME_BACKDROP_ADDRESS, movie.getBackdropAddress());
        values.put(FavoriteMovieEntry.COLUMN_NAME_TMDB_ID, tmdb_id);

        Uri movieUri = context.getContentResolver().insert(FavoriteMovieEntry.CONTENT_URI, values);
        if (movieUri != null) {
            saveMovieReview(reviews, tmdb_id, context);
            Toast.makeText(context, "Movie was added to Favorites",
                    Toast.LENGTH_SHORT)
                    .show();
            return true;
        } else {
            Toast.makeText(context, "Oops... Movie was not saved", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //insert reviews to database
    public static void saveMovieReview(ArrayList<Review> reviewList, Long movieID, Context context){
        if(reviewList != null){
            for(int i = 0; i < reviewList.size(); i++) {
                String reviewerName = reviewList.get(i).getAuthor();
                String reviewText = reviewList.get(i).getReviewContent();
                ContentValues values = new ContentValues();
                values.put(ReviewsTableEntry.COLUMN_NAME_REVIEWER_NAME, reviewerName);
                values.put(ReviewsTableEntry.COLUMN_NAME_REVIEW_TEXT, reviewText);
                values.put(ReviewsTableEntry.COLUMN_NAME_TMDB_ID, movieID);

                Uri reviewUri = context.getContentResolver().insert(ReviewsTableEntry.CONTENT_URI, values);
            }
        }
    }

    public static int deleteFavoriteMovie(long tmdb_id, Context context){
        Uri uri = FavoriteMovieEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(tmdb_id)).build();
        int removedNum = context.getContentResolver().delete(uri, null, null);
        return removedNum;
    }

    //return movie from specified position of cursor
    public static Movie getMovieFromCursor(int position, Cursor cursor){
        cursor.moveToPosition(position);
        String posterAddress = cursor.getString
                (cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_POSTER_ADDRESS));
        String backdropAddress = cursor.getString
                (cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_BACKDROP_ADDRESS));
        String title = cursor.getString
                (cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_TITLE));
        String genre = cursor.getString
                (cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_GENRE));
        String overview = cursor.getString
                (cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_OVERVIEW));
        String releaseDay = cursor.getString
                (cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_RELEASE));
        double rating = cursor.getDouble
                (cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_RATING));
        int voteCount = cursor.getInt
                (cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_VOTE_COUNT));
        long tmdb_id = cursor.getLong
                (cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_TMDB_ID));
        String posterStoragePath = cursor.getString
                (cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_POSTER_STORAGE_PATH));
        Movie movie = new Movie(title, overview, rating, voteCount, genre, releaseDay,
                posterAddress, posterStoragePath, backdropAddress, tmdb_id);
        return movie;
    }

}
