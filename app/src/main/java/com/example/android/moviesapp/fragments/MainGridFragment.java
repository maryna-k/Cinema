package com.example.android.moviesapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.moviesapp.R;
import com.example.android.moviesapp.adapters.MovieAdapter;
import com.example.android.moviesapp.loaders.MovieLoader;
import com.example.android.moviesapp.models.Movie;
import com.example.android.moviesapp.rest.ApiConnection;
import com.example.android.moviesapp.utilities.EndlessRecyclerViewScrollListener;
import com.example.android.moviesapp.utilities.FragmentCallback;

import java.util.ArrayList;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/** This fragment displays movies received from the server call*/

public class MainGridFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>{

    private final String LOG_TAG = MainGridFragment.class.getSimpleName();

    private MovieAdapter adapter;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private String moviesToSearch;
    private MovieLoader loader;
    private EndlessRecyclerViewScrollListener scrollListener;

    private View rootView;
    @BindView(R.id.empty_grid_view_layout) LinearLayout emptyMovieGridLayout;
    @BindView(R.id.empty_view_image) ImageView emptyImage;
    @BindView(R.id.empty_view_message) TextView emptyText;

    private IntentFilter internetFilter;
    private BroadcastReceiver broadcastReceiver;
    private static boolean loadMoreMovies;

    @BindInt(R.integer.grid_columns) int GRID_COLUMNS_NUM;

    private final int PRIMARY_LOADER_ID = 0;
    private final int SECONDARY_LOADER_ID = 1;

    private Unbinder unbinder;

    public MainGridFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        layoutManager = new GridLayoutManager(getContext(), GRID_COLUMNS_NUM);
        recyclerView.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager, getContext()) {
            @Override
            public void onLoadMore() {
                loadNextDataFromApi();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        if(ApiConnection.checkNetworkConnection(getContext()) || savedInstanceState != null) {
            getLoaderManager().initLoader(PRIMARY_LOADER_ID, null, this);
        } else {
            setEmptyGridViewVisible(true);
        }

        installConnectionListener();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Activate the navigation drawer toggle
        if(item.getItemId() == R.id.action_refresh){
            updateMovieList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, internetFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
    }
    
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args){
        if (id == PRIMARY_LOADER_ID ) {
            scrollListener.resetState();
        }
        loader = new MovieLoader(getContext());
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> result) {
        if (result != null){
            switch(loader.getId()){
                case PRIMARY_LOADER_ID:
                        if (adapter == null){
                            setEmptyGridViewVisible(false);
                            createAdapter(result);
                            recyclerView.setAdapter(adapter);
                        }
                    recyclerView.setAdapter(adapter);
                    break;
                case SECONDARY_LOADER_ID:
                    adapter.addData(result);
                    getLoaderManager().destroyLoader(SECONDARY_LOADER_ID);
            }
        } else if (result == null && loader.getId() == PRIMARY_LOADER_ID){
            setEmptyGridViewVisible(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
    }

    //broadcast receiver that loads movies as soon as internet connection is established
    private void installConnectionListener() {
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();
                    NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");
                    NetworkInfo.State state = info.getState();
                    if (state == NetworkInfo.State.CONNECTED && emptyMovieGridLayout != null
                            && emptyMovieGridLayout.getVisibility() == View.VISIBLE) {
                        updateMovieList();
                    }
                    if (state == NetworkInfo.State.CONNECTED && loadMoreMovies){
                        loadMoreMovies = false;
                        scrollListener.incrementCurrentPage();
                        loadNextDataFromApi();
                    }
                }
            };
            internetFilter = new IntentFilter();
            internetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        }
    }

    private void loadNextDataFromApi(){
        getLoaderManager().initLoader(SECONDARY_LOADER_ID, null, this);
    }

    private void updateMovieList(){
        adapter = null;
        getLoaderManager().restartLoader(PRIMARY_LOADER_ID, null, this);
    }

    private void createAdapter(ArrayList<Movie> data){
        adapter = new MovieAdapter(GRID_COLUMNS_NUM, data, new MovieAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(Movie movie){
                /* false here just indicates that DetailFragment has to check the database,
                to see if the movie is in favorite */
                ((FragmentCallback) getActivity()).onItemSelected(movie);
            }
        });
    }

    private void setEmptyGridViewVisible(boolean visible){
        if(emptyMovieGridLayout != null) {
            if (visible) {
                emptyMovieGridLayout.setVisibility(View.VISIBLE);
                emptyImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_empty_movie_grid));
                emptyText.setText(getText(R.string.empty_movie_gridview));
            } else {
                emptyMovieGridLayout.setVisibility(View.GONE);
            }
        }
    }

    public static void setLoadMoreMovies(boolean load){
        loadMoreMovies = load;
    }
}
