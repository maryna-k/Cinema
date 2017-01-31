package com.example.android.moviesapp.review;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviesapp.R;

import java.util.ArrayList;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private ArrayList<Review> mList;
    private final String LOG_TAG = ReviewAdapter.class.getSimpleName();
    private Context context;
    private static boolean[] shouldBeHidden;


    public static  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView authorView;
        private TextView contentViewSubstring;
        private TextView contentViewFull;
        private ImageView expandReview;
        private Context context;


        public ViewHolder(Context context, View view){
            super(view);
            this.context = context;
            authorView = (TextView) view.findViewById(R.id.reviewer_name_activity);
            contentViewSubstring = (TextView) view.findViewById(R.id.review_content_substring);
            contentViewFull = (TextView) view.findViewById(R.id.review_content_full);
            expandReview = (ImageView) view.findViewById(R.id.expand_review_activity);
            expandReview.setOnClickListener(this);
            contentViewSubstring.setOnClickListener(this);
            contentViewFull.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition(); // gets item position
            //Toast.makeText(context, "Blah", Toast.LENGTH_SHORT).show();
            if (!shouldBeHidden[position]){
                contentViewSubstring.setVisibility(View.GONE);
                contentViewFull.setVisibility(View.VISIBLE);
                expandReview.setImageResource(R.drawable.ic_action_hide_review);
                shouldBeHidden[position] = true;
            }
            else{
                contentViewSubstring.setVisibility(View.VISIBLE);
                contentViewFull.setVisibility(View.GONE);
                expandReview.setImageResource(R.drawable.ic_action_expand_review);
                shouldBeHidden[position] = false;
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
        final String contentStr = reviewItem.getReviewContent();
        final String contentStrSubstring = (contentStr.substring(0, 400) + "...");
        holder.authorView.setText(reviewItem.getAuthor());
        holder.contentViewSubstring.setText(contentStrSubstring);
        holder.contentViewFull.setText(contentStr);
        holder.expandReview.setVisibility(View.VISIBLE);

        if(shouldBeHidden[position]){
            holder.contentViewSubstring.setVisibility(View.VISIBLE);
            holder.contentViewFull.setVisibility(View.GONE);
            holder.expandReview.setImageResource(R.drawable.ic_action_expand_review);
        } else {
            holder.contentViewSubstring.setVisibility(View.GONE);
            holder.contentViewFull.setVisibility(View.VISIBLE);
            holder.expandReview.setImageResource(R.drawable.ic_action_hide_review);
        }

        /*holder.expandReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandListener.onItemClick(contentStr);
                notifyItemChanged(position);
            }
        });

        holder.collapseReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapseListener.onItemClick(contentStr);
                notifyItemChanged(position);
            }
        });*/

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Clicked", String.valueOf(holder.getAdapterPosition()));
                if (holder.contentViewSubstring.getVisibility()==View.VISIBLE){
                    holder.contentViewSubstring.setVisibility(View.GONE);
                    holder.contentViewFull.setVisibility(View.VISIBLE);
                    holder.expandReview.setImageResource(R.drawable.ic_action_hide_review);
                    shouldBeHidden[position] = true;
                }
                else{
                    holder.contentViewSubstring.setVisibility(View.VISIBLE);
                    holder.contentViewFull.setVisibility(View.GONE);
                    holder.expandReview.setImageResource(R.drawable.ic_action_expand_review);
                    shouldBeHidden[position] = false;
                }

            }
        });*/
    }

    @Override
    public int getItemCount(){
        return mList.size();
    }
}
