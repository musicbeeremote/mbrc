package com.kelsos.mbrc.ui.navigation.library.tracks


import android.arch.paging.PagedListAdapter
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.extensions.string
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller.BubbleTextGetter
import com.kelsos.mbrc.utilities.Checks.ifNotNull
import kotterknife.bindView
import javax.inject.Inject

class TrackEntryAdapter
@Inject
constructor() : PagedListAdapter<TrackEntity, TrackEntryAdapter.ViewHolder>(DIFF_CALLBACK), BubbleTextGetter {
  
  private var listener: MenuItemSelectedListener? = null

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val holder = ViewHolder.create(parent)
    
    holder.indicator.setOnClickListener { createPopup(it, holder) }

    holder.itemView.setOnClickListener {
      val position = holder.adapterPosition
      ifNotNull(listener, getItem(position)) { listener, track ->
        listener.onItemClicked(track)
      }

    }
    return holder
  }

  private fun createPopup(it: View, holder: ViewHolder) {
    val popupMenu = PopupMenu(it.context, it)
    popupMenu.inflate(R.menu.popup_track)
    popupMenu.setOnMenuItemClickListener { menuItem ->
      val position = holder.adapterPosition
      ifNotNull(listener, getItem(position)) { listener, track ->
        listener.onMenuItemSelected(menuItem, track)
      }
      true
    }
    popupMenu.show()
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val entry = getItem(holder.adapterPosition)
    entry?.let { (artist, title) ->
      holder.title.text = title
      holder.artist.text = if (artist.isBlank()) holder.unknownArtist else artist
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
    fun onMenuItemSelected(menuItem: MenuItem, entry: TrackEntity)

    fun onItemClicked(track: TrackEntity)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val artist: TextView by bindView(R.id.line_two)
    val title: TextView by bindView(R.id.line_one)
    val indicator: ImageView by bindView(R.id.overflow_menu)
    val unknownArtist: String by lazy { string(R.string.unknown_artist) }
    
    companion object {
      fun create(parent: ViewGroup): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.ui_list_dual, parent, false)
        return ViewHolder(view)
      }
    }
  }

}
