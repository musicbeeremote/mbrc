package com.kelsos.mbrc.features.library.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.Group
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.library.FastScrolling.STARTED
import com.kelsos.mbrc.features.library.FastScrolling.STOPPED
import com.kelsos.mbrc.features.library.FastScrollingListener
import com.kelsos.mbrc.features.library.OnFastScrollListener
import com.kelsos.mbrc.features.library.presentation.screens.LibraryScreen
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller
import kotterknife.bindView

class LibraryViewHolder(
  val itemView: View,
  private val fastScrollingListener: FastScrollingListener
) : RecyclerView.ViewHolder(itemView) {
  private val recycler: RecyclerView by bindView(R.id.library_browser__content)
  private val scroller: RecyclerViewFastScroller by bindView(R.id.library_browser__fast_scroller)

  private val emptyView: Group by bindView(R.id.library_browser__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.library_browser__text_title)
  private val progressBar: ProgressBar by bindView(R.id.library_browser__progress_bar)

  fun bind(libraryScreen: LibraryScreen, visible: Boolean) {
    scroller.isVisible = visible
    libraryScreen.bind(this)
  }

  fun refreshingComplete(empty: Boolean) {
    emptyView.isVisible = empty
    progressBar.isGone = true
  }

  fun setup(
    @StringRes empty: Int,
    adapter: RecyclerView.Adapter<*>
  ) {
    emptyViewTitle.setText(empty)
    recycler.adapter = adapter

    val fastScrollListener = adapter as? OnFastScrollListener
    recycler.setHasFixedSize(true)
    val layoutManager: LinearLayoutManager

    layoutManager = object : LinearLayoutManager(recycler.context, VERTICAL, false) {
      override fun onLayoutChildren(
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State
      ) {
        super.onLayoutChildren(recycler, state)

        val firstVisibleItemPosition = findFirstVisibleItemPosition()
        if (firstVisibleItemPosition != 0) {
          if (firstVisibleItemPosition == -1) {
            scroller.isVisible = false
          }
          return
        }
        val lastVisibleItemPosition = findLastVisibleItemPosition()
        val itemsShown = lastVisibleItemPosition - firstVisibleItemPosition + 1

        scroller.isVisible = adapter.itemCount > itemsShown
      }
    }
    recycler.layoutManager = layoutManager

    scroller.setRecyclerView(recycler)
    scroller.setViewsToUse(
      R.layout.recycler_view_fast_scroller_scroller,
      R.id.fastscroller_bubble,
      R.id.fastscroller_handle
    )

    val scrollStateChangeListener = object : RecyclerViewFastScroller.ScrollStateChangeListener {
      override fun scrollStateChanged(@RecyclerViewFastScroller.ScrollState state: Int) {
        when (state) {
          RecyclerViewFastScroller.SCROLL_ENDED -> scrollEnded()
          RecyclerViewFastScroller.SCROLL_STARTED -> scrollStarted()
        }
      }

      private fun scrollStarted() {
        fastScrollingListener.onFastScrolling(STARTED)
        fastScrollListener?.onStart()
      }

      private fun scrollEnded() {
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        fastScrollListener?.onComplete(firstVisibleItemPosition, lastVisibleItemPosition)
        fastScrollingListener.onFastScrolling(STOPPED)
      }
    }
    scroller.setOnScrollStateChangeListener(scrollStateChangeListener)
  }

  companion object {
    fun create(
      parent: ViewGroup,
      fastScrollingListener: FastScrollingListener
    ): LibraryViewHolder {
      val inflater: LayoutInflater = LayoutInflater.from(parent.context)
      val view = inflater.inflate(R.layout.fragment_browse, parent, false)
      return LibraryViewHolder(
        view,
        fastScrollingListener
      )
    }
  }
}