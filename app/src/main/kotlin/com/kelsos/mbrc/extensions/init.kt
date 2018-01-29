package com.kelsos.mbrc.extensions

import android.annotation.SuppressLint
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.navigation.library.OnFastScrollListener
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller

fun RecyclerView.linear(
  adapter: RecyclerView.Adapter<*>,
  fastScroller: RecyclerViewFastScroller? = null,
  fastScrollListener: OnFastScrollListener? = null
) {
  this.adapter = adapter

  val layoutManager: LinearLayoutManager
  if (fastScroller == null) {
    layoutManager = LinearLayoutManager(context)
  } else {
    layoutManager = object : LinearLayoutManager(context, VERTICAL, false) {
      override fun onLayoutChildren(
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State
      ) {
        super.onLayoutChildren(recycler, state)

        //TODO if the items are filtered, considered hiding the fast scroller here

        val firstVisibleItemPosition = findFirstVisibleItemPosition()
        if (firstVisibleItemPosition != 0) {
          // this avoids trying to handle un-needed calls
          if (firstVisibleItemPosition == -1)
          //not initialized, or no items shown, so hide fast-scroller
            fastScroller.gone()
          return
        }
        val lastVisibleItemPosition = findLastVisibleItemPosition()
        val itemsShown = lastVisibleItemPosition - firstVisibleItemPosition + 1
        //if all items are shown, hide the fast-scroller
        if (adapter.itemCount > itemsShown) {
          fastScroller.show()
        } else {
          fastScroller.gone()
        }
      }
    }
    fastScroller.setRecyclerView(this)
    fastScroller.setViewsToUse(
      R.layout.recycler_view_fast_scroller_scroller,
      R.id.fastscroller_bubble,
      R.id.fastscroller_handle
    )
    fastScroller.setOnScrollStateChangeListener(object :
      RecyclerViewFastScroller.ScrollStateChangeListener {
      @SuppressLint("SwitchIntDef")
      override fun scrollStateChanged(state: Int) {
        when (state) {
          RecyclerViewFastScroller.SCROLL_ENDED -> fastScrollListener?.onComplete(
            layoutManager.findFirstVisibleItemPosition(),
            layoutManager.findLastVisibleItemPosition()
          )
          RecyclerViewFastScroller.SCROLL_STARTED -> fastScrollListener?.onStart()
          else -> throw IllegalArgumentException("Invalid state")
        }
      }

    })
  }

  this.layoutManager = layoutManager

}
