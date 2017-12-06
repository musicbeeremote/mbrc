package com.kelsos.mbrc.ui.navigation.library.albums

import android.app.Activity
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView



import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.extensions.count
import com.kelsos.mbrc.extensions.string
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller.BubbleTextGetter
import com.kelsos.mbrc.utilities.Checks.ifNotNull
import com.raizlabs.android.dbflow.list.FlowCursorList
import kotterknife.bindView
import javax.inject.Inject

class AlbumEntryAdapter
@Inject
constructor(context: Activity) : RecyclerView.Adapter<AlbumEntryAdapter.ViewHolder>(), BubbleTextGetter {

  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private var data: FlowCursorList<Album>? = null
  private var listener: MenuItemSelectedListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.ui_list_dual, parent, false)
    val holder = ViewHolder(view)
    holder.indicator.setOnClickListener {
      val popupMenu = PopupMenu(it.context, it)
      popupMenu.inflate(R.menu.popup_album)
      popupMenu.setOnMenuItemClickListener {
        val position = holder.adapterPosition.toLong()
        ifNotNull(listener, data?.getItem(position)) { listener, album ->
          listener.onMenuItemSelected(it, album)
        }
        true
      }
      popupMenu.show()
    }

    holder.itemView.setOnClickListener {
      val position = holder.adapterPosition.toLong()
      val album = data?.getItem(position)

      ifNotNull(listener, album) { listener, album ->
        listener.onItemClicked(album)
      }

    }
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val album = data?.getItem(position.toLong())
    album?.let { (artist, title) ->
      holder.album.text = if (title.isNullOrBlank()) holder.emptyAlbum else title
      holder.artist.text = if (artist.isNullOrBlank()) holder.unknownArtist else artist
    }

  }

  fun refresh() {
    data?.refresh()
    notifyDataSetChanged()
  }

  override fun getItemCount(): Int = data.count()

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  override fun getTextToShowInBubble(pos: Int): String {
    val artist = data?.getItem(pos.toLong())?.artist
    if (artist != null && artist.isNotBlank()) {
      return artist.substring(0, 1)
    }
    return "-"
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(menuItem: MenuItem, entry: Album)

    fun onItemClicked(album: Album)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val artist: TextView by bindView(R.id.line_two)
    val album: TextView by bindView(R.id.line_one)
    val indicator: LinearLayout by bindView(R.id.ui_item_context_indicator)
    val unknownArtist: String by lazy { string(R.string.unknown_artist) }
    val emptyAlbum: String by lazy { string(R.string.non_album_tracks) }
  }

  fun update(albums: FlowCursorList<Album>) {
    data = albums
    notifyDataSetChanged()
  }
}
