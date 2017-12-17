package com.kelsos.mbrc.ui.navigation.nowplaying

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.nowplaying.NowPlaying
import com.kelsos.mbrc.databinding.UiListTrackItemBinding
import com.kelsos.mbrc.ui.drag.ItemTouchHelperAdapter
import com.kelsos.mbrc.ui.drag.OnStartDragListener
import com.kelsos.mbrc.ui.drag.TouchHelperViewHolder
import timber.log.Timber
import javax.inject.Inject

class NowPlayingAdapter
@Inject
constructor(context: Activity) :
  RecyclerView.Adapter<NowPlayingAdapter.TrackHolder>(),
  ItemTouchHelperAdapter {

  private val dragStartListener: OnStartDragListener = context as OnStartDragListener

  private var data: List<NowPlaying>? = null
  private var playingTrackIndex: Int = 0
  private var currentTrack: String = ""
  private val inflater: LayoutInflater = LayoutInflater.from(context)

  private var listener: NowPlayingListener? = null

  private fun setPlayingTrack(index: Int) {
    notifyItemChanged(playingTrackIndex)
    this.playingTrackIndex = index
    notifyItemChanged(index)
  }

  fun getPlayingTrackIndex(): Int {
    return this.playingTrackIndex
  }

  fun setPlayingTrack(path: String) {
    val data = this.data ?: return

    this.currentTrack = path
    data.forEachIndexed { index, track ->
      if (track.path == path) {
        setPlayingTrack(index)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
    val inflatedView = inflater.inflate(R.layout.ui_list_track_item, parent, false)
    val holder = TrackHolder(inflatedView)
    holder.itemView.setOnClickListener { onClick(holder) }
    holder.container.setOnClickListener { onClick(holder) }
    holder.dragHandle.setOnTouchListener { view, motionEvent ->
      view.performClick()
      if (motionEvent.action == ACTION_DOWN) {
        dragStartListener.onStartDrag(holder)
      }
      return@setOnTouchListener false
    }
    return holder
  }

  private fun onClick(holder: TrackHolder) {
    val listener = this.listener ?: return
    val position = holder.bindingAdapterPosition
    setPlayingTrack(position)
    listener.onPress(position)
  }

  override fun onBindViewHolder(holder: TrackHolder, position: Int) {
    val track = data?.get(holder.bindingAdapterPosition) ?: return
    holder.title.text = track.title
    holder.artist.text = track.artist
    if (position == playingTrackIndex) {
      holder.trackPlaying.setImageResource(R.drawable.ic_media_now_playing)
    } else {
      holder.trackPlaying.setImageResource(android.R.color.transparent)
    }
  }

  override fun getItemCount(): Int {
    return data?.size ?: 0
  }

  override fun onItemMove(from: Int, to: Int): Boolean {
    swapPositions(from, to)
    listener?.onMove(from, to)
    notifyItemMoved(from, to)

    if (currentTrack.isNotBlank()) {
      setPlayingTrack(currentTrack)
    }

    return true
  }

  private fun swapPositions(from: Int, to: Int) {
    val data = this.data ?: return

    Timber.v("Swapping %d => %d", from, to)
    val fromTrack = data[from]
    val toTrack = data[to]
    Timber.v("from => %s to => %s", fromTrack, toTrack)
    // TODO: fix the swap functionality with room
    // Before saving remove the listener to avoid interrupting the swapping functionality

    Timber.v("after swap => from => %s to => %s", fromTrack, toTrack)
  }

  override fun onItemDismiss(position: Int) {
    val nowPlaying = data?.get(position)

    nowPlaying?.let {
      notifyItemRemoved(position)
      listener?.onDismiss(position)
    }
  }

  fun update(cursor: List<NowPlaying>) {
    this.data = cursor
    notifyDataSetChanged()
  }

  fun setListener(listener: NowPlayingListener) {
    this.listener = listener
  }

  interface NowPlayingListener {
    fun onPress(position: Int)
    fun onMove(from: Int, to: Int)
    fun onDismiss(position: Int)
  }

  class TrackHolder(itemView: View) : RecyclerView.ViewHolder(itemView), TouchHelperViewHolder {

    val title: TextView
    val artist: TextView
    val trackPlaying: ImageView
    val container: ConstraintLayout
    val dragHandle: View

    init {
      val binding = UiListTrackItemBinding.bind(itemView)
      title = binding.trackTitle
      artist = binding.trackArtist
      trackPlaying = binding.trackIndicatorView
      container = binding.trackContainer
      dragHandle = binding.dragHandle
    }

    override fun onItemSelected() {
      this.itemView.setBackgroundColor(Color.DKGRAY)
    }

    override fun onItemClear() {
      this.itemView.setBackgroundColor(0)
    }
  }
}
