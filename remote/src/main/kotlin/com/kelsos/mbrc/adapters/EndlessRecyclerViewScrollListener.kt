package com.kelsos.mbrc.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

abstract class EndlessRecyclerViewScrollListener(private val mLinearLayoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
  // The minimum amount of items to have below your current scroll position
  // before loading more.
  private val visibleThreshold = 40
  // The current offset index of data you have loaded
  private var currentPage = 0
  // The total number of items in the dataset after the last load
  private var previousTotalItemCount = 0
  // True if we are still waiting for the last set of data to load.
  private var loading = true
  // Sets the starting page index
  private val startingPageIndex = 0

  // This happens many times a second during a scroll, so be wary of the code you place here.
  // We are given a few useful parameters to help us work out if we need to load some more data,
  // but first we check if we are waiting for the previous load to finish.
  override fun onScrolled(view: RecyclerView?, dx: Int, dy: Int) {
    val firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()
    val visibleItemCount = view!!.childCount
    val totalItemCount = mLinearLayoutManager.itemCount

    // If the total item count is zero and the previous isn't, assume the
    // list is invalidated and should be reset back to initial state
    if (totalItemCount < previousTotalItemCount) {
      this.currentPage = this.startingPageIndex
      this.previousTotalItemCount = totalItemCount
      if (totalItemCount == 0) {
        this.loading = true
      }
    }
    // If it’s still loading, we check to see if the dataset count has
    // changed, if so we conclude it has finished loading and load the current page
    // number and total item count.
    if (loading && totalItemCount > previousTotalItemCount) {
      loading = false
      previousTotalItemCount = totalItemCount
    }

    // If it isn’t currently loading, we check to see if we have breached
    // the visibleThreshold and need to reload more data.
    // If we do need to reload some more data, we getAllPlaylists onLoadMore to fetch the data.
    if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
      currentPage++
      onLoadMore(currentPage, totalItemCount)
      loading = true
    }
  }

  // Defines the process for actually loading more data based on page
  abstract fun onLoadMore(page: Int, totalItemsCount: Int)

}
