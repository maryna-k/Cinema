package com.example.android.moviesapp.adapters;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviesapp.models.Movie;
import com.example.android.moviesapp.R;
import com.example.android.moviesapp.database.DatabaseUtilMethods;
import com.example.android.moviesapp.utilities.ImageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CursorMovieAdapter extends RecyclerView.Adapter<CursorMovieAdapter.ViewHolder>{

    private Cursor mCursor;
    private static final String LOG_TAG = CursorMovieAdapter.class.getSimpleName() + "LOG";
    private Context context;
    private MovieAdapter.OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.grid_poster) ImageView imageViewItem;
        @BindView(R.id.title_without_poster) TextView titleView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final Movie movie, final MovieAdapter.OnItemClickListener listener) {
            final String posterPath = movie.getPosterStoragePath();
            if(posterPath == null || posterPath.equals("")) {
                titleView.setText(movie.getTitle());
                titleView.setVisibility(View.VISIBLE);
                imageViewItem.setVisibility(View.GONE);
            } else {
                titleView.setVisibility(View.GONE);
                imageViewItem.setVisibility(View.VISIBLE);
                final Bitmap posterBitmap = ImageUtils.getPosterFromStorage(posterPath, Long.toString(movie.getTmdb_id()));
                imageViewItem.setImageBitmap(posterBitmap);
            }
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
        Movie movie = DatabaseUtilMethods.getMovieFromCursor(position, mCursor);
        long tmdb_id = movie.getTmdb_id();

        //set tag for tmdb_id that will be used in FavoriteGridFragment to delete Movie object
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
