package com.example.android.moviesapp.fragments;

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
import com.example.android.moviesapp.adapters.CursorMovieAdapter;
import com.example.android.moviesapp.adapters.MovieAdapter;
import com.example.android.moviesapp.database.MovieContract;
import com.example.android.moviesapp.models.Movie;
import com.example.android.moviesapp.sync.MovieSyncAdapter;
import com.example.android.moviesapp.utilities.FragmentCallback;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.android.moviesapp.activities.MainActivity.DETAILFRAGMENT_TAG;

/** This fragment displays favorite movies saved in the device's database*/

public class FavoriteGridFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainGridFragment.class.getSimpleName();

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private View rootView;
    private Loader loader;
    private CursorMovieAdapter cursorAdapter;
    private Cursor cursorMovieData;
    private Unbinder unbinder;

    @BindView(R.id.empty_view_image) ImageView emptyImage;
    @BindView(R.id.empty_view_message) TextView emptyText;

    @BindInt(R.integer.grid_columns) int GRID_COLUMNS_NUM;
    private final int PRIMARY_LOADER_ID = 0;

    public interface SwipeMovieCallback {
        public void onSwipeMovie(long tmdb_id);
    }

    public FavoriteGridFragment() {
    }

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
        }).attachToRecyclerView(recyclerView);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(PRIMARY_LOADER_ID, null, this);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.FavoriteMovieEntry.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor result) {
        if (result != null) {
            setEmptyGridViewVisible(false);
            cursorMovieData = result;
            cursorAdapter = new CursorMovieAdapter(cursorMovieData, new MovieAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Movie movie){
                    ((FragmentCallback) getActivity()).onItemSelected(movie);
                }
            });
            recyclerView.setAdapter(cursorAdapter);
        } if (result == null || result.getCount() == 0){
            setEmptyGridViewVisible(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (cursorMovieData != null) {
            cursorAdapter.swapCursor(null);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh){
            updateMovieList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMovieList(){
        MovieSyncAdapter.syncImmediately(getContext());
    }

    private void setEmptyGridViewVisible(boolean visible){
        LinearLayout emptyFavoritesLayout = (LinearLayout) rootView.findViewById(R.id.empty_grid_view_layout);
        if(visible) {
            emptyFavoritesLayout.setVisibility(View.VISIBLE);
            emptyImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_empty_movie_grid));
            emptyText.setText(getText(R.string.empty_favorite_movie_gridview));
        } else{
            emptyFavoritesLayout.setVisibility(View.GONE);
        }
    }
}
