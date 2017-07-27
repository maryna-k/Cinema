package com.example.android.moviesapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.moviesapp.R;
import com.example.android.moviesapp.fragments.DetailFragment;
import com.example.android.moviesapp.fragments.FavoriteGridFragment;
import com.example.android.moviesapp.fragments.ReviewDialogFragment;
import com.example.android.moviesapp.models.Movie;
import com.example.android.moviesapp.models.Review;
import com.example.android.moviesapp.mvp.movieGrid.MainGridFragment;
import com.example.android.moviesapp.sync.MovieSyncAdapter;
import com.example.android.moviesapp.utilities.FragmentCallback;

import java.util.ArrayList;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FragmentCallback,
        DetailFragment.ReviewFragmentCallback, DetailFragment.SettingsFragmentCallback {

    @BindBool(R.bool.isTabletTwoPane) boolean isTabletTwoPaneLayout;
    @BindBool(R.bool.isTabletPortrait) boolean isTabletPortraitLayout;

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view) NavigationView navigationViewDrawer;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.movie_grid_container) FrameLayout movieGridContainet;
    private ActionBarDrawerToggle drawerToggle;
    @Nullable @BindView(R.id.secondary_toolbar) Toolbar secondaryToolbar;
    @Nullable @BindView(R.id.empty_movie_view_layout) LinearLayout emptyView;
    @Nullable @BindView(R.id.empty_view_image) ImageView emptyViewImage;
    @Nullable @BindView(R.id.empty_view_message) TextView emptyViewText;

    private static String preferenceMoviesToSearch;
    private static String moviesToSearch;

    private Movie lastViewedMovie;
    private static final String LAST_VIEWED_MOVIE = "last_viewed";

    public static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static final String GRIDFRAGMENT_TAG = "GFTAG";

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Navigation drawer was created using the tutorial
     * https://github.com/codepath/android_guides/wiki/Fragment-Navigation-Drawer
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = setupDrawerToggle();
        drawerLayout.addDrawerListener(drawerToggle);
        setupDrawerContent(navigationViewDrawer);

        // ensures that application is properly initialized with default settings
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        preferenceMoviesToSearch = settings.getString(getString(R.string.pref_search_key),
                getString(R.string.pref_search_default));

        if (savedInstanceState == null) {
            moviesToSearch = preferenceMoviesToSearch;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_grid_container, new MainGridFragment(), GRIDFRAGMENT_TAG).commit();
        } else {
            moviesToSearch = savedInstanceState.getString("searchCategory");
        }
        this.setTitle(getString(R.string.app_name) + ": " + moviesToSearch);

        if (isTabletTwoPaneLayout) {
            if (savedInstanceState == null || savedInstanceState.getSerializable(LAST_VIEWED_MOVIE) == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container_main_activity, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
                setEmptyMovieDetailViewVisible(true);
            } else {
                lastViewedMovie = (Movie) savedInstanceState.getSerializable(LAST_VIEWED_MOVIE);
                if (emptyView.getVisibility() == View.VISIBLE) {
                    setEmptyMovieDetailViewVisible(false);
                }
                DetailFragment fragment = detailFragmentBundle(lastViewedMovie);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container_main_activity, fragment, DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else if (isTabletPortraitLayout) {
            if (savedInstanceState != null && savedInstanceState.getSerializable(LAST_VIEWED_MOVIE) != null) {
                //put the movie object to the local variables so that on rotation it will be saved in the bundle
                //and restored in the two pane layout
                lastViewedMovie = (Movie) savedInstanceState.getSerializable(LAST_VIEWED_MOVIE);
            }
        }
        MovieSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("searchCategory", moviesToSearch);
        if (lastViewedMovie != null) {
            outState.putSerializable(LAST_VIEWED_MOVIE, lastViewedMovie);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
        if (isTabletTwoPaneLayout && newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Fragment detailFragment = getSupportFragmentManager().findFragmentById(R.id.detail_container_main_activity);
            if (detailFragment instanceof DetailFragment) {
                getSupportFragmentManager().beginTransaction().remove(detailFragment).commit();
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onResume() {
        super.onResume();
        setToolbarPlaceholderHeight();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!isTabletTwoPaneLayout) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSettingsMenuItemSelected(boolean selected){
        if(selected){
            startActivity(new Intent(this, SettingsActivity.class));
        }
    }

    //method from FragmentCallback interface
    @Override
    public void onItemSelected(Movie movie) {
        lastViewedMovie = movie;

        if (isTabletTwoPaneLayout) {
            DetailFragment fragment = detailFragmentBundle(movie);
            if (emptyView.getVisibility() == View.VISIBLE) {
                setEmptyMovieDetailViewVisible(false);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container_main_activity, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailFragment.MOVIE_DETAIL, movie);
            startActivity(intent);
        }
    }

    //method from ReviewFragmentCallback interface
    @Override
    public void onMoreReviewsSelected(ArrayList<Review> reviewList, String title, int color) {
        DialogFragment newFragment = ReviewDialogFragment.newInstance(reviewList, title);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        //Create a new fragment and specify the fragment to show based on the navigation item clicked
        Fragment fragment = null;
        Class fragmentClass;
        if (menuItem.getItemId() == R.id.nav_favorite_fragment) {
            fragmentClass = FavoriteGridFragment.class;
        } else {
            fragmentClass = MainGridFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.movie_grid_container, fragment).commit();
        menuItem.setChecked(true);
        //get title from the drawer and update moviesToSearch variable
        String title = menuItem.getTitle().toString();
        moviesToSearch = title;
        //Change the title of the ActionBar
        setTitle(getString(R.string.app_name) + ": " + title);
        drawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(
                this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
    }

    private void setEmptyMovieDetailViewVisible(boolean visible) {
        if (visible) {
            emptyView.setVisibility(View.VISIBLE);
            emptyViewImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_empty_movie_detail));
            emptyViewText.setText(getText(R.string.empty_movie_detail));
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    public static String getMoviesToSearch() {
        return moviesToSearch;
    }

    private DetailFragment detailFragmentBundle(Movie movie) {
        Bundle args = new Bundle();
        args.putSerializable(DetailFragment.MOVIE_DETAIL, movie);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public Toolbar getSecondaryToolbar() {
        return secondaryToolbar;
    }

    private void setToolbarPlaceholderHeight() {
        if (isTabletTwoPaneLayout && toolbar != null) {
            View placeholder = (View) findViewById(R.id.toolbar_placeholder);
            placeholder.getLayoutParams().height = getActionBarHeight();
            placeholder.requestLayout();
        }
    }

    private int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }
}
