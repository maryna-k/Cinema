package com.example.android.moviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private ArrayList<Movie> mList;
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName() + "LOG";
    private static Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewItem;
        private TextView titleView;

        public ViewHolder(View view) {
            super(view);
            imageViewItem = (ImageView) view.findViewById(R.id.grid_poster);
            titleView = (TextView) view.findViewById(R.id.title_without_poster);
        }

        public void bind(final Movie movie, final OnItemClickListener listener) {
            String imageAddress = movie.getImageAddress();
            Log.v(LOG_TAG, "Poster address: " + imageAddress);
            String fullImageAddress = "http://image.tmdb.org/t/p/w780/" + movie.getImageAddress();
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

    public MovieAdapter(ArrayList<Movie> mList, OnItemClickListener listener) {
        this.mList = mList;
        this.listener = listener;
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.grid_item_movie, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void clearData() {
        if (getItemCount() > 0) {
            mList.clear();
        }
        notifyDataSetChanged();
    }

    public void addData(ArrayList<Movie> data) {
        mList.addAll(data);
        /*for(int i = 0; i < mList.size(); i++){
            Log.v(LOG_TAG, i+ ". " + mList.get(i).getTitle());
        }*/
        this.notifyItemRangeChanged(mList.size() + 1, data.size());
        this.notifyDataSetChanged();
    }
}