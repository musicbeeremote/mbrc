package com.kelsos.mbrc.ui.navigation.nowplaying

import android.app.Activity
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.now_playing.NowPlaying
import com.kelsos.mbrc.ui.drag.ItemTouchHelperAdapter
import com.kelsos.mbrc.ui.drag.OnStartDragListener
import com.kelsos.mbrc.ui.drag.TouchHelperViewHolder
import com.kelsos.mbrc.utilities.Checks.ifNotNull
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.list.FlowCursorList.OnCursorRefreshListener
import timber.log.Timber
import javax.inject.Inject

class NowPlayingAdapter
@Inject constructor(context: Activity) : RecyclerView.Adapter<NowPlayingAdapter.TrackHolder>(),
    ItemTouchHelperAdapter,
    OnCursorRefreshListener<NowPlaying> {

  private val dragStartListener: OnStartDragListener = context as OnStartDragListener

  private var data: FlowCursorList<NowPlaying>? = null
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
      if (itemPath.equals(path)) {
        setPlayingTrack(index)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
    val view = inflater.inflate(R.layout.ui_list_track_item, parent, false)
    val holder = TrackHolder(view)
    holder.itemView.setOnClickListener { onClick(holder) }
    holder.container.setOnClickListener { onClick(holder) }
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
    val nowPlaying = data?.getItem(position.toLong())

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
    return data?.count?.toInt() ?: 0
  }

  override fun onItemMove(from: Int, to: Int): Boolean {
    swapPositions(from, to)
    listener?.onMove(from, to)
    notifyItemMoved(from, to)

    if (!currentTrack.isBlank()) {
      setPlayingTrack(currentTrack)
    }

    return true
  }

  private fun swapPositions(from: Int, to: Int) {
    data?.let {
      Timber.v("Swapping %d => %d", from, to)
      ifNotNull(it.getItem(from.toLong()), it.getItem(to.toLong())) { from, to ->
        Timber.v("from => %s to => %s", from, to)
        val position = to.position
        to.position = from.position
        from.position = position
        to.save()
        from.save()

        // Before saving remove the listener to avoid interrupting the swapping functionality
        it.removeOnCursorRefreshListener(this)
        it.refresh()
        it.addOnCursorRefreshListener(this)

        Timber.v("after swap => from => %s to => %s", from, to)
      }
    }
  }

  override fun onItemDismiss(position: Int) {
    val nowPlaying = data?.getItem(position.toLong())

    nowPlaying?.let {
      it.delete()
      refresh()
      notifyItemRemoved(position)
      listener?.onDismiss(position)
    }
  }

  fun refresh() {
    data?.refresh()
  }

  fun update(cursor: FlowCursorList<NowPlaying>) {
    this.data = cursor
    notifyDataSetChanged()
  }

  fun setListener(listener: NowPlayingListener) {
    this.listener = listener
  }

  /**
   * Callback when data refreshes.

   * @param cursorList The object that changed.
   */
  override fun onCursorRefreshed(cursorList: FlowCursorList<NowPlaying>) {
    notifyDataSetChanged()
  }

  interface NowPlayingListener {
    fun onPress(position: Int)
    fun onMove(from: Int, to: Int)
    fun onDismiss(position: Int)
  }

  class TrackHolder(itemView: View) : RecyclerView.ViewHolder(itemView), TouchHelperViewHolder {

    @BindView(R.id.track_title) lateinit var title: TextView
    @BindView(R.id.track_artist) lateinit var artist: TextView
    @BindView(R.id.track_indicator_view) lateinit var trackPlaying: ImageView
    @BindView(R.id.track_container) lateinit var container: FrameLayout
    @BindView(R.id.drag_handle) lateinit var dragHandle: View

    init {
      ButterKnife.bind(this, itemView)
    }

    override fun onItemSelected() {
      this.itemView.setBackgroundColor(Color.DKGRAY)
    }

    override fun onItemClear() {
      this.itemView.setBackgroundColor(0)
    }

  }
}
