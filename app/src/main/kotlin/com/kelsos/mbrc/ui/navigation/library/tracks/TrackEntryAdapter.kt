package com.kelsos.mbrc.ui.navigation.library.tracks

import android.arch.paging.PagedListAdapter
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.Action
import com.kelsos.mbrc.extensions.string
import com.kelsos.mbrc.ui.navigation.library.popup
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller.BubbleTextGetter
import com.kelsos.mbrc.utilities.Checks.ifNotNull
import kotterknife.bindView
import javax.inject.Inject

class TrackEntryAdapter
@Inject
constructor() : PagedListAdapter<TrackEntity, TrackEntryAdapter.ViewHolder>(DIFF_CALLBACK), BubbleTextGetter {

  private var listener: MenuItemSelectedListener? = null
  private val indicatorPressed: (View, Int) -> Unit = { view, position ->
    view.popup(R.menu.popup_track) {

      val action = when (it) {
        R.id.popup_track_queue_next -> LibraryPopup.NEXT
        R.id.popup_track_queue_last -> LibraryPopup.LAST
        R.id.popup_track_play -> LibraryPopup.NOW
        R.id.popup_track_play_queue_all -> LibraryPopup.ADD_ALL
        else -> throw IllegalArgumentException("invalid menuItem id $it")
      }

      ifNotNull(listener, getItem(position)) { listener, track ->
        listener.onMenuItemSelected(action, track)
      }
    }
  }

  private val pressed: (View, Int) -> Unit = { _, position ->
    ifNotNull(listener, getItem(position)) { listener, track ->
      listener.onItemClicked(track)
    }
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder.create(parent, indicatorPressed, pressed)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val trackEntity = getItem(holder.adapterPosition)

    if (trackEntity != null) {
      holder.bindTo(trackEntity)
    } else {
      holder.clear()
    }
  }

  override fun getTextToShowInBubble(pos: Int): String {
    val albumArtist = getItem(pos)?.albumArtist
    if (albumArtist != null && albumArtist.isNotBlank()) {
      return albumArtist.substring(0, 1)
    }
    return "-"
  }

  companion object {
    val DIFF_CALLBACK = object : DiffCallback<TrackEntity>() {
      override fun areItemsTheSame(oldItem: TrackEntity, newItem: TrackEntity): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: TrackEntity, newItem: TrackEntity): Boolean {
        return oldItem == newItem
      }
    }
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(@Action action: String, entry: TrackEntity)

    fun onItemClicked(track: TrackEntity)
  }

  class ViewHolder(
      itemView: View,
      indicatorPressed: (view: View, position: Int) -> Unit,
      pressed: (view: View, position: Int) -> Unit
  ) : RecyclerView.ViewHolder(itemView) {
    private val artist: TextView by bindView(R.id.line_two)
    private val title: TextView by bindView(R.id.line_one)
    private val indicator: ImageView by bindView(R.id.overflow_menu)
    private val unknownArtist: String by lazy { string(R.string.unknown_artist) }

    init {
      indicator.setOnClickListener { indicatorPressed(it, adapterPosition) }
      itemView.setOnClickListener { pressed(it, adapterPosition) }
    }

    companion object {
      fun create(
          parent: ViewGroup,
          indicatorPressed: (view: View, position: Int) -> Unit,
          pressed: (view: View, position: Int) -> Unit
      ): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.ui_list_dual, parent, false)
        return ViewHolder(view, indicatorPressed, pressed)
      }
    }

    fun clear() {
      artist.text = ""
      title.text = ""
    }

    fun bindTo(trackEntity: TrackEntity) {
      title.text = trackEntity.title
      artist.text = if (trackEntity.artist.isBlank()) unknownArtist else trackEntity.artist
    }
  }

}
