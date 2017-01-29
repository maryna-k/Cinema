package com.example.android.moviesapp.utilities;

import android.util.Log;

import com.example.android.moviesapp.Movie;
import com.example.android.moviesapp.review.Review;
import com.example.android.moviesapp.trailer.YouTubeTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.android.moviesapp.database.MovieProvider.LOG_TAG;


public class JsonParser {

    public static ArrayList<Movie> getMovieDataFromJson(String movieJsonStr) throws JSONException {

        final String RESULTS = "results";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String RATING = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String IMAGE_ADDRESS = "poster_path";
        final String TMDB_ID = "id";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        ArrayList<Movie> movieList = new ArrayList<>();

        for (int i = 0; i < movieArray.length(); i++) {

            Movie movie = new Movie();
            JSONObject movieObject = movieArray.getJSONObject(i);

            movie.setTitle(movieObject.getString(TITLE));
            movie.setOverview(movieObject.getString(OVERVIEW));
            movie.setRating(movieObject.getDouble(RATING));
            movie.setReleaseDate(movieObject.getString(RELEASE_DATE));
            movie.setImageAddress(movieObject.getString(IMAGE_ADDRESS));
            movie.setMdb_id(movieObject.getLong(TMDB_ID));

            movieList.add(i, movie);
            Log.v(LOG_TAG, movie.toString());
        }
        return movieList;
    }

    public static ArrayList<YouTubeTrailer> getTrailerDataFromJson(String trailerJsonStr) throws JSONException{

        ArrayList<YouTubeTrailer> trailerList = new ArrayList<>();
        final String RESULTS = "results";
        final String KEY = "key";
        final String TYPE = "type";

        JSONObject trailerJson = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray(RESULTS);

        for (int i = 0; i < trailerArray.length(); i++) {

            JSONObject trailerObject = trailerArray.getJSONObject(i);

            if(trailerObject.getString(TYPE).equals("Trailer")){
                YouTubeTrailer trailer = new YouTubeTrailer(trailerObject.getString(KEY));
                trailerList.add(trailer);
                Log.v(LOG_TAG, "Trailer key: " +  trailer.getKey());
            }
        }
        return trailerList;
    }

    public static ArrayList<Review> getReviewDataFromJson(String reviewJsonStr) throws JSONException{

        ArrayList<Review> reviewList = new ArrayList<>();
        final String RESULTS = "results";
        final String ID = "id";
        final String AUTHOR = "author";
        final String CONTENT = "content";

        JSONObject reviewJson = new JSONObject(reviewJsonStr);
        JSONArray reviewArray = reviewJson.getJSONArray(RESULTS);

        for (int i = 0; i < reviewArray.length(); i++) {

            JSONObject reviewObject = reviewArray.getJSONObject(i);

            String id = reviewObject.getString(ID);
            String author = reviewObject.getString(AUTHOR);
            String content = reviewObject.getString(CONTENT);
            Review review = new Review(id, author, content);
            reviewList.add(review);
            Log.v(LOG_TAG, review.toString());
        }
        return reviewList;
    }
}
