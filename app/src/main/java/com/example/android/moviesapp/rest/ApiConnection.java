package com.example.android.moviesapp.rest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.android.moviesapp.utilities.EndlessRecyclerViewScrollListener;
import com.example.android.moviesapp.utilities.Keys;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.android.moviesapp.activities.MainActivity.getMoviesToSearch;

/** Builds url string based on the user request and makes server calls*/

public class ApiConnection {

    private final static String LOG_TAG = ApiConnection.class.getSimpleName() + "LOG";

    public final static int LOAD_MOVIES = 1;
    public final static int LOAD_TRAILER_INFO = 2;
    public final static int LOAD_REVIEWS = 3;
    public final static int LOAD_MOVIE_BY_ID = 4;

    private final static String BASE_URL = "http://api.themoviedb.org/3/";
    private final static String API_KEY = "?api_key=" + Keys.TMDb_API_KEY;


    public static String getApiResponse(int requestType, long tmdb_id) {

        String urlString;
        switch (requestType) {
            case LOAD_MOVIES:
                urlString = buildUrlBySearchCriteria(getMoviesToSearch());
                break;
            case LOAD_TRAILER_INFO:
                urlString = buildUrlByMovieID(tmdb_id, LOAD_TRAILER_INFO);
                break;
            case LOAD_REVIEWS:
                urlString = buildUrlByMovieID(tmdb_id, LOAD_REVIEWS);
                break;
            case LOAD_MOVIE_BY_ID:
                urlString = buildUrlByMovieID(tmdb_id, LOAD_MOVIE_BY_ID);
                break;
            default:
                return null;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String buildUrlBySearchCriteria(String category) {
        String url;
        if (category.equals("Popular") || category.equals("Top Rated")) {
            url = buildMovieUrlHelper(searchCategories.get(category), "",
                    "&page=" + EndlessRecyclerViewScrollListener.getPageIndex());
        } else {
            url = buildMovieUrlHelper("discover/movie", "&with_genres=" + searchCategories.get(category),
                    "&page=" + EndlessRecyclerViewScrollListener.getPageIndex());
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

    private static String buildUrlByMovieID(long movieId, int infoType) {
        String type = "";
        switch (infoType) {
            case LOAD_MOVIE_BY_ID:
                type = null;
                break;
            case LOAD_TRAILER_INFO:
                type = "/videos";
                break;
            case LOAD_REVIEWS:
                type = "/reviews";
                break;
        }
        return new StringBuilder()
                .append(BASE_URL)
                .append("movie/")
                .append(movieId)
                .append(type)
                .append(API_KEY)
                .toString();
    }

    public static boolean checkNetworkConnection(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    /*key value pairs of genres and their ids as well as http requests for popular and top rated movies*/
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