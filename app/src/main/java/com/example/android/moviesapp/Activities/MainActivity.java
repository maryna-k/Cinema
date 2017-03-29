package com.example.android.moviesapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.example.android.moviesapp.Fragments.DetailFragment;
import com.example.android.moviesapp.Fragments.FavoriteGridFragment;
import com.example.android.moviesapp.Objects.Movie;
import com.example.android.moviesapp.Fragments.MainGridFragment;
import com.example.android.moviesapp.R;
import com.example.android.moviesapp.Objects.Review;
import com.example.android.moviesapp.Fragments.ReviewDialogFragment;
import com.example.android.moviesapp.utilities.FragmentCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FragmentCallback,
        DetailFragment.ReviewFragmentCallback, DetailFragment.SettingsFragmentCallback {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationViewDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private Toolbar secondaryToolbar;
    private static String preferenceMoviesToSearch;
    private static String moviesToSearch;

    private LinearLayout emptyView;
    private FrameLayout movieGridContainet;

    private boolean isTabletTwoPaneLayout;
    private boolean isTabletPortraitLayout;

    private Movie lastViewedMovie;
    private static final String LAST_VIEWED_MOVIE = "last_viewed";

    public static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static final String GRIDFRAGMENT_TAG = "GFTAG";

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    /**Api for adding fragments at run-time:
     https://developer.android.com/training/basics/fragments/fragment-ui.html */

    /**
     * Navigation drawer was created using the tutorial
     * https://github.com/codepath/android_guides/wiki/Fragment-Navigation-Drawer
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isTabletTwoPaneLayout = getResources().getBoolean(R.bool.isTabletTwoPane);
        isTabletPortraitLayout = getResources().getBoolean(R.bool.isTabletPortrait);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = setupDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mNavigationViewDrawer = (NavigationView) findViewById(R.id.navigation_view);
        setupDrawerContent(mNavigationViewDrawer);

        // ensures that application is properly initialized with default settings
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        //get preferenceMoviesToSearch from shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        preferenceMoviesToSearch = settings.getString(getString(R.string.pref_search_key),
                getString(R.string.pref_search_default));

        movieGridContainet = (FrameLayout) findViewById(R.id.movie_grid_container);

        if (savedInstanceState == null) {
            moviesToSearch = preferenceMoviesToSearch;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_grid_container, new MainGridFragment(), GRIDFRAGMENT_TAG).commit();
        } else {
            moviesToSearch = savedInstanceState.getString("searchCategory");
        }
        this.setTitle(getString(R.string.app_name) + ": " + moviesToSearch);

        if (isTabletTwoPaneLayout) {
            secondaryToolbar = (Toolbar) findViewById(R.id.secondary_toolbar);
            emptyView = (LinearLayout) findViewById(R.id.empty_movie_view_layout);
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
        mDrawerToggle.onConfigurationChanged(newConfig);
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
        mDrawerToggle.syncState();
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
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

    //method from callback interface between MainGridFragment, FavoriteMovieFragment and activities
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
        //Create a new fragment and specify the fragment to show based on
        //the navigation item clicked
        Fragment fragment = null;
        Class fragmentClass;
        if (menuItem.getItemId() == R.id.nav_favorite_fragment) {
            fragmentClass = FavoriteGridFragment.class;
        } else {
            //MainGridFragment.moviesToSearch = title;
            fragmentClass = MainGridFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.movie_grid_container, fragment).commit();
        //Highlight selected item in the drawer
        menuItem.setChecked(true);
        //get title from the drawer and update moviesToSearch variable
        String title = menuItem.getTitle().toString();
        moviesToSearch = title;
        //Change the title of the ActionBar
        setTitle(getString(R.string.app_name) + ": " + title);
        mDrawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(
                this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
    }

    private void setEmptyMovieDetailViewVisible(boolean visible) {
        if (visible) {
            emptyView.setVisibility(View.VISIBLE);
            ImageView emptyViewImage = (ImageView) findViewById(R.id.empty_view_image);
            TextView emptyViewText = (TextView) findViewById(R.id.empty_view_message);
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
