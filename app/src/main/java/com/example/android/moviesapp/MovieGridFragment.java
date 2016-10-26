package com.example.android.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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

public class MovieGridFragment extends Fragment {

    private MovieAdapter adapter;
    private ArrayList<Movie> movieList;

    public MovieGridFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
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

        checkNetworkConnection();
        return rootView;
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
