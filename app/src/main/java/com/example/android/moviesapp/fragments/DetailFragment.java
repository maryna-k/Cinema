package com.example.android.moviesapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
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

import com.example.android.moviesapp.R;
import com.example.android.moviesapp.activities.MainActivity;
import com.example.android.moviesapp.adapters.TrailerAdapter;
import com.example.android.moviesapp.database.DatabaseUtilMethods;
import com.example.android.moviesapp.database.MovieContract;
import com.example.android.moviesapp.loaders.ReviewLoader;
import com.example.android.moviesapp.loaders.TrailerInfoLoader;
import com.example.android.moviesapp.models.Movie;
import com.example.android.moviesapp.models.Review;
import com.example.android.moviesapp.models.YouTubeTrailer;
import com.example.android.moviesapp.utilities.ImageUtils;
import com.example.android.moviesapp.utilities.Keys;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import butterknife.BindBool;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DetailFragment extends Fragment implements FavoriteGridFragment.SwipeMovieCallback {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    //variables from movie object
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

    @BindView(R.id.title) TextView title;
    @BindView(R.id.rating) TextView rating;
    @BindView(R.id.vote_count) TextView voteCount;
    @BindView(R.id.release_date) TextView release;
    @BindView(R.id.genre) TextView genre;
    @BindView(R.id.overview) TextView overview;

    @BindView(R.id.backdrop) ImageView backdropView;
    @BindView(R.id.small_poster_base) LinearLayout smallPosterBase;
    @BindView(R.id.no_trailers_found_icon) ImageView emptyTrailers;

    //RecyclerView variables
    private TrailerAdapter mTrailerAdapter;
    @BindView(R.id.trailers_recycler_view) RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private TrailerInfoLoader loader;
    private View rootView;
    @BindView(R.id.trailer_progress_bar) ProgressBar trailersProgressBar;
    @BindView(R.id.review_progress_bar) ProgressBar reviewsProgressBar;
    @BindView(R.id.scrollView) ScrollView scrollView;

    private ArrayList<Review> reviewList;

    private BroadcastReceiver mBroadcastReceiver;
    private IntentFilter mInternetFilter;

    //toolbar and colors
    private ActionBar toolbar;
    private Toolbar secondaryToolbar;
    int themeColor;
    @BindColor(R.color.colorPrimary) int defaultColor;
    private int toolbarChangePoint = 0;
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangeListener;

    @BindView(R.id.small_poster) ImageView smallPosterView;

    @BindBool(R.bool.isTabletTwoPane) boolean isTabletTwoPane;
    @BindBool(R.bool.isTabletPortrait) boolean isTabletPortrait;

    //review layout
    String contentStr;
    @BindView(R.id.layout_reviews) RelativeLayout layoutReviews;
    @BindView(R.id.reviewer_name) TextView author;
    @BindView(R.id.review_text) TextView reviewContent;
    @BindView(R.id.review_button) Button showMoreReviewsButton;
    @BindView(R.id.expand_review) ImageView expandReview;
    @BindView(R.id.hide_review) ImageView collapseReview;
    @BindView(R.id.no_reviews_found_icon) ImageView emptyReviews;

    private Unbinder unbinder;

    private final int COLLAPSED_REVIEW_SIZE = 150;

    private final int TRAILER_LOADER_ID = 1;
    private final int REVIEW_LOADER_ID = 2;
    private final int CURSOR_REVIEW_LOADER_ID = 3;

    public static final String MOVIE_DETAIL = "movie_detail";
    private final String TOOLBAR_CHANGE_POINT = "toolbar_change_point";
    private final String THEME_COLOR = "theme_color";


    //Fragment сallback for when Show more reviews button is clicked.
    public interface ReviewFragmentCallback {
        public void onMoreReviewsSelected(ArrayList<Review> reviewList, String title, int themeColor);
    }

    //Fragment сallback for when Settings menu item is clicked
    public interface SettingsFragmentCallback{
        public void onSettingsMenuItemSelected(boolean selected);
    }

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        isTabletTwoPane = getResources().getBoolean(R.bool.isTabletTwoPane);
        isTabletPortrait = getResources().getBoolean(R.bool.isTabletPortrait);
        themeColor = defaultColor;

        //get Movie Object from the intent or bundle
        Bundle arguments = getArguments();
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MOVIE_DETAIL) || arguments != null) {

            if (intent != null && intent.hasExtra(MOVIE_DETAIL)) {
                movie = (Movie) intent.getSerializableExtra(MOVIE_DETAIL);
            } else if (arguments != null) {
                movie = (Movie) arguments.getSerializable(MOVIE_DETAIL);
            }

            mTMDB_ID = movie.getTmdb_id();
            favorite = DatabaseUtilMethods.movieIsInFavorite(mTMDB_ID, getContext());

            if(savedInstanceState != null){
                toolbarChangePoint = savedInstanceState.getInt(TOOLBAR_CHANGE_POINT);
                themeColor = savedInstanceState.getInt(THEME_COLOR);
            }
            if(isTabletTwoPane) secondaryToolbar = ((MainActivity) getActivity()).getSecondaryToolbar();
            else toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();

            mPosterAddress = movie.getPosterAddress();
            mBackDropAddress = movie.getBackdropAddress();
            if(mBackDropAddress == null || mBackDropAddress.equals("null") || mBackDropAddress.equals("")) {
                mBackDropAddress = mPosterAddress;
            }
            String backdropImageAddress = Keys.BACKDROP_POSTER_BASE_URL + mBackDropAddress;
            setColorThemeAndBackdrop(backdropImageAddress);

            if(movie.getPosterStoragePath() != null){
                Bitmap bitmap = ImageUtils.getPosterFromStorage(movie.getPosterStoragePath(), Long.toString(mTMDB_ID));
                smallPosterView.setImageBitmap(bitmap);
            } else {
                String fullPosterAddress = Keys.SMALL_POSTER_BASE_URL + mPosterAddress;
                Picasso.with(getContext()).load(fullPosterAddress).into(smallPosterView);
            }

            setDetailFragmentTextFields();

            mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mOnScrollChangeListener = new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    if(toolbarChangePoint == 0) {
                        toolbarChangePoint = getViewBottomCoordinates(title);
                    }
                    else measureToolbarPosition();
                }
            };
            scrollView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangeListener);

            if(!favorite) {
                getLoaderManager().initLoader(REVIEW_LOADER_ID, null, reviewResultLoaderListener);
            } else {
                getLoaderManager().initLoader(CURSOR_REVIEW_LOADER_ID, null, reviewCursorLoaderListener);
            }
            getLoaderManager().initLoader(TRAILER_LOADER_ID, null, trailerResultLoaderListener);
            installConnectionListener();
        } else {
            //if intent or bundle is empty, show the empty view
            //used in two pane tablet layout when the program is first opened and no movie is selected
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
                favorite = DatabaseUtilMethods.saveFavoriteMovie(movie, reviewList, getContext());
                if (favorite) {
                    getActivity().invalidateOptionsMenu();
                }
                break;
            case R.id.action_remove_from_favorite:
                int removedNum = DatabaseUtilMethods.deleteFavoriteMovie(mTMDB_ID, getContext());
                if (removedNum > 0) {
                    favorite = false;
                    getActivity().invalidateOptionsMenu();
                    String str = getContext().getResources().getString(R.string.removed_from_db);
                    Toast.makeText(getContext(), str, Toast.LENGTH_SHORT)
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
        unbinder.unbind();
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
        title.setText(mTitle);

        mRating = movie.getRating();
        rating.setText(mRating + " out of 10");

        mVoteCount = movie.getVoteCount();
        voteCount.setText(mVoteCount + " votes");

        mReleaseDate = movie.getReleaseDate();
        release.setText(mReleaseDate);

        mGenre = movie.getGenre();
        genre.setText(mGenre);

        mOverview = movie.getOverview();
        overview.setText(mOverview);

    }

    private void setColorThemeAndBackdrop(String headerImageAddress){
        Target target = new Target(){
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                backdropView.setImageBitmap(bitmap);
                Palette.from(bitmap)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                themeColor = palette.getVibrantColor(defaultColor);
                                if (themeColor == defaultColor){
                                    themeColor = palette.getDominantColor(defaultColor);
                                }
                                if (themeColor == defaultColor){
                                    themeColor = palette.getMutedColor(defaultColor);
                                }
                                setColorForTitleView(themeColor);
                            }
                        });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.v(LOG_TAG, "Bitmap failed");
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.v(LOG_TAG, "Picasso: onPrepareLoad");
            }
        };
        Picasso.with(getContext()).load(headerImageAddress).into(target);
        backdropView.setTag(target);
    }

    private void setColorForTitleView(int themeColor){
        smallPosterBase.setBackgroundColor(themeColor);
        title.setBackgroundColor(themeColor);
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
        String title = getContext().getResources().getString(R.string.share_movie_message_title);
        String textToShare = movie.getTitle() + "\n\n" +
                getContext().getResources().getString(R.string.rating) + movie.getRating() + "\n\n" +
                getContext().getResources().getString(R.string.release_date) + movie.getReleaseDate() + "\n\n" +
                getContext().getResources().getString(R.string.overview) + "\n" + movie.getOverview();
        String subject = getContext().getResources().getString(R.string.app_name) + ": " + movie.getTitle();
        ShareCompat.IntentBuilder
                .from(getActivity())
                .setType(mimeType)
                .setSubject(subject)
                .setChooserTitle(title)
                .setText(textToShare)
                .startChooser();
    }

    //anonymous class that implements LoaderCallbacks and loads trailers
    private LoaderManager.LoaderCallbacks<ArrayList<YouTubeTrailer>> trailerResultLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<YouTubeTrailer>>() {

        @Override
        public Loader<ArrayList<YouTubeTrailer>> onCreateLoader(int id, Bundle args) {
            if (id == TRAILER_LOADER_ID) {
                return new TrailerInfoLoader(getContext(), mTMDB_ID);
            } else return null;
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<YouTubeTrailer>> loader, ArrayList<YouTubeTrailer> trailerData) {
            if (trailerData == null) {
                mRecyclerView.setVisibility(View.GONE);
                trailersProgressBar.setVisibility(View.VISIBLE);
                emptyTrailers.setVisibility(View.GONE);
            } else if (trailerData.size() > 0) {
                mTrailerAdapter = new TrailerAdapter(trailerData, new TrailerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String keyStr) {
                        startActivity(YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                                Keys.YOUTUBE_API_KEY, keyStr, 0, true, true));
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
        }
    };

    //anonymous class that implements LoaderCallbacks and loads reviews
    private LoaderManager.LoaderCallbacks<ArrayList<Review>> reviewResultLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<Review>>() {

        @Override
        public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {
            if (id == REVIEW_LOADER_ID) {
                return new ReviewLoader(getContext(), mTMDB_ID);
            } else return null;
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Review>> loader, final ArrayList<Review> reviewData) {
            reviewList = reviewData;
            setReviewLayout(reviewData);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Review>> loader) {
        }
    };

    //anonymous class that implements LoaderCallbacks and loads cursor with reviews
    private LoaderManager.LoaderCallbacks<Cursor> reviewCursorLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if(id == CURSOR_REVIEW_LOADER_ID) {
                String selection = MovieContract.ReviewsTableEntry.COLUMN_NAME_TMDB_ID + "=?";
                String[] selectionArgs = new String[]{Long.toString(mTMDB_ID)};
                return new CursorLoader(getActivity(),
                        MovieContract.ReviewsTableEntry.CONTENT_URI,
                        null, selection, selectionArgs, null);
            } else return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
            ArrayList<Review> reviewData = new ArrayList<>();
            if(cursor != null){
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    String rText = cursor.getString
                            (cursor.getColumnIndex(MovieContract.ReviewsTableEntry.COLUMN_NAME_REVIEW_TEXT));
                    String rName = cursor.getString
                            (cursor.getColumnIndex(MovieContract.ReviewsTableEntry.COLUMN_NAME_REVIEWER_NAME));
                    Review review = new Review("", rName, rText);
                    reviewData.add(review);
                }
                reviewList = reviewData;
            }
            setReviewLayout(reviewData);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
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
                    if (state == NetworkInfo.State.CONNECTED && reviewsProgressBar.getVisibility() == View.VISIBLE && !favorite) {
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
        if (reviewData == null) {
            showReviewProgressBar();
        } else if (reviewData.size() > 0) {
            contentStr = reviewData.get(0).getReviewContent();
            author.setText(reviewData.get(0).getAuthor());
            showReviewLayout();

            if (contentStr.length() > COLLAPSED_REVIEW_SIZE) {
                showCollapseReviewLayout();
                layoutReviews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (expandReview.getVisibility() == View.VISIBLE && collapseReview.getVisibility() == View.GONE) {
                            showExpandedReviewLayout();
                        } else if (expandReview.getVisibility() == View.GONE && collapseReview.getVisibility() == View.VISIBLE) {
                            showCollapseReviewLayout();
                        }
                    }
                });
            } else {
                showFullTextReviewLayout();
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
            showEmptyReviewLayout();
        }
    }

    private void showEmptyReviewLayout(){
        layoutReviews.setVisibility(View.GONE);
        showMoreReviewsButton.setVisibility(View.GONE);
        emptyReviews.setVisibility(View.VISIBLE);
        reviewsProgressBar.setVisibility(View.GONE);
    }

    private void showReviewProgressBar(){
        layoutReviews.setVisibility(View.GONE);
        showMoreReviewsButton.setVisibility(View.GONE);
        emptyReviews.setVisibility(View.GONE);
        reviewsProgressBar.setVisibility(View.VISIBLE);
    }

    private void showReviewLayout(){
        layoutReviews.setVisibility(View.VISIBLE);
        emptyReviews.setVisibility(View.GONE);
        reviewsProgressBar.setVisibility(View.GONE);
    }

    private void showCollapseReviewLayout(){
        reviewContent.setText(contentStr.substring(0, COLLAPSED_REVIEW_SIZE) + "...");
        expandReview.setVisibility(View.VISIBLE);
        collapseReview.setVisibility(View.GONE);
    }

    private void showExpandedReviewLayout(){
        reviewContent.setText(contentStr);
        expandReview.setVisibility(View.GONE);
        collapseReview.setVisibility(View.VISIBLE);
    }

    private void showFullTextReviewLayout(){
        reviewContent.setText(contentStr);
        expandReview.setVisibility(View.GONE);
        collapseReview.setVisibility(View.GONE);
    }
}
