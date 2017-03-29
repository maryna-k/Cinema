package com.example.android.moviesapp.utilities;

import com.example.android.moviesapp.Objects.Movie;
import com.example.android.moviesapp.Objects.Review;
import com.example.android.moviesapp.Objects.YouTubeTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static com.example.android.moviesapp.utilities.MDBConnection.searchCategories;


public class JsonParser {

    private static final String LOG_TAG = JsonParser.class.getSimpleName() + "LOG";

    public static ArrayList<Movie> getMovieDataFromJson(String movieJsonStr) throws JSONException {

        if (movieJsonStr == null) return null;

        final String RESULTS = "results";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String VOTE_COUNT = "vote_count";
        final String RELEASE_DATE = "release_date";
        final String DURATION = "runtime";
        final String POSTER_ADDRESS = "poster_path";
        final String BACKDROP_ADDRESS = "backdrop_path";
        final String TMDB_ID = "id";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        ArrayList<Movie> movieList = new ArrayList<>();

        for (int i = 0; i < movieArray.length(); i++) {

            Movie movie = new Movie();
            JSONObject movieObject = movieArray.getJSONObject(i);

            movie.setTitle(movieObject.getString(TITLE));
            movie.setOverview(movieObject.getString(OVERVIEW));
            movie.setRating(movieObject.getDouble(VOTE_AVERAGE));
            movie.setVoteCount(movieObject.getInt(VOTE_COUNT));
            movie.setReleaseDate(movieObject.getString(RELEASE_DATE));
            movie.setPosterAddress(movieObject.getString(POSTER_ADDRESS));
            movie.setBackdropAddress(movieObject.getString(BACKDROP_ADDRESS));
            movie.setMdb_id(movieObject.getLong(TMDB_ID));
            movie.setGenre(getGenresFromJson(movieObject));

            movieList.add(i, movie);
            //Log.v(LOG_TAG, movie.toString());
        }
        return movieList;
    }

    private static String getGenresFromJson(JSONObject movieObject) throws JSONException{
        final String GENRE_ID = "genre_ids";
        String infoStr = "";
        JSONArray genreArray = movieObject.getJSONArray(GENRE_ID);
        for(int i = 0; i < genreArray.length(); i++){
            int id = genreArray.getInt(i);

            for (Map.Entry<String, String> entry : searchCategories.entrySet()) {
                if ((Integer.toString(id)).equals(entry.getValue())) {
                    infoStr = infoStr + entry.getKey();
                    if(i < genreArray.length() - 1) {
                        infoStr = infoStr + ", ";
                    }
                }
            }
        }
        return infoStr;
    }

    public static ArrayList<YouTubeTrailer> getTrailerDataFromJson(String trailerJsonStr) throws JSONException {

        if (trailerJsonStr == null) return null;

        ArrayList<YouTubeTrailer> trailerList = new ArrayList<>();
        final String RESULTS = "results";
        final String KEY = "key";
        final String TYPE = "type";

        JSONObject trailerJson = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray(RESULTS);

        for (int i = 0; i < trailerArray.length(); i++) {

            JSONObject trailerObject = trailerArray.getJSONObject(i);

            if (trailerObject.getString(TYPE).equals("Trailer")) {
                YouTubeTrailer trailer = new YouTubeTrailer(trailerObject.getString(KEY));
                trailerList.add(trailer);
                //Log.v(LOG_TAG, "Trailer key: " +  trailer.getKey());
            }
        }
        return trailerList;
    }

    public static ArrayList<Review> getReviewDataFromJson(String reviewJsonStr) throws JSONException {

        if (reviewJsonStr == null) return null;

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
            //Log.v(LOG_TAG, review.toString());
        }
        return reviewList;
    }
}
