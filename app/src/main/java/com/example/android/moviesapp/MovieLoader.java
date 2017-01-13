package com.example.android.moviesapp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.android.moviesapp.MainActivity.getMoviesToSearch;

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private ArrayList<Movie> movieList;

    private final String LOG_TAG = MovieLoader.class.getSimpleName();

    public MovieLoader(Context context){
        super(context);
    }

    @Override
    public void onStartLoading(){
        forceLoad();
    }

    @Override
    public ArrayList<Movie> loadInBackground(){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;
        final String urlString = buildMovieUrl(getMoviesToSearch());
        Log.v(LOG_TAG, "Url: " + urlString);

        try {
            URL url = new URL(urlString);

            // Create the request to TMDb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            /*if(urlConnection.getResponseCode() == 200){
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }*/
            
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
            movieJsonStr = buffer.toString();
            Log.v(LOG_TAG, "TMDb JSON string: " + movieJsonStr);

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

        try {
            return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr) throws JSONException {

        final String RESULTS = "results";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String RATING = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String IMAGE_ADDRESS = "poster_path";
        final String TMDB_ID = "id";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        if (movieList == null) movieList = new ArrayList<Movie>();
        else movieList.clear();

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

    private String buildMovieUrlHelper(String criteria1, String criteria2, String page) {

        final String BASE_URL = "http://api.themoviedb.org/3/";
        final String API_KEY = "?api_key=" + BuildConfig.TMDb_API_KEY;

        return new StringBuilder()
                .append(BASE_URL)
                .append(criteria1)
                .append(API_KEY)
                .append(criteria2)
                .append(page).toString();
    }

    private String buildMovieUrl(String category){
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
