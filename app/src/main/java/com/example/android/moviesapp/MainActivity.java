package com.example.android.moviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationViewDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    /**Api for adding fragments at run-time:
    https://developer.android.com/training/basics/fragments/fragment-ui.html */

    /**Navigation drawer was created using the tutorial
    https://github.com/codepath/android_guides/wiki/Fragment-Navigation-Drawer*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Set a Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerToggle = setupDrawerToggle();

        //tie mDrawerLayout to the ActionBarToggle
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mNavigationViewDrawer = (NavigationView) findViewById(R.id.navigation_view);
        setupDrawerContent(mNavigationViewDrawer);

        //if the activity is restored, no need to create new fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, new MovieGridFragment()).commit();
        }
        /* ensures that application is properly initialized with default settings.
           Third boolean parameters allows setting values not to be overwritten each
           time activity is created */
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch(item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem){
        //Create a new fragment and specify the fragment to show based on
        //the navigation item clicked
        Fragment fragment = null;
        Class fragmentClass;
        if(menuItem.getItemId() == R.id.nav_favorite_fragment){
            fragmentClass = FavoriteGridFragment.class;
        } else {
            //get title from the drawer and update moviesToSearch variable
            String title = menuItem.getTitle().toString();
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            settings.edit().putString(getString(R.string.pref_search_key), title).commit();
            //MovieGridFragment.moviesToSearch = title;
            fragmentClass = MovieGridFragment.class;
        }

        try{
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
        //Highlight selected item in the drawer
        menuItem.setChecked(true);
        //Change the title of the ActionBar
        setTitle(getString(R.string.app_name) + ": " + menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle(){
        return new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    /*since java has no map literals and categoriesKeyValuePairs is a class variable, initialization
      should be done in a static initializer */
    public static final HashMap<String, String> categoriesKeyValuePairs = new HashMap<>();
    static {
        categoriesKeyValuePairs.put("Popular", "movie/popular");
        categoriesKeyValuePairs.put("Top Rated", "movie/top_rated");
        categoriesKeyValuePairs.put("Action", "28");
        categoriesKeyValuePairs.put("Adventure", "12");
        categoriesKeyValuePairs.put("Animation", "16");
        categoriesKeyValuePairs.put("Comedy", "35");
        categoriesKeyValuePairs.put("Crime", "80");
        categoriesKeyValuePairs.put("Documentary", "99");
        categoriesKeyValuePairs.put("Drama", "18");
        categoriesKeyValuePairs.put("Family", "10751");
        categoriesKeyValuePairs.put("Fantasy", "14");
        categoriesKeyValuePairs.put("Foreign", "10769");
        categoriesKeyValuePairs.put("Horror", "27");
        categoriesKeyValuePairs.put("Music", "10402");
        categoriesKeyValuePairs.put("Mystery", "9648");
        categoriesKeyValuePairs.put("Romance", "10749");
        categoriesKeyValuePairs.put("Science Fiction", "878");
        categoriesKeyValuePairs.put("TV Movie", "10770");
        categoriesKeyValuePairs.put("Thriller", "53");
        categoriesKeyValuePairs.put("War", "10752");
        categoriesKeyValuePairs.put("Western", "37");
    }
}
