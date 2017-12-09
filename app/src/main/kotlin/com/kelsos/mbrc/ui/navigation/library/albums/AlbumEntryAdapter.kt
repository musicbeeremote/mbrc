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
import com.kelsos.mbrc.extensions.string
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller.BubbleTextGetter
import com.kelsos.mbrc.utilities.Checks.ifNotNull
import kotterknife.bindView
import javax.inject.Inject

class AlbumEntryAdapter
@Inject
constructor(context: Activity) : RecyclerView.Adapter<AlbumEntryAdapter.ViewHolder>(), BubbleTextGetter {

  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private var data: List<Album> = emptyList()
  private var listener: MenuItemSelectedListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.ui_list_dual, parent, false)
    val holder = ViewHolder(view)
    holder.setIndicatorOnClickListener {
      val popupMenu = PopupMenu(it.context, it)
      popupMenu.inflate(R.menu.popup_album)
      popupMenu.setOnMenuItemClickListener {
        val position = holder.adapterPosition
        ifNotNull(listener, data[position]) { listener, album ->
          listener.onMenuItemSelected(it, album)
        }
        true
      }
      popupMenu.show()
    }

    holder.itemView.setOnClickListener {
      val position = holder.adapterPosition
      val album = data[position]
      listener?.onItemClicked(album)
    }
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val album = data[holder.adapterPosition]
    holder.bind(album)
  }

  override fun getItemCount(): Int = data.size

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  override fun getTextToShowInBubble(pos: Int): String {
    val artist = data[pos].artist
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
    private val artist: TextView by bindView(R.id.line_two)
    private val album: TextView by bindView(R.id.line_one)
    private val indicator: LinearLayout by bindView(R.id.ui_item_context_indicator)
    private val unknownArtist: String by lazy { string(R.string.unknown_artist) }
    private val emptyAlbum: String by lazy { string(R.string.non_album_tracks) }

    fun bind(album: Album) {
      val title = album.album
      val artist = album.artist
      this.album.text = if (title.isNullOrBlank()) emptyAlbum else title
      this.artist.text = if (artist.isNullOrBlank()) unknownArtist else artist
    }

    fun setIndicatorOnClickListener(listener: (view: View) -> Unit) {
      indicator.setOnClickListener { listener(it) }
    }
  }

  fun update(albums: List<Album>) {
    data = albums
    notifyDataSetChanged()
  }
}
