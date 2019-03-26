package com.kelsos.mbrc.ui.navigation.library

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

class LibraryPagerAdapter(
  private val viewLifecycleOwner: LifecycleOwner,
  private val fastScrollingListener: FastScrollingListener
) : RecyclerView.Adapter<LibraryViewHolder>() {
  private var visiblePosition = 0
  private val screens: MutableList<LibraryScreen> = mutableListOf()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
    return LibraryViewHolder.create(parent, fastScrollingListener)
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