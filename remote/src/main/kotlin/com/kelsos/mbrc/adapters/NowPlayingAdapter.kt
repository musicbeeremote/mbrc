package com.kelsos.mbrc.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.dao.QueueTrackDao
import com.kelsos.mbrc.domain.QueueTrack
import com.kelsos.mbrc.interfaces.ITouchHelperAdapter
import java.util.ArrayList
import java.util.Collections
import timber.log.Timber

class NowPlayingAdapter
@Inject constructor(context: Context) : RecyclerView.Adapter<NowPlayingAdapter.TrackHolder>(), ITouchHelperAdapter {
  private val data: MutableList<QueueTrack>
  private var playingTrackIndex: Int = 0
  private val inflater: LayoutInflater
  private var listener: OnUserActionListener? = null

  init {
    inflater = LayoutInflater.from(context)
    data = ArrayList<QueueTrack>()
    setHasStableIds(true)
  }

  fun updateData(data: List<QueueTrack>) {
    this.data.addAll(data)
    notifyDataSetChanged()
  }

  fun clearData() {
    this.data.clear()
    notifyDataSetChanged()
  }

  fun setPlayingTrackIndex(index: Int) {
    this.playingTrackIndex = index
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
    val view = inflater.inflate(R.layout.ui_list_track_item, parent, false)
    return TrackHolder(view)
  }

  override fun onBindViewHolder(holder: TrackHolder, position: Int) {
    val track = data[position]
    holder.title.text = track.title
    holder.artist.text = track.artist

    if (position == playingTrackIndex) {
      holder.trackPlaying.setImageResource(R.drawable.ic_media_now_playing)
    } else {
      holder.trackPlaying.setImageResource(android.R.color.transparent)
    }

    holder.container.setOnClickListener listener@{ v ->
      Timber.v("Clicked")
      if (listener == null) {
        return@listener
      }

      listener!!.onItemClicked(position, track)
    }
  }

  override fun getItemCount(): Int {
    return data.size
  }

  override fun getItemId(position: Int): Long {
    return data[position].position.toLong()
  }

  fun setOnUserActionListener(listener: OnUserActionListener) {
    this.listener = listener
  }

  /**
   * This method is used to restore the tracks to their original positions in case the move
   * failed to complete.

   * @param from The original position of the [QueueTrackDao].
   * *
   * @param to The position the element was original moved to.
   */
  fun restorePositions(from: Int, to: Int) {
    val track = data[to]
    data.remove(track)
    data.add(from, track)
  }

  fun insert(track: QueueTrack, index: Int) {
    data.add(index, track)
  }

  fun setPlayingTrack(track: QueueTrack) {
    setPlayingTrackIndex(data.indexOf(track))
    notifyDataSetChanged()
  }

  override fun onItemMove(from: Int, to: Int) {
    if (from < to) {
      for (i in from..to - 1) {
        Collections.swap(data, i, i + 1)
      }
    } else {
      for (i in from downTo to + 1) {
        Collections.swap(data, i, i - 1)
      }
    }
    notifyItemMoved(from, to)
    listener?.onItemMoved(from, to)
  }

  override fun onItemDismiss(position: Int) {
    data.removeAt(position)
    notifyItemRemoved(position)
    listener?.onItemRemoved(position)
  }

  interface OnUserActionListener {
    fun onItemRemoved(position: Int)

    fun onItemMoved(from: Int, to: Int)

    fun onItemClicked(position: Int, track: QueueTrack)
  }

  class TrackHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.drag_handle) lateinit var dragHandle: View
    @BindView(R.id.track_title) lateinit var title: TextView
    @BindView(R.id.track_artist) lateinit var artist: TextView
    @BindView(R.id.track_indicator_view) lateinit var trackPlaying: ImageView
    @BindView(R.id.container) lateinit var container: FrameLayout

    init {
      ButterKnife.bind(this, itemView)
    }
  }
}
