package com.example.android.moviesapp.utilities;


import java.util.HashMap;

public class Keys {

    public static final String TMDb_API_KEY = "PUT YOUR TMDB API KEY HERE";

    public static final String YOUTUBE_API_KEY = "PUT YOUR YOUTUBE API KEY HERE";

    public final static String BASE_URL = "http://api.themoviedb.org/3/";

    public static final String BACKDROP_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w780/";

    public static final String SMALL_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w500/";

    /*key value pairs of genres and their ids as well as http requests for popular and top rated movies*/
    public static final HashMap<String, String> searchCategories = new HashMap<>();

    static {
        searchCategories.put("Popular", "popular");
        searchCategories.put("Top Rated", "top_rated");
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