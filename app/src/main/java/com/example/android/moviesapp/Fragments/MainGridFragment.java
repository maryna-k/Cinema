package com.example.android.moviesapp.Fragments;

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

import com.example.android.moviesapp.Adapters.MovieAdapter;
import com.example.android.moviesapp.Loaders.MovieLoader;
import com.example.android.moviesapp.Objects.Movie;
import com.example.android.moviesapp.R;
import com.example.android.moviesapp.utilities.EndlessRecyclerViewScrollListener;
import com.example.android.moviesapp.utilities.FragmentCallback;
import com.example.android.moviesapp.utilities.MDBConnection;

import java.util.ArrayList;

public class MainGridFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>{

    private final String LOG_TAG = MainGridFragment.class.getSimpleName();

    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private String moviesToSearch;
    private int GRID_COLUMNS_NUM;
    private View rootView;
    private MovieLoader loader;
    private EndlessRecyclerViewScrollListener mScrollListener;

    private LinearLayout emptyMovieGridLayout;

    private IntentFilter mInternetFilter;
    private BroadcastReceiver mBroadcastReceiver;
    private static boolean loadMoreMovies;

    private int progressBarExtraItems;

    private final int PRIMARY_LOADER_ID = 0;
    private final int SECONDARY_LOADER_ID = 1;

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

        progressBarExtraItems = getContext().getResources().getInteger(R.integer.grid_columns);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        GRID_COLUMNS_NUM = getResources().getInteger(R.integer.grid_columns);
        mLayoutManager = new GridLayoutManager(getContext(), GRID_COLUMNS_NUM);
        mRecyclerView.setLayoutManager(mLayoutManager);

        emptyMovieGridLayout = (LinearLayout) rootView.findViewById(R.id.empty_grid_view_layout);

        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager, getContext()) {
            @Override
            public void onLoadMore() {
                loadNextDataFromApi();
            }
        };
        mRecyclerView.addOnScrollListener(mScrollListener);

        if(MDBConnection.checkNetworkConnection(getContext()) || savedInstanceState != null) {
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
        getActivity().registerReceiver(mBroadcastReceiver, mInternetFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }
    
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args){
        if (id == PRIMARY_LOADER_ID ) {
            mScrollListener.resetState();
        }
        loader = new MovieLoader(getContext());
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> result) {
        if (result != null){
            switch(loader.getId()){
                case PRIMARY_LOADER_ID:
                        if (mAdapter == null){
                            setEmptyGridViewVisible(false);
                            createAdapter(result);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    mRecyclerView.setAdapter(mAdapter);
                    break;
                case SECONDARY_LOADER_ID:
                    mAdapter.addData(result);
                    getLoaderManager().destroyLoader(SECONDARY_LOADER_ID);
            }
        } else if (result == null && loader.getId() == PRIMARY_LOADER_ID){
            setEmptyGridViewVisible(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
    }

    private void installConnectionListener() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();
                    NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");
                    NetworkInfo.State state = info.getState();
                    if (state == NetworkInfo.State.CONNECTED && emptyMovieGridLayout.getVisibility() == View.VISIBLE) {
                        updateMovieList();
                    }
                    if (state == NetworkInfo.State.CONNECTED && loadMoreMovies){
                        loadMoreMovies = false;
                        mScrollListener.incrementCurrentPage();
                        loadNextDataFromApi();
                    }
                }
            };
            mInternetFilter = new IntentFilter();
            mInternetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        }
    }

    private void loadNextDataFromApi(){
        getLoaderManager().initLoader(SECONDARY_LOADER_ID, null, this);
    }

    private void updateMovieList(){
        mAdapter = null;
        getLoaderManager().restartLoader(PRIMARY_LOADER_ID, null, this);
    }

    private void createAdapter(ArrayList<Movie> data){
        mAdapter = new MovieAdapter(progressBarExtraItems, data, new MovieAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(Movie movie){
                /* false here just indicates that DetailFragment has to check the database,
                to see if the movie is in favorite */
                ((FragmentCallback) getActivity()).onItemSelected(movie);
            }
        });
    }

    private void setEmptyGridViewVisible(boolean visible){

        if(visible) {
            emptyMovieGridLayout.setVisibility(View.VISIBLE);
            ImageView emptyImage = (ImageView) rootView.findViewById(R.id.empty_view_image);
            TextView emptyText = (TextView) rootView.findViewById(R.id.empty_view_message);
            emptyImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_empty_movie_grid));
            emptyText.setText(getText(R.string.empty_movie_gridview));
        } else{
            emptyMovieGridLayout.setVisibility(View.GONE);
        }
    }

    public static void setLoadMoreMovies(boolean load){
        loadMoreMovies = load;
    }
}