package com.example.android.moviesapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends ArrayAdapter<Movie>{

    private ViewHolder holder;
    private final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context,ArrayList<Movie> movies){
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        Log.v(LOG_TAG, "The movie in the adapter: ");
        Log.v(LOG_TAG, movie.toString());
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
            holder = new ViewHolder();
            holder.imageViewItem = (ImageView) convertView.findViewById(R.id.grid_poster);
            convertView.setTag(holder);
        }

        String address = "http://image.tmdb.org/t/p/w780/" + movie.getImageAddress();
        holder = (ViewHolder) convertView.getTag();
        //ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_poster);
        Picasso.with(getContext()).load(address).into(holder.imageViewItem);
        return convertView;
    }

    public static  class ViewHolder {
        ImageView imageViewItem;
    }
}














