package com.example.android.moviesapp;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/*Based on the interface from https://gist.github.com/nesquena/d09dc68ff07e845cc622 as a part of the
tutorial Endless Scrolling with AdapterViews and RecyclerView
(https://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView)*/

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 2;
    // The current offset index of data you have loaded
    private static int currentPage = 1;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 1;

    private GridLayoutManager mLayoutManager;

    private final String LOG_TAG = EndlessRecyclerViewScrollListener.class.getSimpleName();

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition;
        int totalItemCount = mLayoutManager.getItemCount();

        lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();

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
            currentPage++;
            onLoadMore();
            loading = true;
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

    public abstract void onLoadMore();
}
