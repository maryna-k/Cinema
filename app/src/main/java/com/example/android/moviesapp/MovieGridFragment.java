package com.example.android.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MovieGridFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>{

    private final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    public static String moviesToSearch;
    private int GRID_COLUMNS_NUM;
    private View rootView;
    private MovieLoader loader;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private final int PRIMARY_LOADER_ID = 0;
    private final int SECONDARY_LOADER_ID = 1;

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

    public MovieGridFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(mScrollListener);

        //recreate fragment state on rotation
        if (savedInstanceState == null) {
            //use SharedPreferences to get the default value of movie search
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
            moviesToSearch = settings.getString(getString(R.string.pref_search_key),
                                                getString(R.string.pref_search_default));
            getActivity().setTitle(getString(R.string.app_name) + ": " + moviesToSearch);
            Log.v(LOG_TAG, "SharedPreferences: " + moviesToSearch);
        } else {
            moviesToSearch = savedInstanceState.getString("searchCategory");
            getActivity().setTitle(getString(R.string.app_name) + ": " + moviesToSearch);
        }
        /*DownloadMovieDataTask downloadMovies = new DownloadMovieDataTask();
        if (checkNetworkConnection()) downloadMovies.execute(drawerItemTitle);*/

        getLoaderManager().initLoader(PRIMARY_LOADER_ID, null, this);
        return rootView;
    }

    private void loadNextDataFromApi(){
        getLoaderManager().initLoader(SECONDARY_LOADER_ID, null, this);
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
                            mAdapter = new MovieAdapter(result, new MovieAdapter.OnItemClickListener(){
                                @Override
                                public void onItemClick(Movie movie){
                                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                                            .putExtra("movie", movie);
                                    startActivity(intent);
                                }
                            });
                            mRecyclerView.setAdapter(mAdapter);
                        } else
                            mAdapter.addData(result);
                    break;
                case SECONDARY_LOADER_ID:
                    mAdapter.addData(result);
                    getLoaderManager().destroyLoader(SECONDARY_LOADER_ID);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        mScrollListener.resetState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("searchCategory", moviesToSearch);
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) return true;
        else {
            // display an error as a toast message
            Toast.makeText(getContext(), "No Internet connection", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public static String getMoviesToSearch() {
        return moviesToSearch;
    }

    public void updateMovieList(){
        mAdapter.clearData();
        getLoaderManager().restartLoader(PRIMARY_LOADER_ID, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Activate the navigation drawer toggle
        /*if (drawerToggle.onOptionsItemSelected(item)) return true;
        else*/ if(item.getItemId() == R.id.action_refresh){
            updateMovieList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
