package com.example.android.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailFragment extends Fragment {

    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            Movie movie = (Movie)intent.getSerializableExtra("movie");

            ImageView header = (ImageView) rootView.findViewById(R.id.header);
            String headerImageAddress = "http://image.tmdb.org/t/p/w780/" + movie.getImageAddress();
            Picasso.with(getContext()).load(headerImageAddress).into(header);

            ImageView small_poster = (ImageView) rootView.findViewById(R.id.small_poster);
            String smallImageAddress = "http://image.tmdb.org/t/p/w500/" + movie.getImageAddress();
            Picasso.with(getContext()).load(smallImageAddress).into(small_poster);

            TextView title = (TextView) rootView.findViewById(R.id.title);
            title.setText(movie.getTitle());

            TextView release = (TextView) rootView.findViewById(R.id.release_date);
            release.setText(movie.getReleaseDate());

            TextView rating = (TextView) rootView.findViewById(R.id.rating);
            rating.setText(Double.toString(movie.getRating()));

            TextView overview = (TextView) rootView.findViewById(R.id.overview);
            overview.setText(movie.getOverview());
        }
        return rootView;
    }
}
