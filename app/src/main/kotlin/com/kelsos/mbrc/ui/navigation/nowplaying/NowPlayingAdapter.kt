package com.kelsos.mbrc.ui.navigation.nowplaying

import android.app.Activity
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.nowplaying.NowPlayingEntity
import com.kelsos.mbrc.ui.drag.ItemTouchHelperAdapter
import com.kelsos.mbrc.ui.drag.OnStartDragListener
import com.kelsos.mbrc.ui.drag.TouchHelperViewHolder
import kotterknife.bindView
import javax.inject.Inject

class NowPlayingAdapter
@Inject
constructor(context: Activity) : RecyclerView.Adapter<NowPlayingAdapter.TrackHolder>(),
  ItemTouchHelperAdapter {

  private val dragStartListener: OnStartDragListener = context as OnStartDragListener

  private var data: List<NowPlayingEntity>? = null
  private var playingTrackIndex: Int = 0
  private var currentTrack: String = ""
  private val inflater: LayoutInflater = LayoutInflater.from(context)

  private var listener: NowPlayingListener? = null

  fun setPlayingTrack(index: Int) {
    notifyItemChanged(playingTrackIndex)
    this.playingTrackIndex = index
    notifyItemChanged(index)
  }

  fun getPlayingTrackIndex(): Int {
    return this.playingTrackIndex
  }

  fun setPlayingTrack(path: String) {
    if (data == null) {
      return
    }

    this.currentTrack = path
    data?.forEachIndexed { index, (_, _, itemPath) ->
      if (itemPath == path) {
        setPlayingTrack(index)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
    val view = inflater.inflate(R.layout.ui_list_track_item, parent, false)
    val holder = TrackHolder(view)
    holder.itemView.setOnClickListener { onClick(holder) }
    holder.dragHandle.setOnTouchListener { _, motionEvent ->
      if (motionEvent.action == ACTION_DOWN) {
        dragStartListener.onStartDrag(holder)
      }
      return@setOnTouchListener false
    }
    return holder
  }

  private fun onClick(holder: TrackHolder) {
    listener?.let {
      val position = holder.adapterPosition
      setPlayingTrack(position)
      it.onPress(position)
    }
  }

  override fun onBindViewHolder(holder: TrackHolder, position: Int) {
    val nowPlaying = data?.get(holder.adapterPosition)

    nowPlaying?.let { (title, artist) ->
      holder.title.text = title
      holder.artist.text = artist
      if (position == playingTrackIndex) {
        holder.trackPlaying.setImageResource(R.drawable.ic_media_now_playing)
      } else {
        holder.trackPlaying.setImageResource(android.R.color.transparent)
      }
    }
  }

  override fun getItemCount(): Int {
    return data?.size ?: 0
  }

  override fun onItemMove(from: Int, to: Int): Boolean {
    listener?.onMove(from, to)
    notifyItemMoved(from, to)

    if (!currentTrack.isBlank()) {
      setPlayingTrack(currentTrack)
    }

    return true
  }

  override fun onItemDismiss(position: Int) {
    val nowPlaying = data?.get(position)

    nowPlaying?.let {
      //it.delete()
      notifyItemRemoved(position)
      listener?.onDismiss(position)
    }
  }

  fun update(cursor: List<NowPlayingEntity>) {
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

    val title: TextView by bindView(R.id.track_title)
    val artist: TextView by bindView(R.id.track_artist)
    val trackPlaying: ImageView by bindView(R.id.track_indicator_view)
    val dragHandle: View by bindView(R.id.drag_handle)

    override fun onItemSelected() {
      this.itemView.setBackgroundColor(Color.DKGRAY)
    }

    override fun onItemClear() {
      this.itemView.setBackgroundColor(0)
    }
  }
}