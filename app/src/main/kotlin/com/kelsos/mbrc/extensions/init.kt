package com.kelsos.mbrc.extensions

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller

fun RecyclerView.initLinear(adapter: RecyclerView.Adapter<*>, fastScroller: RecyclerViewFastScroller? = null) {
  this.adapter = adapter

  if (fastScroller == null) {
    this.layoutManager = LinearLayoutManager(this.context)
  } else {
    this.layoutManager = object : LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false) {
      override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        //TODO if the items are filtered, considered hiding the fast scroller here
        val firstVisibleItemPosition = findFirstVisibleItemPosition()
        if (firstVisibleItemPosition != 0) {
          // this avoids trying to handle un-needed calls
          if (firstVisibleItemPosition == -1)
          //not initialized, or no items shown, so hide fast-scroller
            fastScroller.visibility = View.GONE
          return
        }
        val lastVisibleItemPosition = findLastVisibleItemPosition()
        val itemsShown = lastVisibleItemPosition - firstVisibleItemPosition + 1
        //if all items are shown, hide the fast-scroller
        fastScroller.visibility = if (adapter.itemCount > itemsShown) View.VISIBLE else View.GONE
      }
    }
    fastScroller.setRecyclerView(this)
    fastScroller.setViewsToUse(R.layout.recycler_view_fast_scroller_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle)
  }

}
