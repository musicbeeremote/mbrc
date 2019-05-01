package com.kelsos.mbrc.features.nowplaying.presentation

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import arrow.core.Option
import arrow.core.extensions.option.monad.binding
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.features.nowplaying.dragsort.ItemTouchHelperAdapter
import com.kelsos.mbrc.features.nowplaying.dragsort.OnStartDragListener
import com.kelsos.mbrc.features.nowplaying.dragsort.TouchHelperViewHolder
import com.kelsos.mbrc.ui.BindableViewHolder
import com.kelsos.mbrc.ui.OnViewItemPressed
import kotterknife.bindView

class NowPlayingAdapter(
  private val dragStartListener: OnStartDragListener,
  private val nowPlayingListener: NowPlayingListener
) :
  PagedListAdapter<NowPlaying, NowPlayingAdapter.NowPlayingTrackViewHolder>(
    DIFF_CALLBACK
  ),
  ItemTouchHelperAdapter {

  private var currentTrack = ""
  private var playingTrackIndex = -1

  fun getPlayingTrackIndex(): Int = this.playingTrackIndex

  fun setPlayingTrack(path: String) {
    this.currentTrack = path

    // todo notify range change with paylaod
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NowPlayingTrackViewHolder {
    return NowPlayingTrackViewHolder.create(
      parent,
      { position ->
        nowPlayingListener.onPress(position)
        playingTrackIndex = position
        currentTrack = getItem(position)?.path ?: ""
      }) { holder -> dragStartListener.onStartDrag(holder) }
  }

  override fun onBindViewHolder(holder: NowPlayingTrackViewHolder, position: Int) {
    val track = Option.fromNullable(getItem(holder.adapterPosition))
    binding {
      val (nowPlayingTrack) = track
      val isPlayingTrack = nowPlayingTrack.path == currentTrack
      holder.bindTo(nowPlayingTrack)
      holder.setPlayingTrack(isPlayingTrack)

      if (isPlayingTrack) {
        playingTrackIndex = holder.adapterPosition
      }
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
    itemView: View,
    onHolderItemPressed: OnViewItemPressed,
    onDragStart: (holder: RecyclerView.ViewHolder) -> Unit
  ) : BindableViewHolder<NowPlaying>(itemView),
    TouchHelperViewHolder {

    private val title: TextView by bindView(R.id.track_title)
    private val artist: TextView by bindView(R.id.track_artist)
    private val trackPlaying: ImageView by bindView(R.id.track_indicator_view)
    private val dragHandle: View by bindView(R.id.drag_handle)

    init {
      itemView.setOnClickListener { onHolderItemPressed(adapterPosition) }
      dragHandle.setOnTouchListener { _, motionEvent ->
        if (motionEvent.action == ACTION_DOWN) {
          onDragStart(this)
        }
        true
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
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.ui_list_track_item, parent, false)
        return NowPlayingTrackViewHolder(
          view,
          onHolderItemPressed,
          onDragStart
        )
      }
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NowPlaying>() {
      override fun areItemsTheSame(oldItem: NowPlaying, newItem: NowPlaying): Boolean {
        return oldItem.id == newItem.id
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