package com.example.android.moviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.moviesapp.R;
import com.example.android.moviesapp.models.Review;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    private ArrayList<Review> mList;
    private Context context;
    private static boolean[] isCollapsed;
    private static final int SUBSTRING_LENGTH = 400;

    public static  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.reviewer_name_activity) TextView authorView;
        @BindView(R.id.review_content) TextView contentView;
        @BindView(R.id.expand_review_activity) ImageView expandReview;
        @BindView(R.id.collapse_review_activity) ImageView collapseReview;
        @BindView(R.id.review_layout) RelativeLayout reviewLayout;
        private Context context;
        private String contentStr;
        private String contentStrSubstring;

        public ViewHolder(Context context, View view){
            super(view);
            this.context = context;
            ButterKnife.bind(this, view);
            reviewLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(contentStr.length() > SUBSTRING_LENGTH) {
                int position = getLayoutPosition(); // gets item position
                if (isCollapsed[position]) {
                    isCollapsed[position] = false;
                    contentView.setText(contentStr);
                    expandReview.setVisibility(View.GONE);
                    collapseReview.setVisibility(View.VISIBLE);
                } else {
                    isCollapsed[position] = true;
                    contentView.setText(contentStrSubstring);
                    expandReview.setVisibility(View.VISIBLE);
                    collapseReview.setVisibility(View.GONE);
                }
            }
        }
    }

    public ReviewAdapter(ArrayList<Review> mList){
        this.mList = mList;
        isCollapsed = new boolean[mList.size()];
        for(int i = 0; i<mList.size(); i++){
            isCollapsed[i] = true;
        }
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.review_item, parent, false);
        return new ViewHolder(context, itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        Review reviewItem = mList.get(position);
        holder.contentStr = reviewItem.getReviewContent();
        holder.authorView.setText(reviewItem.getAuthor());
        holder.contentView.setText(holder.contentStr);

        if (holder.contentStr.length() > SUBSTRING_LENGTH) {
            holder.contentStrSubstring = (holder.contentStr.substring(0, SUBSTRING_LENGTH) + "...");
            holder.contentView.setText(holder.contentStrSubstring);
            holder.expandReview.setVisibility(View.VISIBLE);
            holder.collapseReview.setVisibility(View.GONE);
        } else {
            holder.contentView.setText(holder.contentStr);
            holder.expandReview.setVisibility(View.GONE);
            holder.collapseReview.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount(){
        return mList.size();
    }
}
