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
import com.kelsos.mbrc.features.library.presentation.screens.LibraryScreen
import kotterknife.bindView

class LibraryViewHolder(
  val itemView: View
) : RecyclerView.ViewHolder(itemView) {
  private val recycler: RecyclerView by bindView(R.id.library_browser__content)
  private val emptyView: Group by bindView(R.id.library_browser__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.library_browser__text_title)
  private val progressBar: ProgressBar by bindView(R.id.library_browser__progress_bar)

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
      val view = inflater.inflate(R.layout.fragment_browse, parent, false)
      return LibraryViewHolder(view)
    }
  }
}