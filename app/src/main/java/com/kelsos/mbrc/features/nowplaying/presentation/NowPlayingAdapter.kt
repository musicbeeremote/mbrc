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
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.UiListTrackItemBinding
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.ui.BindableViewHolder
import com.kelsos.mbrc.ui.OnViewItemPressed
import com.kelsos.mbrc.ui.drag.ItemTouchHelperAdapter
import com.kelsos.mbrc.ui.drag.OnStartDragListener
import com.kelsos.mbrc.ui.drag.TouchHelperViewHolder

class NowPlayingAdapter(
  private val dragStartListener: OnStartDragListener
) : PagingDataAdapter<NowPlaying, NowPlayingAdapter.NowPlayingTrackViewHolder>(
  NOW_PLAYING_COMPARATOR
),
  ItemTouchHelperAdapter {
  private var currentTrack = ""
  private var playingTrackIndex = -1

  private var listener: NowPlayingListener? = null

  fun getPlayingTrackIndex(): Int = this.playingTrackIndex

  fun setPlayingTrack(path: String) {
    this.currentTrack = path
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NowPlayingTrackViewHolder {
    return NowPlayingTrackViewHolder.create(
      parent,
      { position ->
        listener?.onPress(position)
        playingTrackIndex = position
        currentTrack = getItem(position)?.path ?: ""
      }
    ) { holder -> dragStartListener.onStartDrag(holder) }
  }

  override fun onBindViewHolder(holder: NowPlayingTrackViewHolder, position: Int) {
    with(holder) {
      getItem(bindingAdapterPosition)?.let {
        bindTo(it)

        val isPlayingTrack = it.path == currentTrack
        setPlayingTrack(isPlayingTrack)

        if (isPlayingTrack) {
          playingTrackIndex = bindingAdapterPosition
        }
      }
    }
  }

  override fun onItemMove(from: Int, to: Int): Boolean {
    listener?.onMove(from, to)
    notifyItemMoved(from, to)

    if (currentTrack.isNotBlank()) {
      setPlayingTrack(currentTrack)
    }

    return true
  }

  override fun onItemDismiss(position: Int) {
    val nowPlaying = getItem(position)

    nowPlaying?.let {
      notifyItemRemoved(position)
      listener?.onDismiss(position)
    }
  }

  fun setListener(listener: NowPlayingListener) {
    this.listener = listener
  }

  interface NowPlayingListener {
    fun onPress(position: Int)
    fun onMove(from: Int, to: Int)
    fun onDismiss(position: Int)
  }

  class NowPlayingTrackViewHolder(
    binding: UiListTrackItemBinding,
    onHolderItemPressed: OnViewItemPressed,
    onDragStart: (holder: RecyclerView.ViewHolder) -> Unit
  ) : BindableViewHolder<NowPlaying>(binding), TouchHelperViewHolder {

    private val title: TextView = binding.trackTitle
    private val artist: TextView = binding.trackArtist
    private val trackPlaying: ImageView = binding.trackIndicatorView

    init {
      itemView.setOnClickListener { onHolderItemPressed(bindingAdapterPosition) }
      binding.dragHandle.setOnTouchListener { view, motionEvent ->
        return@setOnTouchListener if (motionEvent.action == ACTION_DOWN) {
          view.performClick()
          onDragStart(this)
          true
        } else {
          false
        }
      }
    }

    override fun onItemSelected() {
      this.itemView.setBackgroundColor(Color.DKGRAY)
    }

    override fun onItemClear() {
      this.itemView.setBackgroundColor(0)
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
        onDragStart: (holder: RecyclerView.ViewHolder) -> Unit
      ): NowPlayingTrackViewHolder {
        val binding = UiListTrackItemBinding.inflate(LayoutInflater.from(parent.context))
        return NowPlayingTrackViewHolder(
          binding,
          onHolderItemPressed,
          onDragStart
        )
      }
    }
  }

  companion object {
    val NOW_PLAYING_COMPARATOR = object : DiffUtil.ItemCallback<NowPlaying>() {
      override fun areItemsTheSame(oldItem: NowPlaying, newItem: NowPlaying): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: NowPlaying, newItem: NowPlaying): Boolean {
        return oldItem.position == newItem.position &&
          oldItem.artist == newItem.artist &&
          oldItem.title == newItem.title
      }
    }
  }
}
