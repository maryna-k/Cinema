package com.example.android.moviesapp;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviesapp.database.MovieContract.FavoriteMovieEntry;
import com.squareup.picasso.Picasso;


public class CursorMovieAdapter extends RecyclerView.Adapter<CursorMovieAdapter.ViewHolder>{

    private Cursor mCursor;
    private static final String LOG_TAG = CursorMovieAdapter.class.getSimpleName() + "LOG";
    private Context context;
    private MovieAdapter.OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewItem;
        private TextView titleView;

        public ViewHolder(View view) {
            super(view);
            imageViewItem = (ImageView) view.findViewById(R.id.grid_poster);
            titleView = (TextView) view.findViewById(R.id.title_without_poster);
        }

        public void bind(final Movie movie, final MovieAdapter.OnItemClickListener listener) {
            String imageAddress = movie.getPosterAddress();
            Log.v(LOG_TAG, "Poster address: " + imageAddress);
            String fullImageAddress = "http://image.tmdb.org/t/p/w780/" + movie.getPosterAddress();
            Picasso.with(itemView.getContext()).load(fullImageAddress).into(imageViewItem);
            if(imageAddress.equals("null")){
                titleView.setText(movie.getTitle());
                titleView.setVisibility(View.VISIBLE);
            } else titleView.setVisibility(View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(movie);
                }
            });
        }
    }


    public CursorMovieAdapter(Cursor mCursor, MovieAdapter.OnItemClickListener listener){
        this.mCursor = mCursor;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.grid_item_movie, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
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
        Movie movie = new Movie(title, overview, rating, 0, null, releaseDay, imageAddress, null, tmdb_id);

        //set tag for tmdb_id (it will be used in FavoriteGridFragment to delete Movie object)
        holder.itemView.setTag(tmdb_id);

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
