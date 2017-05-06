package com.kelsos.mbrc.ui.navigation.nowplaying

import android.app.Activity
import android.arch.paging.PagedListAdapter
import android.graphics.Color
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.nowplaying.NowPlayingEntity
import com.kelsos.mbrc.ui.BindableViewHolder
import com.kelsos.mbrc.ui.OnViewItemPressed
import com.kelsos.mbrc.ui.drag.ItemTouchHelperAdapter
import com.kelsos.mbrc.ui.drag.OnStartDragListener
import com.kelsos.mbrc.ui.drag.TouchHelperViewHolder
import com.kelsos.mbrc.utilities.Checks.ifNotNull
import kotterknife.bindView
import javax.inject.Inject

class NowPlayingAdapter
@Inject
constructor(context: Activity) :
  PagedListAdapter<NowPlayingEntity, NowPlayingAdapter.NowPlayingTrackViewHolder>(
    DIFF_CALLBACK
  ),
  ItemTouchHelperAdapter {

  private val dragStartListener = context as OnStartDragListener

  private var currentTrack = ""
  private var playingTrackIndex = -1

  private var listener: NowPlayingListener? = null

  fun getPlayingTrackIndex(): Int = this.playingTrackIndex

  fun setPlayingTrack(path: String) {
    this.currentTrack = path

    //todo notify range change with paylaod
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NowPlayingTrackViewHolder {
    return NowPlayingTrackViewHolder.create(parent, { position ->
      listener?.onPress(position)
      playingTrackIndex = position
      currentTrack = getItem(position)?.path ?: ""
    }) { holder -> dragStartListener.onStartDrag(holder) }
  }

  override fun onBindViewHolder(holder: NowPlayingTrackViewHolder, position: Int) {
    with(holder) {
      getItem(adapterPosition)?.let {
        bindTo(it)

        val isPlayingTrack = it.path == currentTrack
        setPlayingTrack(isPlayingTrack)

        if (isPlayingTrack) {
          playingTrackIndex = adapterPosition
        }
      }
    }

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
    val nowPlaying = getItem(position)

    nowPlaying?.let {
      //it.delete()
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
    itemView: View,
    onHolderItemPressed: OnViewItemPressed,
    onDragStart: (holder: RecyclerView.ViewHolder) -> Unit
  ) : BindableViewHolder<NowPlayingEntity>(itemView),
    TouchHelperViewHolder {

    private val title: TextView by bindView(R.id.track_title)
    private val artist: TextView by bindView(R.id.track_artist)
    private val trackPlaying: ImageView by bindView(R.id.track_indicator_view)
    private val dragHandle: View by bindView(R.id.drag_handle)

    init {
      itemView.setOnClickListener { onHolderItemPressed(adapterPosition) }
      dragHandle.setOnTouchListener { _, motionEvent ->
        return@setOnTouchListener if (motionEvent.action == ACTION_DOWN) {
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

    override fun bindTo(item: NowPlayingEntity) {
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
        return NowPlayingTrackViewHolder(view, onHolderItemPressed, onDragStart)
      }
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NowPlayingEntity>() {
      override fun areItemsTheSame(oldItem: NowPlayingEntity, newItem: NowPlayingEntity): Boolean {
        val samePath = oldItem.path == newItem.path
        val samePosition = oldItem.position == newItem.position
        return samePath && samePosition
      }

      override fun areContentsTheSame(
        oldItem: NowPlayingEntity,
        newItem: NowPlayingEntity
      ): Boolean {
        return oldItem == newItem
      }
    }
  }
}