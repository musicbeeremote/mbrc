package com.kelsos.mbrc.features.library.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.Group
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.databinding.FragmentBrowseBinding
import com.kelsos.mbrc.features.library.presentation.screens.LibraryScreen

class LibraryViewHolder(binding: FragmentBrowseBinding) : RecyclerView.ViewHolder(binding.root) {
  private val content: RecyclerView = binding.libraryBrowserContent
  private val emptyGroup: Group = binding.libraryBrowserEmptyGroup
  private val emptyTitle: TextView = binding.libraryBrowserTextTitle
  private val progressBar: ProgressBar = binding.libraryBrowserProgressBar

  fun bind(libraryScreen: LibraryScreen) {
    libraryScreen.bind(this)
  }

  fun refreshingComplete(empty: Boolean) {
    emptyGroup.isGone = !empty
    progressBar.isGone = true
  }

  fun setup(
    @StringRes empty: Int,
    adapter: RecyclerView.Adapter<*>
  ) {
    emptyTitle.setText(empty)
    content.adapter = adapter
    content.setHasFixedSize(true)
    content.layoutManager = LinearLayoutManager(
      content.context,
      LinearLayoutManager.VERTICAL,
      false
    )
  }

  companion object {
    fun create(
      parent: ViewGroup
    ): LibraryViewHolder {
      val inflater = LayoutInflater.from(parent.context)
      val binding = FragmentBrowseBinding.inflate(inflater, parent, false)
      return LibraryViewHolder(binding)
    }
  }
}
