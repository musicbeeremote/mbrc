package com.kelsos.mbrc.features.nowplaying.presentation

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import arrow.core.Option
import arrow.core.extensions.option.monad.binding
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.helpers.VisibleRangeGetter
import com.kelsos.mbrc.databinding.UiListTrackItemBinding
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.features.nowplaying.dragsort.ItemTouchHelperAdapter
import com.kelsos.mbrc.features.nowplaying.dragsort.OnStartDragListener
import com.kelsos.mbrc.features.nowplaying.dragsort.TouchHelperViewHolder
import com.kelsos.mbrc.ui.BindableViewHolder
import com.kelsos.mbrc.ui.OnViewItemPressed

class NowPlayingAdapter(
  private val dragStartListener: OnStartDragListener,
  private val nowPlayingListener: NowPlayingListener,
  private val visibleRangeGetter: VisibleRangeGetter
) : PagingDataAdapter<NowPlaying, NowPlayingAdapter.NowPlayingTrackViewHolder>(
  NOW_PLAYING_COMPARATOR
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
      val track = Option.fromNullable(getItem(holder.bindingAdapterPosition))
      binding {
        val (nowPlayingTrack) = track
        val isPlayingTrack = nowPlayingTrack.path == currentTrack
        holder.setPlayingTrack(isPlayingTrack)
        if (isPlayingTrack) {
          playingTrackIndex = holder.bindingAdapterPosition
        }
      }
    } else {
      onBindViewHolder(holder, position)
    }
  }

  override fun onBindViewHolder(holder: NowPlayingTrackViewHolder, position: Int) {
    val track = Option.fromNullable(getItem(holder.bindingAdapterPosition))
    binding {
      val (nowPlayingTrack) = track
      val isPlayingTrack = nowPlayingTrack.path == currentTrack
      holder.bindTo(nowPlayingTrack)
      holder.setPlayingTrack(isPlayingTrack)

      if (isPlayingTrack) {
        playingTrackIndex = holder.bindingAdapterPosition
      }
    }
  }

  override fun onItemMove(from: Int, to: Int): Boolean {
    notifyItemMoved(from, to)
    nowPlayingListener.onMove(from, to)

    if (currentTrack.isNotBlank()) {
      setPlayingTrack(currentTrack)
    }

    return true
  }

  override fun onItemDismiss(position: Int) {
    getItem(position)?.let {
      nowPlayingListener.onDismiss(position)
    }
  }

  interface NowPlayingListener {
    fun onPress(position: Int)
    fun onMove(from: Int, to: Int)
    fun onDismiss(position: Int)
  }

  class NowPlayingTrackViewHolder(
    binding: UiListTrackItemBinding,
    onHolderItemPressed: OnViewItemPressed,
    private val onDrag: (start: Boolean, holder: RecyclerView.ViewHolder) -> Unit
  ) : BindableViewHolder<NowPlaying>(binding), TouchHelperViewHolder {

    private val title: TextView = binding.trackTitle
    private val artist: TextView = binding.trackArtist
    private val trackPlaying: ImageView = binding.trackIndicatorView

    init {
      itemView.setOnClickListener { onHolderItemPressed(bindingAdapterPosition) }
      binding.dragHandle.setOnTouchListener { view, motionEvent ->
        if (motionEvent.action == ACTION_DOWN) {
          view.performClick()
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
        val binding = UiListTrackItemBinding.inflate(LayoutInflater.from(parent.context))
        return NowPlayingTrackViewHolder(
          binding,
          onHolderItemPressed,
          onDrag
        )
      }
    }
  }

  companion object {
    const val PLAYING_CHANGED = "playing_changed"
    val NOW_PLAYING_COMPARATOR = object : DiffUtil.ItemCallback<NowPlaying>() {
      override fun areItemsTheSame(oldItem: NowPlaying, newItem: NowPlaying): Boolean {
        return oldItem.path == newItem.path
      }

      override fun areContentsTheSame(oldItem: NowPlaying, newItem: NowPlaying): Boolean {
        return oldItem.position == newItem.position &&
          oldItem.artist == newItem.artist &&
          oldItem.title == newItem.title
      }
    }
  }
}
