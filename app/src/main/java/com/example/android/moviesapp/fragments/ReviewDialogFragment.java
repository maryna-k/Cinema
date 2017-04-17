package com.example.android.moviesapp.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.moviesapp.R;
import com.example.android.moviesapp.models.Review;
import com.example.android.moviesapp.adapters.ReviewAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.android.moviesapp.activities.ReviewActivity.REVIEW_LIST;

/** Displays reviews in a DialogFragment in tablet layout*/

public class ReviewDialogFragment extends DialogFragment {

    private View rootView;
    @BindView(R.id.review_recyclerview) RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ReviewAdapter adapter;
    private ArrayList<Review> reviewList;
    private Unbinder unbinder;

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
        unbinder = ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if(arguments != null){
            reviewList = (ArrayList<Review>) arguments.getSerializable(REVIEW_LIST);
        }
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new ReviewAdapter(reviewList);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
    }
}
