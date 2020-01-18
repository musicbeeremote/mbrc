package com.kelsos.mbrc.features.library.presentation

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.features.library.FastScrollingListener
import com.kelsos.mbrc.features.library.presentation.screens.LibraryScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LibraryPagerAdapter(
  private val viewLifecycleOwner: LifecycleOwner,
  private val fastScrollingListener: FastScrollingListener
) : RecyclerView.Adapter<LibraryViewHolder>() {
  private var visiblePosition = 0
  private val screens: MutableList<LibraryScreen> = mutableListOf()
  private val job: Job = Job()
  private val scope: CoroutineScope = CoroutineScope(job + Dispatchers.Main)

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
    scope.launch {
      delay(400)
      screen.observe(viewLifecycleOwner)
    }
  }

  fun setVisiblePosition(itemPosition: Int) {
    visiblePosition = itemPosition
  }
}