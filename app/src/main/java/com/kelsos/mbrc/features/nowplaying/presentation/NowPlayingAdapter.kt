package com.kelsos.mbrc.features.nowplaying.presentation

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.common.ui.helpers.VisibleRangeGetter
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.features.nowplaying.dragsort.ItemTouchHelperAdapter
import com.kelsos.mbrc.features.nowplaying.dragsort.OnStartDragListener

class NowPlayingAdapter(
  private val dragStartListener: OnStartDragListener,
  private val nowPlayingListener: NowPlayingListener,
  private val visibleRangeGetter: VisibleRangeGetter
) :
  PagedListAdapter<NowPlaying, NowPlayingTrackViewHolder>(
    DIFF_CALLBACK
  ),
  ItemTouchHelperAdapter {

  private var currentTrack = ""
  private var playingTrackIndex = -1

  fun getPlayingTrackIndex(): Int = this.playingTrackIndex

  fun setPlayingTrack(path: String) {
    this.currentTrack = path
    val range = visibleRangeGetter.visibleRange()
    notifyItemRangeChanged(
      range.firstItem,
      range.itemCount,
      PLAYING_CHANGED
    )
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NowPlayingTrackViewHolder {
    return NowPlayingTrackViewHolder.create(
      parent,
      { position ->
        nowPlayingListener.onPress(position)
        playingTrackIndex = position
        currentTrack = getItem(position)?.path ?: ""
      }
    ) { start, holder -> dragStartListener.onStartDrag(start, holder) }
  }

  override fun onBindViewHolder(
    holder: NowPlayingTrackViewHolder,
    position: Int,
    payloads: MutableList<Any>
  ) {
    if (payloads.contains(PLAYING_CHANGED)) {
      val track = getItem(holder.adapterPosition) ?: return
      val isPlayingTrack = track.path == currentTrack
      holder.setPlayingTrack(isPlayingTrack)
      if (isPlayingTrack) {
        playingTrackIndex = holder.adapterPosition
      }
    } else {
      onBindViewHolder(holder, position)
    }
  }

  override fun onBindViewHolder(holder: NowPlayingTrackViewHolder, position: Int) {
    val track = getItem(holder.adapterPosition) ?: return
    val isPlayingTrack = track.path == currentTrack
    holder.bindTo(track)
    holder.setPlayingTrack(isPlayingTrack)

    if (isPlayingTrack) {
      playingTrackIndex = holder.adapterPosition
    }
  }

  override fun onItemMove(from: Int, to: Int): Boolean {
    notifyItemMoved(from, to)
    nowPlayingListener.onMove(from, to)

    if (!currentTrack.isBlank()) {
      setPlayingTrack(currentTrack)
    }

    return true
  }

  override fun onItemDismiss(position: Int) {
    nowPlayingListener.onDismiss(position)
  }

  interface NowPlayingListener {
    fun onPress(position: Int)
    fun onMove(from: Int, to: Int)
    fun onDismiss(position: Int)
  }

  companion object {
    const val PLAYING_CHANGED = "playing_changed"
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NowPlaying>() {
      override fun areItemsTheSame(oldItem: NowPlaying, newItem: NowPlaying): Boolean {
        return oldItem.path == newItem.path
      }

      override fun areContentsTheSame(
        oldItem: NowPlaying,
        newItem: NowPlaying
      ): Boolean {
        return oldItem == newItem
      }
    }
  }
}