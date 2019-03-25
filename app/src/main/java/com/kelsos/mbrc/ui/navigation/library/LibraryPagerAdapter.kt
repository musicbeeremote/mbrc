package com.kelsos.mbrc.ui.navigation.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kelsos.mbrc.R
import com.kelsos.mbrc.extensions.linear
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller
import kotterknife.bindView

class LibraryPagerAdapter(
  private val viewLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<LibraryViewHolder>() {
  private var visiblePosition = 0
  private val screens: MutableList<LibraryScreen> = mutableListOf()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
    return LibraryViewHolder.create(parent)
  }

  fun submit(screens: List<LibraryScreen>) {
    this.screens.clear()
    this.screens.addAll(screens)
  }
  override fun getItemCount(): Int = 4

  override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
    val screen = screens[position]
    holder.bind(screen, visiblePosition == position)
    screen.observe(viewLifecycleOwner)
  }

  fun setVisiblePosition(itemPosition: Int) {
    visiblePosition = itemPosition
  }
}

class LibraryViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
  private val recycler: RecyclerView by bindView(R.id.library_browser__content)
  private val swipeLayout: SwipeRefreshLayout by bindView(R.id.library_browser__refresh_layout)
  private val fastScroller: RecyclerViewFastScroller by bindView(R.id.fastscroller)

  private val emptyView: Group by bindView(R.id.library_browser__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.library_browser__text_title)

  fun bind(libraryScreen: LibraryScreen, visible: Boolean) {
    fastScroller.isVisible = visible
    libraryScreen.bind(this)
  }

  fun refreshing() {
    if (!swipeLayout.isRefreshing) {
      swipeLayout.isRefreshing = true
    }
  }

  fun refreshingComplete(empty: Boolean) {
    emptyView.isVisible = empty
    swipeLayout.isRefreshing = false
  }

  fun setup(
    @StringRes empty: Int,
    screen: SwipeRefreshLayout.OnRefreshListener,
    adapter: RecyclerView.Adapter<*>
  ) {
    swipeLayout.setOnRefreshListener(screen)
    emptyViewTitle.setText(empty)
    recycler.linear(adapter, fastScroller)
    recycler.setHasFixedSize(true)
  }

  companion object {
    fun create(
      parent: ViewGroup
    ): LibraryViewHolder {
      val inflater: LayoutInflater = LayoutInflater.from(parent.context)
      val view = inflater.inflate(R.layout.fragment_browse, parent, false)
      return LibraryViewHolder(view)
    }
  }
}