package com.kelsos.mbrc.ui.navigation.library

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class LibraryPagerAdapter(
  private val viewLifecycleOwner: LifecycleOwner,
  private val fastScrollingListener: FastScrollingListener
) : RecyclerView.Adapter<LibraryViewHolder>() {
  private var visiblePosition = 0
  private val screens: MutableList<LibraryScreen> = mutableListOf()
  private val job: Job = Job()
  private val scope: CoroutineScope = CoroutineScope(job + Dispatchers.Main)
  private var deferred: Deferred<Unit>? = null

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
    deferred?.cancel()
    deferred = scope.async {
      delay(400)
      screen.observe(viewLifecycleOwner)
    }
  }

  fun setVisiblePosition(itemPosition: Int) {
    visiblePosition = itemPosition
  }
}