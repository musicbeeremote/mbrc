package com.kelsos.mbrc.features.nowplaying.presentation

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.ListitemTrackBinding
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.features.nowplaying.dragsort.TouchHelperViewHolder
import com.kelsos.mbrc.ui.BindableViewHolder
import com.kelsos.mbrc.ui.OnViewItemPressed

@SuppressLint("ClickableViewAccessibility")
class NowPlayingTrackViewHolder(
  binding: ListitemTrackBinding,
  onHolderItemPressed: OnViewItemPressed,
  private val onDrag: (start: Boolean, holder: RecyclerView.ViewHolder) -> Unit
) : BindableViewHolder<NowPlaying>(binding.root),
  TouchHelperViewHolder {

  private val title: TextView = binding.trackTitle
  private val artist: TextView = binding.trackArtist
  private val trackPlaying: ImageView = binding.trackIndicatorView
  private val dragHandle: ImageView = binding.dragHandle

  init {
    itemView.setOnClickListener { onHolderItemPressed(adapterPosition) }
    dragHandle.setOnTouchListener { view, motionEvent ->
      view.performClick()
      if (motionEvent.action == MotionEvent.ACTION_DOWN) {
        onDrag(true, this)
      }
      true
    }
  }

  override fun onItemSelected() {
    this.itemView.setBackgroundColor(Color.DKGRAY)
  }

  override fun onItemClear() {
    this.itemView.setBackgroundColor(0)
    onDrag(false, this)
  }

  override fun bindTo(item: NowPlaying) {
    title.text = item.title
    artist.text = item.artist
  }

  fun setPlayingTrack(isPlayingTrack: Boolean) {
    trackPlaying.setImageResource(
      if (isPlayingTrack) {
        R.drawable.ic_media_now_playing
      } else {
        android.R.color.transparent
      }
    )
  }

  override fun clear() {
    title.text = ""
    artist.text = ""
  }

  companion object {
    fun create(
      parent: ViewGroup,
      onHolderItemPressed: OnViewItemPressed,
      onDrag: (start: Boolean, holder: RecyclerView.ViewHolder) -> Unit
    ): NowPlayingTrackViewHolder {
      val inflater: LayoutInflater = LayoutInflater.from(parent.context)
      val binding: ListitemTrackBinding = DataBindingUtil.inflate(
        inflater,
        R.layout.listitem_track,
        parent,
        false
      )
      return NowPlayingTrackViewHolder(
        binding,
        onHolderItemPressed,
        onDrag
      )
    }
  }
}
