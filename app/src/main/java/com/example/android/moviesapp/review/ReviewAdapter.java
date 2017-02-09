package com.example.android.moviesapp.review;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.moviesapp.R;

import java.util.ArrayList;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private ArrayList<Review> mList;
    private final String LOG_TAG = ReviewAdapter.class.getSimpleName() + "LOG";
    private Context context;
    private static boolean[] shouldBeHidden;
    private static final int SUBSTRING_LENGTH = 400;

    public static  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView authorView;
        private TextView contentViewSubstring;
        private TextView contentViewFull;
        private ImageView expandReview;
        private Context context;
        private String contentStr;

        public ViewHolder(Context context, View view){
            super(view);
            this.context = context;
            LinearLayout review_layout = (LinearLayout) view.findViewById(R.id.review_layout);
            authorView = (TextView) view.findViewById(R.id.reviewer_name_activity);
            contentViewSubstring = (TextView) view.findViewById(R.id.review_content_substring);
            contentViewFull = (TextView) view.findViewById(R.id.review_content_full);
            expandReview = (ImageView) view.findViewById(R.id.expand_review_activity);
            review_layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(contentStr.length() > SUBSTRING_LENGTH) {
                int position = getLayoutPosition(); // gets item position
                if (!shouldBeHidden[position]) {
                    contentViewSubstring.setVisibility(View.GONE);
                    contentViewFull.setVisibility(View.VISIBLE);
                    expandReview.setImageResource(R.drawable.ic_action_hide_review);
                    shouldBeHidden[position] = true;
                } else {
                    contentViewSubstring.setVisibility(View.VISIBLE);
                    contentViewFull.setVisibility(View.GONE);
                    expandReview.setImageResource(R.drawable.ic_action_expand_review);
                    shouldBeHidden[position] = false;
                }
            }
        }
    }

    public ReviewAdapter(ArrayList<Review> mList){
        this.mList = mList;
        shouldBeHidden = new boolean[mList.size()];
        for(int i = 0; i<mList.size(); i++){
            shouldBeHidden[i] = true;
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
        holder.contentViewFull.setText(holder.contentStr);
        holder.expandReview.setVisibility(View.VISIBLE);

        if(holder.contentStr.length() > SUBSTRING_LENGTH) {
            final String contentStrSubstring = (holder.contentStr.substring(0, SUBSTRING_LENGTH) + "...");
            holder.contentViewSubstring.setText(contentStrSubstring);

            if (shouldBeHidden[position]) {
                holder.contentViewSubstring.setVisibility(View.VISIBLE);
                holder.contentViewFull.setVisibility(View.GONE);
                holder.expandReview.setImageResource(R.drawable.ic_action_expand_review);
            } else {
                holder.contentViewSubstring.setVisibility(View.GONE);
                holder.contentViewFull.setVisibility(View.VISIBLE);
                holder.expandReview.setImageResource(R.drawable.ic_action_hide_review);
            }
        } else {
            holder.contentViewSubstring.setVisibility(View.GONE);
            holder.contentViewFull.setVisibility(View.VISIBLE);
            holder.expandReview.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount(){
        return mList.size();
    }
}
