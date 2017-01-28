package com.example.android.moviesapp.utilities;

import android.util.Log;

import com.example.android.moviesapp.BuildConfig;
import com.example.android.moviesapp.DetailFragment;
import com.example.android.moviesapp.EndlessRecyclerViewScrollListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static com.example.android.moviesapp.MainActivity.getMoviesToSearch;
import static com.example.android.moviesapp.database.MovieProvider.LOG_TAG;

public class MDBConnection {

    public final static int LOAD_MOVIES = 1;
    public final static int LOAD_TRAILER_INFO = 2;
    public final static int LOAD_REVIEWS = 3;

    private final static String BASE_URL = "http://api.themoviedb.org/3/";
    private final static String API_KEY = "?api_key=" + BuildConfig.TMDb_API_KEY;


    public static String getJsonResponse(int requestType){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonString = null;
        String urlString;

        switch (requestType){
            case LOAD_MOVIES:
                urlString = buildMovieUrl(getMoviesToSearch());
                break;
            case LOAD_TRAILER_INFO:
                urlString = buildTrailerInfoUrl(DetailFragment.getmTMDB_ID());
                break;
            case LOAD_REVIEWS:
                urlString = buildMovieUrl(getMoviesToSearch());
                break;
            default: return null;
        }

        Log.v(LOG_TAG, "Url: " + urlString);

        try {
            URL url = new URL(urlString);

            // Create the request to TMDb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            jsonString = buffer.toString();
            Log.v(LOG_TAG, "TMDb JSON string: " + jsonString);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return jsonString;
    }

    private static String buildMovieUrl(String category){
        String url;
        if (category.equals("Popular") || category.equals("Top Rated")) {
            url = buildMovieUrlHelper(searchCategories.get(category), "",
                    "&page=" + EndlessRecyclerViewScrollListener.setPageIndex());
        } else {
            url = buildMovieUrlHelper("discover/movie", "&with_genres=" + searchCategories.get(category),
                    "&page=" + EndlessRecyclerViewScrollListener.setPageIndex());
        }
        return url;
    }

    private static String buildMovieUrlHelper(String criteria1, String criteria2, String page) {

        return new StringBuilder()
                .append(BASE_URL)
                .append(criteria1)
                .append(API_KEY)
                .append(criteria2)
                .append(page).toString();
    }

    private static String buildTrailerInfoUrl(long movieId){

        return new StringBuilder()
                .append(BASE_URL)
                .append("movie/")
                .append(movieId)
                .append("/videos")
                .append(API_KEY)
                .toString();
    }


    /*since java has no map literals and searchCategories is a class variable, initialization
      should be done in a static initializer */
    public static final HashMap<String, String> searchCategories = new HashMap<>();
    static {
        searchCategories.put("Popular", "movie/popular");
        searchCategories.put("Top Rated", "movie/top_rated");
        searchCategories.put("Action", "28");
        searchCategories.put("Adventure", "12");
        searchCategories.put("Animation", "16");
        searchCategories.put("Comedy", "35");
        searchCategories.put("Crime", "80");
        searchCategories.put("Documentary", "99");
        searchCategories.put("Drama", "18");
        searchCategories.put("Family", "10751");
        searchCategories.put("Fantasy", "14");
        searchCategories.put("Foreign", "10769");
        searchCategories.put("Horror", "27");
        searchCategories.put("Music", "10402");
        searchCategories.put("Mystery", "9648");
        searchCategories.put("Romance", "10749");
        searchCategories.put("Science Fiction", "878");
        searchCategories.put("TV Movie", "10770");
        searchCategories.put("Thriller", "53");
        searchCategories.put("War", "10752");
        searchCategories.put("Western", "37");
    }
}
