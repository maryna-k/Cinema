package com.example.android.moviesapp.mvp.movieGrid;

import android.content.Context;

import com.example.android.moviesapp.models.Movie;
import com.example.android.moviesapp.models.MovieResponse;
import com.example.android.moviesapp.mvp.data.ConnectionStateWatcher;
import com.example.android.moviesapp.mvp.data.DataRepository;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MovieGridPresenter implements MovieGridContract.Presenter{

    private MovieGridContract.View view;
    private DataRepository repository;
    private Context context;
    private ArrayList<Movie> list;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ConnectionStateWatcher watcher;

    public MovieGridPresenter(MovieGridContract.View view, DataRepository repository, Context context){
        this.view = view;
        this.repository = repository;
        this.context = context;
    }


    @Override
    public void loadMovies(String movieType) {
        compositeDisposable.add(repository.loadMovies(movieType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<MovieResponse>() {
                    @Override
                    public void onSuccess(@NonNull MovieResponse response) {
                        list = response.getResults();
                        if(list != null && !list.isEmpty()){
                            view.setEmptyGridViewVisible(false);
                            view.showMovies(list);
                        } else {
                            view.setEmptyGridViewVisible(true);
                        }
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                }));
    }

    public void unsubscribe() {
        compositeDisposable.clear();
    }

    public void createConnectionWatcher(){
        watcher = new ConnectionStateWatcher(context);
        watcher.startWatching(new ConnectionStateWatcher.ConnectionCallback() {
            @Override
            public void connected() {
                view.loadNextDataFromApi();
                view.updateMovieList();
            }

            @Override
            public void disconnected() {

            }
        });
    }

    public void stopConnectionWatcher(){
        watcher.stopWatching();
    }
}
