package com.kelsos.mbrc.features.library.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.Group
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.FragmentBrowseBinding
import com.kelsos.mbrc.features.library.presentation.screens.LibraryScreen

class LibraryViewHolder(
  binding: FragmentBrowseBinding
) : RecyclerView.ViewHolder(binding.root) {
  private val recycler: RecyclerView = binding.libraryBrowserContent
  private val emptyView: Group = binding.libraryBrowserEmptyGroup
  private val emptyViewTitle: TextView = binding.libraryBrowserTextTitle
  private val progressBar: ProgressBar = binding.libraryBrowserProgressBar

  fun bind(libraryScreen: LibraryScreen) {
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
    recycler.setHasFixedSize(true)
    recycler.layoutManager = LinearLayoutManager(
      recycler.context,
      LinearLayoutManager.VERTICAL,
      false
    )
  }

  companion object {
    fun from(
      parent: ViewGroup
    ): LibraryViewHolder {
      val inflater: LayoutInflater = LayoutInflater.from(parent.context)
      val binding: FragmentBrowseBinding = DataBindingUtil.inflate(
        inflater,
        R.layout.fragment_browse,
        parent,
        false
      )
      return LibraryViewHolder(binding)
    }
  }
}
