package com.example.android.moviesapp.utilities;

import com.example.android.moviesapp.Movie;
import com.example.android.moviesapp.review.Review;
import com.example.android.moviesapp.trailer.YouTubeTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class JsonParser {

    private static final String LOG_TAG = JsonParser.class.getSimpleName() + "LOG";

    public static ArrayList<Long> getIDsFromJson(String movieJsonStr) throws JSONException {
        if (movieJsonStr == null) return null;

        final String TMDB_ID = "id";
        final String RESULTS = "results";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        ArrayList<Long> idList = new ArrayList<>();

        for (int i = 0; i < movieArray.length(); i++) {
            Long idStr;
            JSONObject movieObject = movieArray.getJSONObject(i);
            idStr = movieObject.getLong(TMDB_ID);
            idList.add(i, idStr);
        }
        return idList;
    }

    public static Movie getMovieDataFromJson(String movieJsonStr) throws JSONException {

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
        final String COUNTRIES = "production_countries";
        final String GENRES = "genres";
        final String PRODUCTION_COMPANIES = "production_companies";
        final String TMDB_ID = "id";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        Movie movie = new Movie();

        movie.setTitle(movieJson.getString(TITLE));
        movie.setOverview(movieJson.getString(OVERVIEW));
        movie.setRating(movieJson.getDouble(VOTE_AVERAGE));
        movie.setVoteCount(movieJson.getInt(VOTE_COUNT));
        movie.setReleaseDate(movieJson.getString(RELEASE_DATE));
        movie.setDuration(movieJson.getInt(DURATION));
        movie.setPosterAddress(movieJson.getString(POSTER_ADDRESS));
        movie.setBackdropAddress(movieJson.getString(BACKDROP_ADDRESS));
        movie.setMdb_id(movieJson.getLong(TMDB_ID));

        movie.setCountry(getAdditionalInfoFromJson(movieJson, COUNTRIES));
        movie.setGenre(getAdditionalInfoFromJson(movieJson, GENRES));
        movie.setProductionCompanies(getAdditionalInfoFromJson(movieJson, PRODUCTION_COMPANIES));

        return movie;
    }

    private static String getAdditionalInfoFromJson(JSONObject movieJson, final String TYPE) throws JSONException{
        final String NAME = "name";
        String infoStr = "";
        JSONArray infoArray = movieJson.getJSONArray(TYPE);
        for(int i = 0; i < infoArray.length(); i++){
            JSONObject infoObject = infoArray.getJSONObject(i);
            infoStr = infoStr + infoObject.getString(NAME);
            if(i < infoArray.length() - 1) {
                infoStr = infoStr + ", ";
            }
        }
        return infoStr;
    }

    /*public static String[] getCountryAndGenreDataFromJson(String movieJsonStr) {
        if (movieJsonStr == null) return null;

        final String COUNTRIES = "country";
        final String GENRES = "genre_ids";
        final String PRODUCTION_COMPANIES = "production_companies";

        try {
            JSONObject movieJsonObject = new JSONObject(movieJsonStr);

            String[] additionalMovieInfoArray = new String[3];

            additionalMovieInfoArray[0] = getAdditionalInfoFromJson(movieJsonObject, COUNTRIES);
            additionalMovieInfoArray[1] = getAdditionalInfoFromJson(movieJsonObject, GENRES);
            additionalMovieInfoArray[2] = getAdditionalInfoFromJson(movieJsonObject, PRODUCTION_COMPANIES);

            return additionalMovieInfoArray;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    } */

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
