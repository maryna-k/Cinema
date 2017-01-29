package com.example.android.moviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviesapp.database.MovieContract.FavoriteMovieEntry;
import com.example.android.moviesapp.trailer.TrailerAdapter;
import com.example.android.moviesapp.trailer.TrailerInfoLoader;
import com.example.android.moviesapp.trailer.YouTubeTrailer;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private TrailerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private Movie movie;
    private String mTitle;
    private String mOverview;
    private String mReleaseDate;
    private double mRating;
    private int mVoteCount;
    private String mImageAddress;
    private boolean favorite;
    private static long mTMDB_ID;
    private int db_id;
    private final int LOADER_ID = 1;
    private TrailerInfoLoader loader;
    private View rootView;
    private ProgressBar mProgressBar;
    private LinearLayout trailerView;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        //get Movie Object from the intent
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            movie = (Movie) intent.getSerializableExtra("movie");
            mTMDB_ID = movie.getMdb_id();

            /*intent.hasExtra("favorite") is true if this intent was sent from FavoriteGridFragment
            * and therefore this movie is in database. Otherwise, fragment should check if the tmdb_id of this
            * Movie object is already in database */
            if (intent.hasExtra("favorite")) {
                favorite = intent.getExtras().getBoolean("favorite");
            } else {
                favorite = movieIsInFavorite(mTMDB_ID);
            }
            mImageAddress = movie.getImageAddress();
            ImageView header = (ImageView) rootView.findViewById(R.id.header);
            String headerImageAddress = "http://image.tmdb.org/t/p/w780/" + mImageAddress;
            Picasso.with(getContext()).load(headerImageAddress).into(header);

            ImageView small_poster = (ImageView) rootView.findViewById(R.id.small_poster);
            String smallImageAddress = "http://image.tmdb.org/t/p/w500/" + mImageAddress;
            Picasso.with(getContext()).load(smallImageAddress).into(small_poster);

            mTitle = movie.getTitle();
            TextView title = (TextView) rootView.findViewById(R.id.title);
            title.setText(mTitle);

            mReleaseDate = movie.getReleaseDate();
            TextView release = (TextView) rootView.findViewById(R.id.release_date);
            release.setText(mReleaseDate);

            mRating = movie.getRating();
            TextView rating = (TextView) rootView.findViewById(R.id.rating);
            rating.setText(Double.toString(mRating) + "/10");

            mOverview = movie.getOverview();
            TextView overview = (TextView) rootView.findViewById(R.id.overview);
            overview.setText(mOverview);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.trailers_recycler_view);
            mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
            trailerView = (LinearLayout) rootView.findViewById(R.id.layout_trailers_title);

            getLoaderManager().initLoader(LOADER_ID, null, trailerResultLoaderListener);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail_fragment, menu);
    }

    //change the icon of the favorite button depending on if the Movie object is favorite or not
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem removeFromFavorite = menu.findItem(R.id.action_remove_from_favorite);
        MenuItem addToFavorite = menu.findItem(R.id.action_add_to_favorite);

        if (favorite) {
            addToFavorite.setVisible(false);
            removeFromFavorite.setVisible(true);
        } else {
            addToFavorite.setVisible(true);
            removeFromFavorite.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_to_favorite:
                boolean inserted = insertMovie();
                if (inserted) {
                    getActivity().invalidateOptionsMenu();
                }
                break;

            case R.id.action_remove_from_favorite:
                Uri uri = FavoriteMovieEntry.CONTENT_URI.buildUpon()
                        .appendPath(FavoriteMovieEntry.PATH_FAVORITE_MOVIES_TMDB_ID).appendPath(Long.toString(mTMDB_ID)).build();
                int removedNum = getContext().getContentResolver().delete(uri, null, null);
                if (removedNum > 0) {
                    favorite = false;
                    getActivity().invalidateOptionsMenu();
                    Toast.makeText(getContext(), "Movie was removed from Favorite", Toast.LENGTH_LONG)
                            .show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //insert Movie object to favorite_movies table
    public boolean insertMovie() {

        ContentValues values = new ContentValues();
        values.put(FavoriteMovieEntry.COLUMN_NAME_TITLE, mTitle);
        values.put(FavoriteMovieEntry.COLUMN_NAME_OVERVIEW, mOverview);
        values.put(FavoriteMovieEntry.COLUMN_NAME_RATING, mRating);
        values.put(FavoriteMovieEntry.COLUMN_NAME_RELEASE, mReleaseDate);
        values.put(FavoriteMovieEntry.COLUMN_NAME_IMAGE_ADDRESS, mImageAddress);
        values.put(FavoriteMovieEntry.COLUMN_NAME_MDB_ID, mTMDB_ID);

        Uri uri = getContext().getContentResolver().insert(FavoriteMovieEntry.CONTENT_URI, values);
        if (uri != null) {
            favorite = true;
            //set db_id from the uri, in case if user wants to remove item right away
            db_id = Integer.parseInt(uri.getPathSegments().get(1));
            Toast.makeText(getContext(), "Movie was saved into Favorite with uri: " + uri + " and id " + db_id,
                    Toast.LENGTH_LONG)
                    .show();
            return true;
        } else {
            Toast.makeText(getContext(), "Movie was not saved", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    //uses mdb_id to check if the Movie object downloaded from remote database is already saved to favorite_movies table
    private boolean movieIsInFavorite(long tmdb_id) {
        Uri uri = FavoriteMovieEntry.CONTENT_URI.buildUpon()
                .appendPath(FavoriteMovieEntry.PATH_FAVORITE_MOVIES_TMDB_ID).appendPath(Long.toString(tmdb_id)).build();
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount() == 0) {
            return false;
        }
        cursor.moveToFirst();
        db_id = cursor.getInt(cursor.getColumnIndex(FavoriteMovieEntry._ID));
        return true;
    }

    //inner anonymous class that implements LoaderCallbacks
    private LoaderManager.LoaderCallbacks<ArrayList<YouTubeTrailer>> trailerResultLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<YouTubeTrailer>>() {

        @Override
        public Loader<ArrayList<YouTubeTrailer>> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_ID) {
                return new TrailerInfoLoader(getContext());
            } else return null;
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<YouTubeTrailer>> loader, ArrayList<YouTubeTrailer> data) {
            int numElements = data.size();
            if (numElements != 0) {
                mAdapter = new TrailerAdapter(data, new TrailerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String keyStr) {
                        startActivity(YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                                BuildConfig.YOUTUBE_API_KEY, keyStr, 0, true, true));
                    }
                });
                mAdapter.setProgressBar(mProgressBar);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mRecyclerView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                trailerView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<YouTubeTrailer>> loader) {

        }
    };


    public static long getmTMDB_ID() {
        return mTMDB_ID;
    }
}
