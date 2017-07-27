package com.example.android.moviesapp.mvp.movieGrid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.moviesapp.R;
import com.example.android.moviesapp.adapters.MovieAdapter;
import com.example.android.moviesapp.models.Movie;
import com.example.android.moviesapp.mvp.data.RemoteDataSource;
import com.example.android.moviesapp.utilities.EndlessRecyclerViewScrollListener;
import com.example.android.moviesapp.utilities.FragmentCallback;
import com.example.android.moviesapp.utilities.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.android.moviesapp.activities.MainActivity.getMoviesToSearch;

/** This fragment displays movies received from the server call*/

public class MainGridFragment extends Fragment implements MovieGridContract.View {

    private final String LOG_TAG = MainGridFragment.class.getSimpleName();

    private MovieAdapter adapter;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;

    private android.view.View rootView;
    @BindView(R.id.empty_grid_view_layout) LinearLayout emptyMovieGridLayout;
    @BindView(R.id.empty_view_image) ImageView emptyImage;
    @BindView(R.id.empty_view_message) TextView emptyText;

    private static boolean loadMoreMovies;

    private MovieGridPresenter presenter;

    @BindInt(R.integer.grid_columns) int GRID_COLUMNS_NUM;


    private Unbinder unbinder;

    public MainGridFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container,
                                          Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        layoutManager = new GridLayoutManager(getContext(), GRID_COLUMNS_NUM);
        recyclerView.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager, getContext()) {
            @Override
            public void onLoadMore() {
                loadMoreMovies = true;
                loadNextDataFromApi();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        presenter = new MovieGridPresenter(this, RemoteDataSource.getInstance(), getContext());
        if(NetworkUtils.checkNetworkConnection(getContext()) || savedInstanceState != null) {
            presenter.loadMovies(getMoviesToSearch());
        } else {
            setEmptyGridViewVisible(true);
        }

        presenter.createConnectionWatcher();
        return rootView;
    }

    @Override
    public void showMovies(ArrayList<Movie> list){
        if(adapter == null) {
            scrollListener.resetState();
            createAdapter(list);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.addData(list);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Activate the navigation drawer toggle
        if(item.getItemId() == R.id.action_refresh){
            updateMovieList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        presenter.createConnectionWatcher();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.stopConnectionWatcher();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.unsubscribe();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void loadNextDataFromApi(){
        if(loadMoreMovies){
            loadMoreMovies = false;
            presenter.loadMovies(getMoviesToSearch());
        }
    }

    @Override
    public void updateMovieList(){
        if(emptyMovieGridLayout != null
                && emptyMovieGridLayout.getVisibility() == android.view.View.VISIBLE) {
            adapter = null;
            presenter.loadMovies(getMoviesToSearch());
        }
    }

    private void createAdapter(ArrayList<Movie> data){
        adapter = new MovieAdapter(GRID_COLUMNS_NUM, data, new MovieAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(Movie movie){
                /* false here just indicates that DetailFragment has to check the database,
                to see if the movie is in favorite */
                ((FragmentCallback) getActivity()).onItemSelected(movie);
            }
        });
    }

    @Override
    public void setEmptyGridViewVisible(boolean visible){
        if(emptyMovieGridLayout != null) {
            if (visible) {
                emptyMovieGridLayout.setVisibility(android.view.View.VISIBLE);
                emptyImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_empty_movie_grid));
                emptyText.setText(getText(R.string.empty_movie_gridview));
            } else {
                emptyMovieGridLayout.setVisibility(android.view.View.GONE);
            }
        }
    }

    public static void setLoadMoreMovies(boolean load){
        loadMoreMovies = load;
    }
}
