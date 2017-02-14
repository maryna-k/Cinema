package com.example.android.moviesapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviesapp.utilities.FragmentCallback;

import java.util.ArrayList;

public class MovieGridFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>{

    private final String LOG_TAG = MovieGridFragment.class.getSimpleName() + " LOG";

    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private String moviesToSearch;
    private int GRID_COLUMNS_NUM;
    private View rootView;
    private MovieLoader loader;
    private EndlessRecyclerViewScrollListener mScrollListener;

    private final int PRIMARY_LOADER_ID = 0;
    private final int SECONDARY_LOADER_ID = 1;

    public MovieGridFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.v(LOG_TAG, "OnCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        GRID_COLUMNS_NUM = getResources().getInteger(R.integer.grid_columns);
        mLayoutManager = new GridLayoutManager(getContext(), GRID_COLUMNS_NUM);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore() {
                loadNextDataFromApi();
            }
        };
        mRecyclerView.addOnScrollListener(mScrollListener);
        moviesToSearch = MainActivity.getMoviesToSearch();
        getLoaderManager().initLoader(PRIMARY_LOADER_ID, null, this);
        Log.v(LOG_TAG, "OnCreateView");
        return rootView;
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args){
        Log.v(LOG_TAG, "Loader: OnCreateLoader, ID " + id);
        if (id == PRIMARY_LOADER_ID ) {
            mScrollListener.resetState();
        }
        loader = new MovieLoader(getContext());
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> result) {
        Log.v(LOG_TAG, "Loader: OnLoadFinished, ID " + loader.getId());
        if (result != null){
            switch(loader.getId()){
                case PRIMARY_LOADER_ID:
                        if (mAdapter == null){
                            setEmptyGridViewVisible(false);
                            createAdapter(result);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    break;
                case SECONDARY_LOADER_ID:
                    mAdapter.addData(result);
                    getLoaderManager().destroyLoader(SECONDARY_LOADER_ID);
            }
        }
        if (result == null || result.size() == 0){
            setEmptyGridViewVisible(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        Log.v(LOG_TAG, "Loader: OnLoaderReset");
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        Log.v(LOG_TAG, "onSaveInstanceState");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment, menu);
        Log.v(LOG_TAG, "OnCreateOptionsMenu");
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

    private void loadNextDataFromApi(){
        getLoaderManager().initLoader(SECONDARY_LOADER_ID, null, this);
    }

    private void updateMovieList(){
        mAdapter = null;
        getLoaderManager().restartLoader(PRIMARY_LOADER_ID, null, this);
    }

    private void createAdapter(ArrayList<Movie> data){
        mAdapter = new MovieAdapter(data, new MovieAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(Movie movie){
                /* false here just indicates that DetailFragment has to check the database,
                to see if the movie is in favorite */
                ((FragmentCallback) getActivity()).onItemSelected(movie, false);
            }
        });
    }

    private void setEmptyGridViewVisible(boolean visible){
        LinearLayout emptyFavoritesLayout = (LinearLayout) rootView.findViewById(R.id.empty_grid_view_layout);
        if(visible) {
            emptyFavoritesLayout.setVisibility(View.VISIBLE);
            ImageView emptyImage = (ImageView) rootView.findViewById(R.id.empty_view_image);
            TextView emptyText = (TextView) rootView.findViewById(R.id.empty_view_message);
            emptyImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_empty_movie_grid));
            emptyText.setText(getText(R.string.empty_movie_gridview));
        } else{
            emptyFavoritesLayout.setVisibility(View.GONE);
        }
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) return true;
        else {
            Toast.makeText(getContext(), "No Internet connection", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
