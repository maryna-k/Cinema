package com.example.android.moviesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MovieGridFragment extends Fragment {

    private final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    private RecyclerView.Adapter adapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Movie> movieList;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ArrayAdapter<String> drawerAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private int gridViewPosition = 0;
    private String drawerItemTitle;
    private int gridColumnsNum = 2;
    private View rootView;

    /*since java has no map literals and searchCategories is a class variable, initialization
      should be done in a static initializer */
    private static final HashMap<String, String> searchCategories = new HashMap<>();
    static {
        searchCategories.put("Popular", "movie/popular");
        searchCategories.put("Top Rated", "movie/top_rated");
        searchCategories.put("Action", "28");
        searchCategories.put("Adventure", "12");
        searchCategories.put("Animation", "16");
        searchCategories.put("Comedy", "35");
        searchCategories.put("Crime", "80");
        searchCategories.put("Documentary", "99");
        searchCategories.put("Drama", "18");
        searchCategories.put("Family", "10751");
        searchCategories.put("Fantasy", "14");
        searchCategories.put("Foreign", "10769");
        searchCategories.put("Horror", "27");
        searchCategories.put("Music", "10402");
        searchCategories.put("Mystery", "9648");
        searchCategories.put("Romance", "10749");
        searchCategories.put("Science Fiction", "878");
        searchCategories.put("TV Movie", "10770");
        searchCategories.put("Thriller", "53");
        searchCategories.put("War", "10752");
        searchCategories.put("Western", "37");
    }

    public MovieGridFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(getContext(), gridColumnsNum);
        mRecyclerView.setLayoutManager(layoutManager);

        /*gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movie", adapter.getItem(i));
                startActivity(intent);
            }
        });*/

        drawerLayout = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        drawerList = (ListView) rootView.findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        drawerAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.drawer_list_item, R.id.drawer_list_item_textview, new ArrayList<String>(searchCategories.keySet()));
        drawerList.setAdapter(drawerAdapter);
        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        //change drawer icon
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_drawer);
        setupDrawer();

        //recreate fragment state on rotation
        if (savedInstanceState == null) {
            //use SharedPreferences to get the default value of movie search
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
            drawerItemTitle = settings.getString(getString(R.string.pref_search_key),
                                                getString(R.string.pref_search_default));
            getActivity().setTitle(getString(R.string.app_name) + ": " + drawerItemTitle);
            Log.v(LOG_TAG, "SharedPreferences: " + drawerItemTitle);
        } else {
            drawerItemTitle = savedInstanceState.getString("searchCategory");
            getActivity().setTitle(getString(R.string.app_name) + ": " + drawerItemTitle);
            /*gridViewPosition = savedInstanceState.getInt("gridViewPosition");
            gv.smoothScrollToPosition(gridViewPosition);
            Log.v(LOG_TAG, Integer.toString(gridViewPosition));*/
        }
        DownloadMovieDataTask downloadMovies = new DownloadMovieDataTask();
        if (checkNetworkConnection()) downloadMovies.execute(drawerItemTitle);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("searchCategory", drawerItemTitle);
        /*gridViewPosition = gv.getFirstVisiblePosition();
        outState.putInt("gridViewPosition", gridViewPosition);*/
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Highlight the selected item, update the title, and close the drawer
            drawerList.setItemChecked(position, true);
            updateMovieList(((TextView) view.findViewById(R.id.drawer_list_item_textview)).getText().toString());
            //getActivity().setTitle(searchCategories.get(drawerAdapter.getItem(position)));
            drawerItemTitle = drawerAdapter.getItem(position);
            getActivity().setTitle(getString(R.string.app_name) + ": " + drawerItemTitle);
            drawerLayout.closeDrawer(drawerList);
        }
    }

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            //Called when a drawer has settled in a completely closed state
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActivity().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            //Called when a drawer has settled in a completely open state
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) return true;
        else {
            // display an error as a toast message
            Toast.makeText(getContext(), "No Internet connection", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void updateMovieList(String searchCriteria){
        if (checkNetworkConnection()) new DownloadMovieDataTask().execute(searchCriteria);
    }

    public class DownloadMovieDataTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = DownloadMovieDataTask.class.getSimpleName();

        private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr) throws JSONException {

            final String RESULTS = "results";
            final String TITLE = "title";
            final String OVERVIEW = "overview";
            final String RATING = "vote_average";
            final String RELEASE_DATE = "release_date";
            final String IMAGE_ADDRESS = "poster_path";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);

            if (movieList == null) movieList = new ArrayList<Movie>();
            else movieList.clear();

            for (int i = 0; i < movieArray.length(); i++) {

                Movie movie = new Movie();
                JSONObject movieObject = movieArray.getJSONObject(i);

                movie.setTitle(movieObject.getString(TITLE));
                movie.setOverview(movieObject.getString(OVERVIEW));
                movie.setRating(movieObject.getDouble(RATING));
                movie.setReleaseDate(movieObject.getString(RELEASE_DATE));
                movie.setImageAddress(movieObject.getString(IMAGE_ADDRESS));

                movieList.add(i, movie);
                Log.v(LOG_TAG, movie.toString());
            }
            return movieList;
        }

        private String buildMovieUrlHelper(String criteria1, String criteria2) {

            final String BASE_URL = "http://api.themoviedb.org/3/";
            final String API_KEY = "?api_key=" + BuildConfig.TMDb_API_KEY;

            return new StringBuilder()
                    .append(BASE_URL)
                    .append(criteria1)
                    .append(API_KEY)
                    .append(criteria2).toString();
        }

        private String buildMovieUrl(String category){
            String url;
            if (category.equals("Popular") || category.equals("Top Rated")) {
                url = buildMovieUrlHelper(searchCategories.get(category), "");
            } else {
                url = buildMovieUrlHelper("discover/movie", "&with_genres=" + searchCategories.get(category));
            }
            return url;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;
            final String urlString = buildMovieUrl(params[0]);
            Log.v(LOG_TAG, "Url: " + urlString);

            try {
                URL url = new URL(urlString);

                // Create the request to TMDb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "TMDb JSON string: " + movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            if (result != null) {
                adapter = new MovieAdapter(result);
                mRecyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Activate the navigation drawer toggle
        if (drawerToggle.onOptionsItemSelected(item)) return true;
        else if(item.getItemId() == R.id.action_refresh){
            updateMovieList(drawerItemTitle);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
