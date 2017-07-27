package com.example.android.moviesapp.mvp.data;


import com.example.android.moviesapp.models.Movie;
import com.example.android.moviesapp.models.MovieResponse;
import com.example.android.moviesapp.mvp.rest.TMDBService;
import com.example.android.moviesapp.utilities.EndlessRecyclerViewScrollListener;
import com.example.android.moviesapp.utilities.Keys;

import java.util.ArrayList;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RemoteDataSource implements DataRepository {

    private static RemoteDataSource INSTANCE;
    private Retrofit retrofit = null;
    private TMDBService service;
    private Single<ArrayList<Movie>> list;

    private final String LOG_TAG = RemoteDataSource.class.getSimpleName();

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }

    private RemoteDataSource() {
        if (retrofit==null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Keys.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        service = retrofit.create(TMDBService.class);
    }

    @Override
    public Single<MovieResponse> loadMovies(String movieType) {
        Single<MovieResponse> call;
        if(movieType.equals("Popular") || movieType.equals("Top Rated")) {
            call = service.getMoviesByPopularity(Keys.searchCategories.get(movieType), Keys.TMDb_API_KEY, EndlessRecyclerViewScrollListener.getPageIndex());
        } else {
            call = service.getMoviesByGenre(Keys.TMDb_API_KEY, Keys.searchCategories.get(movieType), EndlessRecyclerViewScrollListener.getPageIndex());
        }
        return call;
    }
}
