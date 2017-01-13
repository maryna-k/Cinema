package com.example.android.moviesapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
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

import com.example.android.moviesapp.database.MovieContract;

import static android.content.ContentValues.TAG;


public class FavoriteGridFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    private CursorMovieAdapter mCursorAdapter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private int GRID_COLUMNS_NUM;
    private View rootView;
    private Loader loader;
    private final int PRIMARY_LOADER_ID = 0;
    private Cursor mCursorMovieData;


    public FavoriteGridFragment() {}

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

        //recreate fragment state on rotation
        if (savedInstanceState == null) {
            getLoaderManager().initLoader(PRIMARY_LOADER_ID, null, this);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // re-queries for all tasks
        getLoaderManager().restartLoader(PRIMARY_LOADER_ID, null, this);
        Log.v(TAG, "onResume(): restartLoader");
    }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            //CursorLoader is a loader that queries the ContentResolver and returns a Cursor.
            return new CursorLoader(getActivity(),
                    MovieContract.FavoriteMovieEntry.CONTENT_URI,
                    null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor result) {
            if (result != null && loader.getId() == PRIMARY_LOADER_ID) {
                mCursorMovieData = result;
                mCursorAdapter = new CursorMovieAdapter(mCursorMovieData, new MovieAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Movie movie) {
                        Intent intent = new Intent(getActivity(), DetailActivity.class)
                                .putExtra("movie", movie)
                                .putExtra("favorite", true);
                        startActivity(intent);
                    }
                });
                mRecyclerView.setAdapter(mCursorAdapter);
            }
        }


        //Called when a previously created loader is being reset,making its data unavailable.
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (mCursorMovieData != null) {
                mCursorAdapter.swapCursor(null);
            }
        }

        @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }
}
