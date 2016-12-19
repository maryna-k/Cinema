package com.example.android.moviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    private ArrayList<Movie> mList;
    private final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(Movie movie);
    }

    public static  class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageViewItem;

        public ViewHolder(View view){
            super(view);
            imageViewItem = (ImageView) view.findViewById(R.id.grid_poster);
        }

        public void bind(final Movie movie, final OnItemClickListener listener){
            String address = "http://image.tmdb.org/t/p/w780/" + movie.getImageAddress();
            Picasso.with(itemView.getContext()).load(address).into(imageViewItem);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    listener.onItemClick(movie);
                }
            });
        }
    }

    public MovieAdapter(ArrayList<Movie> mList, OnItemClickListener listener){
        this.mList = mList;
        this.listener = listener;
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.grid_item_movie, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.bind(mList.get(position), listener);
    }

    @Override
    public int getItemCount(){
        return mList.size();
    }

    public void clearData() {
        if (getItemCount() > 0) {
            mList.clear();
        }
        notifyDataSetChanged();
    }
}