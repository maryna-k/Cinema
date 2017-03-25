package com.example.android.moviesapp;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviesapp.database.MovieContract.FavoriteMovieEntry;
import com.example.android.moviesapp.review.Review;
import com.example.android.moviesapp.review.ReviewLoader;
import com.example.android.moviesapp.trailer.TrailerAdapter;
import com.example.android.moviesapp.trailer.TrailerInfoLoader;
import com.example.android.moviesapp.trailer.YouTubeTrailer;
import com.example.android.moviesapp.utilities.Keys;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;


public class DetailFragment extends Fragment implements FavoriteGridFragment.SwipeMovieCallback {

    private final String LOG_TAG = DetailFragment.class.getSimpleName() + "LOG";

    //variables that represent movie object
    private Movie movie;
    private String mTitle;
    private String mGenre;
    private String mOverview;
    private String mReleaseDate;
    private double mRating;
    private int mVoteCount;
    private String mPosterAddress;
    private String mBackDropAddress;
    private boolean favorite;
    private static long mTMDB_ID;
    private int db_id;

    //RecyclerView variables
    private TrailerAdapter mTrailerAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private TrailerInfoLoader loader;
    private View rootView;
    private ProgressBar trailersProgressBar;
    private ProgressBar reviewsProgressBar;
    private ScrollView scrollView;

    private BroadcastReceiver mBroadcastReceiver;
    private IntentFilter mInternetFilter;

    private ActionBar toolbar;
    private Toolbar secondaryToolbar;
    int themeColor;
    private TextView titleView;
    private int toolbarChangePoint = 0;
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangeListener;

    private boolean isTabletPortrait;
    private boolean isTabletTwoPane;

    private final int COLLAPSED_REVIEW_SIZE = 150;

    private final int TRAILER_LOADER_ID = 1;
    private final int REVIEW_LOADER_ID = 2;

    public static final String MOVIE_DETAIL = "movie_detail";
    private final String TOOLBAR_CHANGE_POINT = "toolbar_change_point";
    private final String THEME_COLOR = "theme_color";


    //FragmentCallback for when Show more reviews button is clicked.
    public interface ReviewFragmentCallback {
        public void onMoreReviewsSelected(ArrayList<Review> reviewList, String title, int themeColor);
    }

