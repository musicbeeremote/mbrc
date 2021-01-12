package com.kelsos.mbrc.features.library.presentation

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.popup

class TrackAdapter : LibraryAdapter<Track, TrackViewHolder>(DIFF_CALLBACK) {
  private var coverMode: Boolean = false

  fun setCoverMode(coverMode: Boolean) {
    this.coverMode = coverMode
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
    val holder = TrackViewHolder.create(parent)
    holder.setCoverMode(coverMode)
    holder.onIndicatorClick { view, position ->
      view.popup(R.menu.popup_track) { id ->
        val track = getItem(position) ?: return@popup
        requireListener().onMenuItemSelected(id, track)
      }
    }

    holder.onPress { position ->
      val track = getItem(position) ?: return@onPress
      requireListener().onItemClicked(track)
    }
    return holder
  }

  override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
    val track = getItem(position)
    if (track != null) {
      holder.bindTo(track)
    } else {
      holder.clear()
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Track>() {
      override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
      }
    }
  }
}
