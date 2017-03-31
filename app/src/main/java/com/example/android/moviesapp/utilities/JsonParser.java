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

    //parse json with request to get multiple movies of particular genre
    public static ArrayList<Movie> getMovieArrayFromJson(String movieJsonStr) throws JSONException {

        if (movieJsonStr == null) return null;

        final String RESULTS = "results";
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        ArrayList<Movie> movieList = new ArrayList<>();

        for (int i = 0; i < movieArray.length(); i++) {

            JSONObject movieObject = movieArray.getJSONObject(i);
            Movie movie = getMovieFromJsonHelper(movieObject);
            //set genre from json with multiple movies
            movie.setGenre(getGenresFromJson(movieObject));
            movieList.add(i, movie);
        }
        return movieList;
    }

    //parse json from request with movie id
    public static Movie getSingleMovieFromJson(String movieJsonStr) throws JSONException{
        if (movieJsonStr == null) return null;
        JSONObject movieObject = new JSONObject(movieJsonStr);
        Movie movie = getMovieFromJsonHelper(movieObject);
        //set genre from json with single movie
        movie.setGenre(getGenresByMovieID(movieObject));
        return movie;
    }

    private static Movie getMovieFromJsonHelper(JSONObject movieObject) throws JSONException{
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String VOTE_COUNT = "vote_count";
        final String RELEASE_DATE = "release_date";
        final String POSTER_ADDRESS = "poster_path";
        final String BACKDROP_ADDRESS = "backdrop_path";
        final String TMDB_ID = "id";

        Movie movie = new Movie();
        movie.setTitle(movieObject.getString(TITLE));
        movie.setOverview(movieObject.getString(OVERVIEW));
        movie.setRating(movieObject.getDouble(VOTE_AVERAGE));
        movie.setVoteCount(movieObject.getInt(VOTE_COUNT));
        movie.setReleaseDate(movieObject.getString(RELEASE_DATE));
        movie.setPosterAddress(movieObject.getString(POSTER_ADDRESS));
        movie.setBackdropAddress(movieObject.getString(BACKDROP_ADDRESS));
        movie.setTmdb_id(movieObject.getLong(TMDB_ID));

        return movie;
    }

    //method gets genres from JSON received from the call to download multiple movies by genre or popularity
    private static String getGenresFromJson(JSONObject movieObject) throws JSONException{
        final String GENRE_ID = "genre_ids";
        String genreStr = "";
        JSONArray genreArray = movieObject.getJSONArray(GENRE_ID);
        for(int i = 0; i < genreArray.length(); i++){
            int id = genreArray.getInt(i);

            for (Map.Entry<String, String> entry : searchCategories.entrySet()) {
                if ((Integer.toString(id)).equals(entry.getValue())) {
                    genreStr = genreStr + entry.getKey();
                    if(i < genreArray.length() - 1) {
                        genreStr = genreStr + ", ";
                    }
                }
            }
        }
        return genreStr;
    }

    //method gets genres from JSON received from the call to download particular movies by its id
    private static String getGenresByMovieID(JSONObject movieObject) throws JSONException{
        final String GENRES = "genres";
        final String NAME = "name";
        String genreStr = "";
        JSONArray genreArray = movieObject.getJSONArray(GENRES);
        for(int i = 0; i < genreArray.length(); i++){
            JSONObject genreObject = genreArray.getJSONObject(i);
            genreStr = genreStr + genreObject.getString(NAME);
                if(i < genreArray.length() - 1) {
                    genreStr = genreStr + ", ";
                }
        }
        return genreStr;
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
        }
        return reviewList;
    }
}
