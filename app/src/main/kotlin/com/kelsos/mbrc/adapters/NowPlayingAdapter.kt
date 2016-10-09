package com.kelsos.mbrc.adapters

import android.app.Activity

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.data.NowPlaying
import com.kelsos.mbrc.data.NowPlaying_Table
import com.kelsos.mbrc.rx.MapWithIndex
import com.kelsos.mbrc.ui.drag.ItemTouchHelperAdapter
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.orderBy
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.list.FlowQueryList
import com.raizlabs.android.dbflow.sql.language.OrderBy
import rx.Observable
import timber.log.Timber
import javax.inject.Inject
class NowPlayingAdapter
@Inject constructor(context: Activity) : RecyclerView.Adapter<NowPlayingAdapter.TrackHolder>(), ItemTouchHelperAdapter, FlowCursorList.OnCursorRefreshListener<NowPlaying> {

  private val data: FlowQueryList<NowPlaying>
  private var playingTrackIndex: Int = 0
  private var currentTrack: String = ""
  private val inflater: LayoutInflater

  private var listener: NowPlayingListener? = null

  init {
    val positionAscending = OrderBy.fromProperty(NowPlaying_Table.position).ascending()
    data = (select from NowPlaying::class orderBy positionAscending).flowQueryList()


    data.addOnCursorRefreshListener(this)
    inflater = LayoutInflater.from(context)
  }

  fun setPlayingTrack(index: Int) {
    notifyItemChanged(playingTrackIndex)
    this.playingTrackIndex = index
    notifyItemChanged(index)
  }

  fun setPlayingTrack(path: String) {
    this.currentTrack = path
    Observable.from(data).compose(MapWithIndex.instance<NowPlaying>()).filter {
      val info = it.value()
      info.path.equals(path)
    }.subscribe({ setPlayingTrack(it.index().toInt()) }) { Timber.v(it, "Failed") }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
    val view = inflater.inflate(R.layout.ui_list_track_item, parent, false)
    val holder = TrackHolder(view)
    holder.itemView.setOnClickListener { onClick(holder) }
    holder.container.setOnClickListener { onClick(holder) }
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
    val track = data[position]
    holder.title.text = track.title
    holder.artist.text = track.artist
    if (position == playingTrackIndex) {
      holder.trackPlaying.setImageResource(R.drawable.ic_media_now_playing)
    } else {
      holder.trackPlaying.setImageResource(android.R.color.transparent)
    }
  }

  override fun getItemCount(): Int {
    return data.size
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
    Timber.v("Swapping %d => %d", from, to)
    val fromTrack = data[from]
    val toTrack = data[to]
    Timber.v("from => %s to => %s", fromTrack, toTrack)
    val position = toTrack.position
    toTrack.position = fromTrack.position
    fromTrack.position = position
    toTrack.save()
    fromTrack.save()
    // Before saving remove the listener to avoid interrupting the swapping functionality
    data.removeOnCursorRefreshListener(this)
    data.refresh()
    data.addOnCursorRefreshListener(this)
    Timber.v("after swap => from => %s to => %s", fromTrack, toTrack)
  }

  override fun onItemDismiss(position: Int) {
    data.removeAt(position)
    notifyItemRemoved(position)
    if (listener != null) {
      listener!!.onDismiss(position)
    }
  }

  fun refresh() {
    data.refreshAsync()
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
  class TrackHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.track_title) lateinit var title: TextView
    @BindView(R.id.track_artist) lateinit var artist: TextView

    @BindView(R.id.track_indicator_view) lateinit var trackPlaying: ImageView
    @BindView(R.id.track_container) lateinit var container: FrameLayout

    init {
      ButterKnife.bind(this, itemView)
    }

  }
}
