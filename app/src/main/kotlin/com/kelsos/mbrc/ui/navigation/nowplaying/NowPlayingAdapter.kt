package com.kelsos.mbrc.ui.navigation.nowplaying

import android.app.Activity
import android.graphics.Color
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
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
import com.kelsos.mbrc.data.NowPlaying
import com.kelsos.mbrc.rx.MapWithIndex
import com.kelsos.mbrc.ui.drag.ItemTouchHelperAdapter
import com.kelsos.mbrc.ui.drag.OnStartDragListener
import com.kelsos.mbrc.ui.drag.TouchHelperViewHolder
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.list.FlowCursorList.OnCursorRefreshListener
import rx.Observable
import timber.log.Timber
import javax.inject.Inject

class NowPlayingAdapter
@Inject constructor(context: Activity) : RecyclerView.Adapter<NowPlayingAdapter.TrackHolder>(),
    ItemTouchHelperAdapter,
    OnCursorRefreshListener<NowPlaying> {

  private val dragStartListener: OnStartDragListener = context as OnStartDragListener

  private var cursor: FlowCursorList<NowPlaying>? = null
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
    if (cursor == null) {
      return
    }

    this.currentTrack = path
    Observable.from(cursor).compose(MapWithIndex.instance<NowPlaying>()).filter {
      val info = it.value()
      info.path.equals(path)
    }.subscribe({ setPlayingTrack(it.index().toInt()) }) { Timber.v(it, "Failed") }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
    val view = inflater.inflate(R.layout.ui_list_track_item, parent, false)
    val holder = TrackHolder(view)
    holder.itemView.setOnClickListener { onClick(holder) }
    holder.container.setOnClickListener { onClick(holder) }
    holder.dragHandle.setOnTouchListener { view, motionEvent ->
      if (MotionEventCompat.getActionMasked(motionEvent) == ACTION_DOWN) {
        dragStartListener.onStartDrag(holder)
      }
      return@setOnTouchListener false
    }
    return holder
  }

  private fun onClick(holder: TrackHolder) {
    if (listener == null) {
      return
    }

    val position = holder.adapterPosition
    setPlayingTrack(position)
    listener!!.onPress(position)
  }

  override fun onBindViewHolder(holder: TrackHolder, position: Int) {
    val track = cursor!!.getItem(position.toLong())
    holder.title.text = track.title
    holder.artist.text = track.artist
    if (position == playingTrackIndex) {
      holder.trackPlaying.setImageResource(R.drawable.ic_media_now_playing)
    } else {
      holder.trackPlaying.setImageResource(android.R.color.transparent)
    }
  }

  override fun getItemCount(): Int {
    return cursor?.count ?: 0
  }

  override fun onItemMove(from: Int, to: Int): Boolean {
    swapPositions(from, to)

    if (listener != null) {
      listener!!.onMove(from, to)
    }

    notifyItemMoved(from, to)

    if (!TextUtils.isEmpty(currentTrack)) {
      setPlayingTrack(currentTrack)
    }

    return true
  }

  private fun swapPositions(from: Int, to: Int) {
    if (cursor == null) {
      return
    }

    Timber.v("Swapping %d => %d", from, to)
    val fromTrack = cursor!!.getItem(from.toLong())
    val toTrack = cursor!!.getItem(to.toLong())
    Timber.v("from => %s to => %s", fromTrack, toTrack)
    val position = toTrack.position
    toTrack.position = fromTrack.position
    fromTrack.position = position
    toTrack.save()
    fromTrack.save()
    // Before saving remove the listener to avoid interrupting the swapping functionality
    cursor!!.removeOnCursorRefreshListener(this)
    cursor!!.refresh()
    cursor!!.addOnCursorRefreshListener(this)
    Timber.v("after swap => from => %s to => %s", fromTrack, toTrack)
  }

  override fun onItemDismiss(position: Int) {
    val item = cursor!!.getItem(position.toLong())
    item.delete()
    refresh()
    notifyItemRemoved(position)
    listener?.onDismiss(position)
  }

  fun refresh() {
    cursor?.refresh()
  }

  fun update(cursor: FlowCursorList<NowPlaying>) {
    this.cursor = cursor
    notifyDataSetChanged()
  }

  fun setListener(listener: NowPlayingListener) {
    this.listener = listener
  }

  /**
   * Callback when cursor refreshes.

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
