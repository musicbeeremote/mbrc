package com.kelsos.mbrc.features.library.presentation

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.features.library.presentation.screens.LibraryScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LibraryPagerAdapter(
  private val viewLifecycleOwner: LifecycleOwner,
) : RecyclerView.Adapter<LibraryViewHolder>() {
  private val screens: MutableList<LibraryScreen> = mutableListOf()
  private val job: Job = Job()
  private val scope: CoroutineScope = CoroutineScope(job + Dispatchers.Main)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
    return LibraryViewHolder.from(parent)
  }

  fun submit(screens: List<LibraryScreen>) {
    this.screens.clear()
    this.screens.addAll(screens)
  }
  override fun getItemCount(): Int = 4

  override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
    val screen = screens[position]
    holder.bind(screen)
    scope.launch {
      delay(400)
      screen.observe(viewLifecycleOwner)
    }
  }
}