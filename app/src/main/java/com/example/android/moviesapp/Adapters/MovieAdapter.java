package com.example.android.moviesapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.moviesapp.Objects.Movie;
import com.example.android.moviesapp.R;
import com.example.android.moviesapp.utilities.Keys;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private ArrayList<Movie> mList;
    private static Context context;
    private OnItemClickListener listener;
    private int extraItemsNum;
    private final int moviesNumPerPage = 20;
    private final int VIEW_ITEM = 0;
    private final int VIEW_PROGRESS = 1;

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
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.grid_item_progress_bar);
        }
    }

    public MovieAdapter(int extraItemsNum, ArrayList<Movie> mList, OnItemClickListener listener) {
        this.extraItemsNum = extraItemsNum;
        this.mList = mList;
        this.listener = listener;
        if(this.mList.size() == moviesNumPerPage) {
            addProgressViews(extraItemsNum);
        } else if(this.mList.size()%moviesNumPerPage < extraItemsNum){
            addProgressViews(extraItemsNum - this.mList.size()%moviesNumPerPage);
        } else if(this.mList.size()%moviesNumPerPage > extraItemsNum){
            removeProgressViews(this.mList.size()%moviesNumPerPage - extraItemsNum);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.grid_item_movie, parent, false);
            viewHolder = new ViewHolder(itemView);
        } else {
            View progressItemView = LayoutInflater.from(context)
                    .inflate(R.layout.progress_bar, parent, false);
            viewHolder = new ProgressViewHolder(progressItemView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            final Movie movie = mList.get(position);
            String imageAddress = movie.getPosterAddress();
            String fullImageAddress = Keys.SMALL_POSTER_BASE_URL + movie.getPosterAddress();
            Picasso mPicasso = Picasso.with(((ViewHolder) holder).itemView.getContext());
            mPicasso.setIndicatorsEnabled(true);
            mPicasso.load(fullImageAddress).into(((ViewHolder) holder).imageViewItem);
            if (imageAddress.equals("null")) {
                ((ViewHolder) holder).titleView.setText(movie.getTitle());
                ((ViewHolder) holder).titleView.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).imageViewItem.setVisibility(View.GONE);
            } else {
                ((ViewHolder) holder).titleView.setVisibility(View.GONE);
                ((ViewHolder) holder).imageViewItem.setVisibility(View.VISIBLE);
            }
            ((ViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(movie);
                }
            });
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addData(ArrayList<Movie> data) {
        removeProgressViews(extraItemsNum);
        mList.addAll(data);
        addProgressViews(extraItemsNum);
        this.notifyItemRangeChanged(mList.size() + 1, data.size());
        this.notifyDataSetChanged();
    }

    private void addProgressViews(int extraNum){
        for(int i = 0; i < extraNum; i++){
            this.mList.add(null);
        }
    }

    private void removeProgressViews(int extraNum){
        for(int i = 1; i <= extraNum; i++){
            this.mList.remove(mList.size() - 1);
        }
    }
}