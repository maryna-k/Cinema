package com.example.android.moviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.moviesapp.models.YouTubeTrailer;
import com.example.android.moviesapp.R;
import com.example.android.moviesapp.utilities.Keys;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    private static Context context;
    private ArrayList<YouTubeTrailer> trailerList;
    private OnItemClickListener listener;
    private static ProgressBar progressBar;

    private static final int UNINITIALIZED = 1;
    private static final int INITIALIZING = 2;
    private static final int INITIALIZED = 3;

    public interface OnItemClickListener {
        void onItemClick(String key);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.trailer_thumbnail) YouTubeThumbnailView thumbView;

        public ViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            initialize();
        }

        public void initialize(){
            thumbView.setTag(R.id.initialize, INITIALIZING);
            thumbView.setTag(R.id.thumbnail_loader, null);
            thumbView.setTag(R.id.trailer_key, "");

            thumbView.initialize(Keys.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    thumbView.setTag(R.id.initialize, INITIALIZED);
                    thumbView.setTag(R.id.thumbnail_loader, youTubeThumbnailLoader);

                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                        @Override
                        public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String loadedVideoId) {
                            String currentVideoId = (String) thumbView.getTag(R.id.trailer_key);
                        }

                        @Override
                        public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                        }
                    });

                    String videoId = (String) thumbView.getTag(R.id.trailer_key);
                    if(videoId != null && !videoId.isEmpty()){
                        youTubeThumbnailLoader.setVideo(videoId);
                        progressBar.setVisibility(View.INVISIBLE);
                        thumbView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult errorReason) {
                    thumbView.setTag(R.id.initialize, UNINITIALIZED);
                    final String errorMessage = errorReason.toString();
                    progressBar.setVisibility(View.VISIBLE);
                    thumbView.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    public TrailerAdapter(ArrayList<YouTubeTrailer> list, OnItemClickListener listener) {
        this.trailerList = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View trailerView = LayoutInflater.from(context)
                .inflate(R.layout.trailer_item, parent, false);
        return new ViewHolder(trailerView, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String keyStr = trailerList.get(position).getKey();
        holder.thumbView.setTag(R.id.trailer_key, keyStr);

        int state = (int) holder.thumbView.getTag(R.id.initialize);

        if(state == UNINITIALIZED){
            holder.initialize();
        }
        else if(state == INITIALIZED){
            YouTubeThumbnailLoader loader = (YouTubeThumbnailLoader) holder.thumbView.getTag(R.id.thumbnail_loader);
            loader.setVideo(keyStr);
        }

        holder.thumbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(keyStr);
            }
        });
    }

    @Override
    public int getItemCount(){
        return trailerList.size();
    }

    public void setProgressBar(View view){
        progressBar = (ProgressBar) view;
    }
}
