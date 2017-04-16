package com.example.android.moviesapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.moviesapp.R;
import com.example.android.moviesapp.Objects.Review;
import com.example.android.moviesapp.Adapters.ReviewAdapter;

import java.util.ArrayList;

import static com.example.android.moviesapp.Activities.ReviewActivity.REVIEW_LIST;

public class ReviewDialogFragment extends DialogFragment {

    private View rootView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ReviewAdapter mAdapter;
    private ArrayList<Review> reviewList;

    public ReviewDialogFragment(){}

    public static ReviewDialogFragment newInstance(ArrayList<Review> reviewList, String title) {
        ReviewDialogFragment frag = new ReviewDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(REVIEW_LIST, reviewList);
        frag.setArguments(arguments);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_review, container, false);
        Bundle arguments = getArguments();
        if(arguments != null){
            reviewList = (ArrayList<Review>) arguments.getSerializable(REVIEW_LIST);
        }
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.review_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new ReviewAdapter(reviewList);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }
}
