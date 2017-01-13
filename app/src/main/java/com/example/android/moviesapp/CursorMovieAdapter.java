package com.example.android.moviesapp;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.moviesapp.database.MovieContract.*;


public class CursorMovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    private Cursor mCursor;
    private final String LOG_TAG = CursorMovieAdapter.class.getSimpleName();
    private Context context;
    private MovieAdapter.OnItemClickListener listener;


    public CursorMovieAdapter(Cursor mCursor, MovieAdapter.OnItemClickListener listener){
        this.mCursor = mCursor;
        this.listener = listener;
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.grid_item_movie, parent, false);
        return new MovieAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder holder, int position){
        mCursor.moveToPosition(position);
        String imageAddress = mCursor.getString(
                mCursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_IMAGE_ADDRESS));
        String title = mCursor.getString(
                mCursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_TITLE));
        String overview = mCursor.getString(
                mCursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_OVERVIEW));
        String releaseDay = mCursor.getString(
                mCursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_RELEASE));
        double rating = mCursor.getDouble(
                mCursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_RATING));
        long tmdb_id = mCursor.getLong(
                mCursor.getColumnIndex(FavoriteMovieEntry.COLUMN_NAME_MDB_ID));
        Movie movie = new Movie(title, overview, rating, releaseDay, imageAddress, tmdb_id);
        holder.bind(movie, listener);
    }

    @Override
    public int getItemCount(){
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; //nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
}
