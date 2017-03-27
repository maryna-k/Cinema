package com.example.android.moviesapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.moviesapp.database.MovieContract;
import com.example.android.moviesapp.utilities.FragmentCallback;

import static android.content.ContentValues.TAG;
import static com.example.android.moviesapp.MainActivity.DETAILFRAGMENT_TAG;


public class FavoriteGridFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieGridFragment.class.getSimpleName() + " LOG";

    private CursorMovieAdapter mCursorAdapter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private int GRID_COLUMNS_NUM;
    private View rootView;
    private Loader loader;
    private final int PRIMARY_LOADER_ID = 0;
    private Cursor mCursorMovieData;

    public interface SwipeMovieCallback {
        public void onSwipeMovie(long tmdb_id);
    }

    public FavoriteGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.v(LOG_TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        GRID_COLUMNS_NUM = getResources().getInteger(R.integer.grid_columns);
        mLayoutManager = new GridLayoutManager(getContext(), GRID_COLUMNS_NUM);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (savedInstanceState == null) {
            getLoaderManager().initLoader(PRIMARY_LOADER_ID, null, this);
        }

        //Touch helper recognizes when a user swipes to delete an item.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback
                (0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long tmdb_id = (long) viewHolder.itemView.getTag();
                /*Uri uri = MovieContract.FavoriteMovieEntry.CONTENT_URI.buildUpon()
                        .appendPath(MovieContract.FavoriteMovieEntry.PATH_FAVORITE_MOVIES_TMDB_ID)
                        .appendPath(Long.toString(tmdb_id))
                        .build();*/
                Uri uri = MovieContract.FavoriteMovieEntry.CONTENT_URI.buildUpon()
                        .appendPath(Long.toString(tmdb_id))
                        .build();

                getActivity().getContentResolver().delete(uri, null, null);
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
                if(fragment != null) {
                    ((SwipeMovieCallback) fragment).onSwipeMovie(tmdb_id);
                }
                getLoaderManager().restartLoader(PRIMARY_LOADER_ID, null, FavoriteGridFragment.this);
            }
        }).attachToRecyclerView(mRecyclerView);
        Log.v(LOG_TAG, "onCreateView");
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(LOG_TAG, "onSaveInstanceState");
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(PRIMARY_LOADER_ID, null, this);
        Log.v(TAG, "onResume(): restartLoader");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "Loader: onCreateLoader");
        return new CursorLoader(getActivity(),
                MovieContract.FavoriteMovieEntry.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor result) {
        Log.v(LOG_TAG, "Loader: onLoadFinished");
        if (result != null) {
            setEmptyGridViewVisible(false);
            mCursorMovieData = result;
            mCursorAdapter = new CursorMovieAdapter(mCursorMovieData, new MovieAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Movie movie){
                    ((FragmentCallback) getActivity()).onItemSelected(movie);
                }
            });
            mRecyclerView.setAdapter(mCursorAdapter);
        } if (result == null || result.getCount() == 0){
            setEmptyGridViewVisible(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "Loader: onLoaderReset");
        if (mCursorMovieData != null) {
            mCursorAdapter.swapCursor(null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setEmptyGridViewVisible(boolean visible){
        LinearLayout emptyFavoritesLayout = (LinearLayout) rootView.findViewById(R.id.empty_grid_view_layout);
        if(visible) {
            emptyFavoritesLayout.setVisibility(View.VISIBLE);
            ImageView emptyImage = (ImageView) rootView.findViewById(R.id.empty_view_image);
            TextView emptyText = (TextView) rootView.findViewById(R.id.empty_view_message);
            emptyImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_empty_movie_grid));
            emptyText.setText(getText(R.string.empty_favorite_movie_gridview));
        } else{
            emptyFavoritesLayout.setVisibility(View.GONE);
        }
    }
}
