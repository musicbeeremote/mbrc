package com.kelsos.mbrc.ui.navigation.library

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.databinding.FragmentBrowseBinding

class LibraryPagerAdapter(
  private val viewLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<LibraryViewHolder>() {
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
    holder.bind(screen)
    screen.observe(viewLifecycleOwner)
  }
}

class LibraryViewHolder(binding: FragmentBrowseBinding) : RecyclerView.ViewHolder(binding.root) {
  private val content: RecyclerView = binding.libraryBrowserContent
  private val emptyGroup: Group = binding.libraryBrowserEmptyGroup
  private val emptyTitle: TextView = binding.libraryBrowserTextTitle

  fun bind(libraryScreen: LibraryScreen) {
    libraryScreen.bind(this)
  }

  fun refreshingComplete(empty: Boolean) {
    emptyGroup.isVisible = empty
  }

  fun setup(
    @StringRes empty: Int,
    adapter: RecyclerView.Adapter<*>
  ) {
    emptyTitle.setText(empty)
    content.adapter = adapter
    content.layoutManager = LinearLayoutManager(content.context)
    content.setHasFixedSize(true)
  }

  companion object {
    fun create(
      parent: ViewGroup
    ): LibraryViewHolder {
      val binding = FragmentBrowseBinding.inflate(LayoutInflater.from(parent.context))
      return LibraryViewHolder(binding)
    }
  }
}
