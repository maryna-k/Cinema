package com.example.android.moviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviesapp.database.MovieContract.FavoriteMovieEntry;
import com.squareup.picasso.Picasso;


public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private Movie movie;
    private String mTitle;
    private String mOverview;
    private String mReleaseDate;
    private double mRating;
    private int mVoteCount;
    private String mImageAddress;
    private boolean favorite;
    private long mTMDB_ID;

    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        //get Movie Object from the intent
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            movie = (Movie)intent.getSerializableExtra("movie");
            mTMDB_ID = movie.getMdb_id();

            /*intent.hasExtra("favorite") is true if this intent was sent from FavoriteGridFragment
            * and therefore this movie is in database. Otherwise, fragment should check if the tmdb_id of this
            * Movie object is already in database */
            if(intent.hasExtra("favorite")){
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
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){
        if(favorite){
            //menu.getItem(R.id.action_add_to_favorite).setIcon(R.drawable.ic_in_favorite);
            menu.findItem(R.id.action_remove_from_favorite).setVisible(true);
            menu.findItem(R.id.action_add_to_favorite).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_to_favorite:
                boolean inserted = insertMovie();
                if (inserted) {
                    item.setIcon(R.drawable.ic_in_favorite);
                }
                break;
            case R.id.action_remove_from_favorite:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean insertMovie() {

        ContentValues values = new ContentValues();
        values.put(FavoriteMovieEntry.COLUMN_NAME_TITLE, mTitle);
        values.put(FavoriteMovieEntry.COLUMN_NAME_OVERVIEW, mOverview);
        values.put(FavoriteMovieEntry.COLUMN_NAME_RATING, mRating);
        values.put(FavoriteMovieEntry.COLUMN_NAME_RELEASE, mReleaseDate);
        values.put(FavoriteMovieEntry.COLUMN_NAME_IMAGE_ADDRESS, mImageAddress);
        values.put(FavoriteMovieEntry.COLUMN_NAME_MDB_ID, mTMDB_ID);

        Uri uri = getContext().getContentResolver().insert(FavoriteMovieEntry.CONTENT_URI, values);
        if (uri != null){
            favorite = true;
            Toast.makeText(getContext(), "Movie was saved into Favorite with uri: " + uri, Toast.LENGTH_LONG)
                    .show();
            return true;
        } else {
            Toast.makeText(getContext(), "Movie was not saved", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    //checks if the movie with particular tmdb_id is already in favorite
    private boolean movieIsInFavorite(long tmdb_id){
        Uri uri = FavoriteMovieEntry.CONTENT_URI.buildUpon()
                .appendPath(FavoriteMovieEntry.PATH_FAVORITE_MOVIES_TMDB_ID).appendPath(Long.toString(tmdb_id)).build();
        Cursor cursor = getContext().getContentResolver().query(uri, null,null,null,null);

        if (cursor.getCount() == 0){
            return false;
        }
        return true;
    }
}
