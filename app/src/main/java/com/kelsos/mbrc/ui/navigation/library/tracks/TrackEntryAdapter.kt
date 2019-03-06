package com.kelsos.mbrc.ui.navigation.library.tracks

import androidx.recyclerview.widget.DiffUtil
import android.view.View
import android.view.ViewGroup
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.ui.FastScrollableAdapter
import com.kelsos.mbrc.ui.navigation.library.OnFastScrollListener
import com.kelsos.mbrc.ui.navigation.library.popup
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller.BubbleTextGetter

class TrackEntryAdapter : FastScrollableAdapter<TrackEntity, TrackViewHolder>(DIFF_CALLBACK),
  BubbleTextGetter, OnFastScrollListener {

  private val indicatorPressed: (View, Int) -> Unit = { view, position ->
    view.popup(R.menu.popup_track) {

      val action = when (it) {
        R.id.popup_track_queue_next -> LibraryPopup.NEXT
        R.id.popup_track_queue_last -> LibraryPopup.LAST
        R.id.popup_track_play -> LibraryPopup.NOW
        R.id.popup_track_play_queue_all -> LibraryPopup.ADD_ALL
        else -> throw IllegalArgumentException("invalid menuItem id $it")
      }

      val listener = requireListener()
      getItem(position)?.run {
        listener.onMenuItemSelected(action, this)
      }
    }
  }

  private val pressed: (View, Int) -> Unit = { _, position ->
    val listener = requireListener()
    getItem(position)?.run {
      listener.onItemClicked(this)
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
    return TrackViewHolder.create(parent, indicatorPressed, pressed)
  }

  override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
    if (fastScrolling) {
      holder.clear()
      return
    }

    val trackEntity = getItem(holder.adapterPosition)

    if (trackEntity != null) {
      holder.bindTo(trackEntity)
    } else {
      holder.clear()
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrackEntity>() {
      override fun areItemsTheSame(oldItem: TrackEntity, newItem: TrackEntity): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: TrackEntity, newItem: TrackEntity): Boolean {
        return oldItem == newItem
      }
    }
  }
}