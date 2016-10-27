package com.example.android.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
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

    private MovieAdapter adapter;
    private ArrayList<Movie> movieList;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ArrayAdapter<String> drawerAdapter;
    private ActionBarDrawerToggle drawerToggle;


    /*since java has no map literals and searchCategories is a class variable, initialization
      should be done in a static initializer */
    private static final HashMap<String, String> searchCategories = new HashMap<>();
    static {
        searchCategories.put("Popular", "movie/popular");
        searchCategories.put("Top Rated", "top_rated");
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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        adapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        GridView gv = (GridView) rootView.findViewById(R.id.gridview);
        gv.setAdapter(adapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movie", adapter.getItem(i));
                startActivity(intent);
            }
        });

        drawerLayout = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        drawerList = (ListView) rootView.findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        drawerAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.drawer_list_item, R.id.drawer_item, new ArrayList<String>(searchCategories.keySet()));
        drawerList.setAdapter(drawerAdapter);
        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        //change drawer icon
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        setupDrawer();

        checkNetworkConnection();
        return rootView;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Highlight the selected item, update the title, and close the drawer
            drawerList.setItemChecked(position, true);
            //getActivity().setTitle(searchCategories.get(drawerAdapter.getItem(position)));
            getActivity().setTitle(drawerAdapter.getItem(position));
            drawerLayout.closeDrawer(drawerList);
        }
    }

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActivity().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Activate the navigation drawer toggle
        if (drawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    private void checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new DownloadMovieDataTask().execute();
        } else {
            // display an error as a toast message
            Toast.makeText(getContext(), "No Internet connection", Toast.LENGTH_LONG).show();
        }
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


        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;
            final String urlString = "http://api.themoviedb.org/3/movie/popular?api_key=" + BuildConfig.TMDb_API_KEY;
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
                adapter.clear();
                for (int i = 0; i < result.size(); i++) {
                    adapter.insert(result.get(i), i);
                }
            }
        }
    }
}