    //FragmentCallback for when Settings menu item is clicked
    public interface SettingsFragmentCallback{
        public void onSettingsMenuItemSelected(boolean selected);
    }

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.v(LOG_TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        isTabletTwoPane = getResources().getBoolean(R.bool.isTabletTwoPane);
        isTabletPortrait = getResources().getBoolean(R.bool.isTabletPortrait);
        themeColor = getResources().getColor(R.color.colorPrimary);

        //get Movie Object from the intent or bundle
        Bundle arguments = getArguments();
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MOVIE_DETAIL) || arguments != null) {

            if (intent != null && intent.hasExtra(MOVIE_DETAIL)) {
                movie = (Movie) intent.getSerializableExtra(MOVIE_DETAIL);
            } else if (arguments != null) {
                movie = (Movie) arguments.getSerializable(MOVIE_DETAIL);
            }

            mTMDB_ID = movie.getMdb_id();
            favorite = movieIsInFavorite(mTMDB_ID);

            if(savedInstanceState != null){
                toolbarChangePoint = savedInstanceState.getInt(TOOLBAR_CHANGE_POINT);
                themeColor = savedInstanceState.getInt(THEME_COLOR);
            }
            if(isTabletTwoPane) secondaryToolbar = ((MainActivity) getActivity()).getSecondaryToolbar();
            else toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();

            mPosterAddress = movie.getPosterAddress();
            mBackDropAddress = movie.getBackdropAddress();
            if(mBackDropAddress.equals("null") || mBackDropAddress.equals("") || mBackDropAddress == null) {
                mBackDropAddress = mPosterAddress;
            }
            ImageView header = (ImageView) rootView.findViewById(R.id.header);
            titleView = (TextView) rootView.findViewById(R.id.title);
            String headerImageAddress = Keys.HEADER_POSTER_BASE_URL + mBackDropAddress;
            Picasso.with(getContext()).load(headerImageAddress).into(header);
            if(themeColor == getResources().getColor(R.color.colorPrimary)) {
                setColorTheme(headerImageAddress);
            } else setColorForTitleView(themeColor);

            ImageView small_poster = (ImageView) rootView.findViewById(R.id.small_poster);
            String smallImageAddress = Keys.SMALL_POSTER_BASE_URL + mPosterAddress;
            Picasso.with(getContext()).load(smallImageAddress).into(small_poster);

            setDetailFragmentTextFields();

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.trailers_recycler_view);
            mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);

            trailersProgressBar = (ProgressBar) rootView.findViewById(R.id.trailer_progress_bar);
            reviewsProgressBar = (ProgressBar) rootView.findViewById(R.id.review_progress_bar);

            scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
            mOnScrollChangeListener = new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    if(toolbarChangePoint == 0) {
                        toolbarChangePoint = getViewBottomCoordinates(titleView);
                    }
                    else measureToolbarPosition();
                }
            };
            scrollView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangeListener);

            getLoaderManager().initLoader(REVIEW_LOADER_ID, null, reviewResultLoaderListener);
            getLoaderManager().initLoader(TRAILER_LOADER_ID, null, trailerResultLoaderListener);
            Log.v(LOG_TAG, "onCreateView");
            installConnectionListener();
        } else {
            rootView.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBroadcastReceiver != null && mInternetFilter != null) {
            getActivity().registerReceiver(mBroadcastReceiver, mInternetFilter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TOOLBAR_CHANGE_POINT, toolbarChangePoint);
        outState.putInt(THEME_COLOR, themeColor);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(isTabletTwoPane && movie != null){
            secondaryToolbar.getMenu().clear();
            secondaryToolbar.inflateMenu(R.menu.menu_detail_fragment);
            secondaryToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onOptionsItemSelected(item);
                }
            });
        } else if (movie != null){
            inflater.inflate(R.menu.menu_detail_fragment, menu);
        }
        Log.v(LOG_TAG, "onCreateOptionsMenu");
    }

    //change the icon of the favorite button depending on if the Movie object is favorite or not
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (movie != null) {
            if(isTabletTwoPane){
                menu = secondaryToolbar.getMenu();
            }
            MenuItem removeFromFavorite = menu.findItem(R.id.action_remove_from_favorite);
            MenuItem addToFavorite = menu.findItem(R.id.action_add_to_favorite);

            if (favorite) {
                addToFavorite.setVisible(false);
                removeFromFavorite.setVisible(true);
            } else {
                addToFavorite.setVisible(true);
                removeFromFavorite.setVisible(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_to_favorite:
                boolean inserted = insertMovie();
                if (inserted) {
                    getActivity().invalidateOptionsMenu();
                }
                break;
            case R.id.action_remove_from_favorite:
                Uri uri = FavoriteMovieEntry.CONTENT_URI.buildUpon()
                        .appendPath(FavoriteMovieEntry.PATH_FAVORITE_MOVIES_TMDB_ID).appendPath(Long.toString(mTMDB_ID)).build();
                int removedNum = getContext().getContentResolver().delete(uri, null, null);
                if (removedNum > 0) {
                    favorite = false;
                    getActivity().invalidateOptionsMenu();
                    Toast.makeText(getContext(), "Movie was removed from Favorite", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            case R.id.action_share:
                shareMovie();
                break;
            case R.id.action_settings:
                ((SettingsFragmentCallback)getActivity()).onSettingsMenuItemSelected(true);
                return true;
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSwipeMovie(long tmdb_id){
        if(tmdb_id == mTMDB_ID){
            favorite = false;
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toolbar toolbar;
        if (isTabletTwoPane) {
            toolbar = ((MainActivity) getActivity()).getSecondaryToolbar();
            if (toolbar != null) {
                toolbar.getMenu().clear();
                toolbar.setBackground(getResources().getDrawable(R.drawable.background_toolbar_translucent));
                toolbar.setTitle("");
            }
        }
        if(scrollView != null && scrollView.getViewTreeObserver().isAlive()) {
            scrollView.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangeListener);
        }
    }

    private void setDetailFragmentTextFields(){
        mTitle = movie.getTitle();
        TextView title = (TextView) rootView.findViewById(R.id.title);
        title.setText(mTitle);

        mRating = movie.getRating();
        TextView rating = (TextView) rootView.findViewById(R.id.rating);
        rating.setText(mRating + " out of 10");

        mVoteCount = movie.getVoteCount();
        TextView voteCount = (TextView) rootView.findViewById(R.id.vote_count);
        voteCount.setText(mVoteCount + " votes");

        mReleaseDate = movie.getReleaseDate();
        TextView release = (TextView) rootView.findViewById(R.id.release_date);
        release.setText(mReleaseDate);

        mGenre = movie.getGenre();
        TextView genre = (TextView) rootView.findViewById(R.id.genre);
        genre.setText(mGenre);

        mOverview = movie.getOverview();
        TextView overview = (TextView) rootView.findViewById(R.id.overview);
        overview.setText(mOverview);

    }

    private void setColorTheme(String headerImageAddress){
        Picasso.with(getContext()).load(headerImageAddress).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Palette.from(bitmap)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                themeColor = palette.getVibrantColor(getResources().getColor(R.color.colorPrimary));
                                setColorForTitleView(themeColor);
                            }
                        });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    private void setColorForTitleView(int themeColor){
        LinearLayout base = (LinearLayout) rootView.findViewById(R.id.small_poster_base);
        base.setBackgroundColor(themeColor);
        titleView.setBackgroundColor(themeColor);
    }

    private int getViewBottomCoordinates(View view){
        if(view != null) {
            int[] coords = {0, 0};
            view.getLocationOnScreen(coords);
            int absoluteBottom = coords[1] + view.getHeight();
            if(toolbar != null){
                return absoluteBottom - toolbar.getHeight();
            } else if(secondaryToolbar != null){
                return absoluteBottom - secondaryToolbar.getHeight();
            }
        }
        return 0;
    }

    private void measureToolbarPosition(){
        if(scrollView != null) {
            int scrollViewPosition = scrollView.getScrollY();
            if (toolbarChangePoint != 0 && toolbarChangePoint <= scrollViewPosition) {
                setToolbarAppearance(themeColor);
            } else if (toolbarChangePoint == 0 || toolbarChangePoint > scrollViewPosition) {
                setToolbarAppearance(-1);
            }
        }
    }

    private void setToolbarAppearance(int themeColor){
        if(isTabletTwoPane) {
            if(themeColor == -1) {
                secondaryToolbar.setBackground(this.getResources().getDrawable(R.drawable.background_toolbar_translucent));
                secondaryToolbar.setTitle("");
            } else {
                secondaryToolbar.setBackgroundColor(themeColor);
                secondaryToolbar.setTitle(mTitle);
            }
        } else {
            if(themeColor == -1) {
                toolbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_toolbar_translucent));
                toolbar.setDisplayShowTitleEnabled(false);
            } else {
                toolbar.setBackgroundDrawable(new ColorDrawable(themeColor));
                toolbar.setDisplayShowTitleEnabled(true);
                toolbar.setTitle(mTitle);
            }
        }
    }

    private void shareMovie() {
        String mimeType = "text/plain";
        String title = "Movie from Cinema";
        String textToShare = movie.getTitle() + "\n\n" + "Rating: " + movie.getRating() + "\n\n" +
                "Release date: " + movie.getReleaseDate() + "\n\n" + "Overivew: " + "\n" + movie.getOverview();
        String subject = "Cinema: " + movie.getTitle();
        ShareCompat.IntentBuilder
                .from(getActivity())
                .setType(mimeType)
                .setSubject(subject)
                .setChooserTitle(title)
                .setText(textToShare)
                .startChooser();
    }

    //insert Movie object to favorite_movies table
    public boolean insertMovie() {
        ContentValues values = new ContentValues();
        values.put(FavoriteMovieEntry.COLUMN_NAME_TITLE, mTitle);
        values.put(FavoriteMovieEntry.COLUMN_NAME_OVERVIEW, mOverview);
        values.put(FavoriteMovieEntry.COLUMN_NAME_RATING, mRating);
        values.put(FavoriteMovieEntry.COLUMN_NAME_RELEASE, mReleaseDate);
        values.put(FavoriteMovieEntry.COLUMN_NAME_IMAGE_ADDRESS, mPosterAddress);
        values.put(FavoriteMovieEntry.COLUMN_NAME_MDB_ID, mTMDB_ID);

        Uri uri = getContext().getContentResolver().insert(FavoriteMovieEntry.CONTENT_URI, values);
        if (uri != null) {
            favorite = true;
            //set db_id from the uri, in case if user wants to remove item right away
            db_id = Integer.parseInt(uri.getPathSegments().get(1));
            Toast.makeText(getContext(), "Movie is now in Favorite",
                    Toast.LENGTH_LONG)
                    .show();
            return true;
        } else {
            Toast.makeText(getContext(), "Movie was not saved", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    //uses mdb_id to check if the Movie object downloaded from remote database is already saved to favorite_movies table
    private boolean movieIsInFavorite(long tmdb_id) {
        Uri uri = FavoriteMovieEntry.CONTENT_URI.buildUpon()
                .appendPath(FavoriteMovieEntry.PATH_FAVORITE_MOVIES_TMDB_ID).appendPath(Long.toString(tmdb_id)).build();
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount() == 0) {
            return false;
        }
        cursor.moveToFirst();
        db_id = cursor.getInt(cursor.getColumnIndex(FavoriteMovieEntry._ID));
        return true;
    }

    //anonymous class that implements LoaderCallbacks and loads trailers
    private LoaderManager.LoaderCallbacks<ArrayList<YouTubeTrailer>> trailerResultLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<YouTubeTrailer>>() {

        @Override
        public Loader<ArrayList<YouTubeTrailer>> onCreateLoader(int id, Bundle args) {
            if (id == TRAILER_LOADER_ID) {
                return new TrailerInfoLoader(getContext());
            } else return null;
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<YouTubeTrailer>> loader, ArrayList<YouTubeTrailer> trailerData) {
            ImageView emptyTrailers = (ImageView) rootView.findViewById(R.id.no_trailers_found_icon);
            if (trailerData == null) {
                mRecyclerView.setVisibility(View.GONE);
                trailersProgressBar.setVisibility(View.VISIBLE);
                emptyTrailers.setVisibility(View.GONE);
            } else if (trailerData.size() > 0) {
                mTrailerAdapter = new TrailerAdapter(trailerData, new TrailerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String keyStr) {
                        startActivity(YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                                BuildConfig.YOUTUBE_API_KEY, keyStr, 0, true, true));
                    }
                });
                mTrailerAdapter.setProgressBar(trailersProgressBar);
                mRecyclerView.setAdapter(mTrailerAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
                trailersProgressBar.setVisibility(View.GONE);
                emptyTrailers.setVisibility(View.GONE);
            } else if (trailerData.size() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                trailersProgressBar.setVisibility(View.GONE);
                emptyTrailers.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<YouTubeTrailer>> loader) {
            Log.v(LOG_TAG, "Loader: onLoaderReset " + TRAILER_LOADER_ID);
        }
    };

    //anonymous class that implements LoaderCallbacks and loads reviews
    private LoaderManager.LoaderCallbacks<ArrayList<Review>> reviewResultLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<Review>>() {

        @Override
        public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG, "Loader: onCreateLoader");
            if (id == REVIEW_LOADER_ID) {
                return new ReviewLoader(getContext());
            } else return null;
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Review>> loader, final ArrayList<Review> reviewData) {
            Log.v(LOG_TAG, "Loader: onLoadFinished");
            setReviewLayout(reviewData);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Review>> loader) {
            Log.v(LOG_TAG, "Loader: onLoaderReset " + REVIEW_LOADER_ID);
        }
    };

    private void installConnectionListener() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();
                    NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");
                    NetworkInfo.State state = info.getState();
                    if (state == NetworkInfo.State.CONNECTED && trailersProgressBar.getVisibility() == View.VISIBLE) {
                        getLoaderManager().restartLoader(TRAILER_LOADER_ID, null, trailerResultLoaderListener);
                    }
                    if (state == NetworkInfo.State.CONNECTED && reviewsProgressBar.getVisibility() == View.VISIBLE) {
                        getLoaderManager().restartLoader(REVIEW_LOADER_ID, null, reviewResultLoaderListener);
                    }
                }
            };
            mInternetFilter = new IntentFilter();
            mInternetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        }
    }

    //helper method that sets review layout
    private void setReviewLayout(final ArrayList<Review> reviewData) {
        final String contentStr;
        RelativeLayout layoutReviews = (RelativeLayout) rootView.findViewById(R.id.layout_reviews);

        TextView author = (TextView) rootView.findViewById(R.id.reviewer_name);
        final TextView reviewContent = (TextView) rootView.findViewById(R.id.review_text);
        Button showMoreReviewsButton = (Button) rootView.findViewById(R.id.review_button);
        final ImageView expandReview = (ImageView) rootView.findViewById(R.id.expand_review);
        final ImageView collapseReview = (ImageView) rootView.findViewById(R.id.hide_review);
        ImageView emptyReviews = (ImageView) rootView.findViewById(R.id.no_reviews_found_icon);

        if (reviewData == null) {
            layoutReviews.setVisibility(View.GONE);
            showMoreReviewsButton.setVisibility(View.GONE);
            emptyReviews.setVisibility(View.GONE);
            reviewsProgressBar.setVisibility(View.VISIBLE);
        } else if (reviewData.size() > 0) {
            contentStr = reviewData.get(0).getReviewContent();
            author.setText(reviewData.get(0).getAuthor());

            layoutReviews.setVisibility(View.VISIBLE);
            emptyReviews.setVisibility(View.GONE);
            reviewsProgressBar.setVisibility(View.GONE);

            if (contentStr.length() > COLLAPSED_REVIEW_SIZE) {
                reviewContent.setText(contentStr.substring(0, COLLAPSED_REVIEW_SIZE) + "...");
                expandReview.setVisibility(View.VISIBLE);
                collapseReview.setVisibility(View.GONE);
                layoutReviews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (expandReview.getVisibility() == View.VISIBLE && collapseReview.getVisibility() == View.GONE) {
                            reviewContent.setText(contentStr);
                            expandReview.setVisibility(View.GONE);
                            collapseReview.setVisibility(View.VISIBLE);
                        } else if (expandReview.getVisibility() == View.GONE && collapseReview.getVisibility() == View.VISIBLE) {
                            reviewContent.setText(contentStr.substring(0, COLLAPSED_REVIEW_SIZE) + "...");
                            expandReview.setVisibility(View.VISIBLE);
                            collapseReview.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                reviewContent.setText(contentStr);
                expandReview.setVisibility(View.GONE);
                collapseReview.setVisibility(View.GONE);
            }
            if (reviewData.size() == 1) {
                showMoreReviewsButton.setVisibility(View.GONE);
            } else {
                showMoreReviewsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ReviewFragmentCallback) getActivity()).onMoreReviewsSelected(reviewData, mTitle, themeColor);
                    }
                });
            }
        } else if (reviewData.size() == 0) {
            layoutReviews.setVisibility(View.GONE);
            showMoreReviewsButton.setVisibility(View.GONE);
            emptyReviews.setVisibility(View.VISIBLE);
            reviewsProgressBar.setVisibility(View.GONE);
        }
    }

    public static long getmTMDB_ID() {
        return mTMDB_ID;
    }
}
