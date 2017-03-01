package com.example.android.moviesapp;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.android.moviesapp.utilities.MDBConnection;

import static com.example.android.moviesapp.MovieGridFragment.setLoadMoreMovies;

/*Based on the interface from https://gist.github.com/nesquena/d09dc68ff07e845cc622 as a part of the
tutorial Endless Scrolling with AdapterViews and RecyclerView
(https://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView)*/

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 1;
    // The current offset index of data you have loaded
    private static int currentPage = 1;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 1;

    private int lastVisibleItemPosition;
    private int totalItemCount;

    private GridLayoutManager mLayoutManager;

    private Context context;

    private boolean connectionMessageCalled;

    private final String LOG_TAG = EndlessRecyclerViewScrollListener.class.getSimpleName() + "LOG";

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager, Context context) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
        this.context = context;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
        totalItemCount = mLayoutManager.getItemCount();

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }
        // If it’s still loading, check to see if the dataset count has
        // changed, if so it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        // If it isn’t currently loading, check if
        // the visibleThreshold is reached and more data need to be reload
        // which is done by onLoadMore.
        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            if(MDBConnection.checkNetworkConnection(context)) {
                currentPage++;
                onLoadMore();
                setLoadMoreMovies(false);
                loading = true;
                connectionMessageCalled = false;
            } else {
                if(!connectionMessageCalled) {
                    setLoadMoreMovies(true);
                    Toast.makeText(context, context.getResources().
                            getText(R.string.empty_movie_gridview), Toast.LENGTH_SHORT).show();
                    connectionMessageCalled = true;
                }
            }
        }

        if(connectionMessageCalled && lastVisibleItemPosition <= (totalItemCount - visibleThreshold)){
            setLoadMoreMovies(false);
            connectionMessageCalled = false;
        }
    }

    // This method is called for all the new searches, for example, when an item
    // from the drawer list is chosen
    public void resetState() {
        this.currentPage = this.startingPageIndex;
        this.previousTotalItemCount = 0;
        this.loading = true;
    }

    public static int setPageIndex(){
        return currentPage;
    }

    public void incrementCurrentPage(){
        currentPage++;
    }

    public abstract void onLoadMore();
}
