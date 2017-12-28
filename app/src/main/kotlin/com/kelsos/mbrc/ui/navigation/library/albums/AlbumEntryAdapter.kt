package com.kelsos.mbrc.ui.navigation.library.albums

import android.arch.paging.PagedListAdapter
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.Action
import com.kelsos.mbrc.extensions.string
import com.kelsos.mbrc.ui.navigation.library.popup
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller.BubbleTextGetter
import com.kelsos.mbrc.utilities.Checks
import kotterknife.bindView
import javax.inject.Inject

class AlbumEntryAdapter
@Inject
constructor() : PagedListAdapter<AlbumEntity, AlbumEntryAdapter.ViewHolder>(DIFF_CALLBACK),
    BubbleTextGetter {

  private var listener: MenuItemSelectedListener? = null

  private val indicatorPressed: (View, Int) -> Unit = { view, position ->
    view.popup(R.menu.popup_album) {
      val action = when (it) {
        R.id.popup_album_tracks -> LibraryPopup.PROFILE
        R.id.popup_album_queue_next -> LibraryPopup.NEXT
        R.id.popup_album_queue_last -> LibraryPopup.LAST
        R.id.popup_album_play -> LibraryPopup.NOW
        else -> throw IllegalArgumentException("invalid menuItem id $it")
      }

      Checks.ifNotNull(listener, getItem(position)) { listener, album ->
        listener.onMenuItemSelected(action, album)
      }
    }
  }
  private val pressed: (View, Int) -> Unit = { _, position ->
    getItem(position)?.run {
      listener?.onItemClicked(this)
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder.create(parent, indicatorPressed, pressed)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val albumEntity = getItem(position)

    if (albumEntity != null) {
      holder.bind(albumEntity)
    } else {
      holder.clear()
    }
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  override fun getTextToShowInBubble(pos: Int): String {
    val artist = getItem(pos)?.artist ?: ""
    if (artist.isNotBlank()) {
      return artist.substring(0, 1)
    }
    return "-"
  }

  companion object {
    val DIFF_CALLBACK = object : DiffCallback<AlbumEntity>() {
      override fun areItemsTheSame(oldItem: AlbumEntity, newItem: AlbumEntity): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: AlbumEntity, newItem: AlbumEntity): Boolean {
        return oldItem == newItem
      }
    }
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(@Action action: String, entry: AlbumEntity)

    fun onItemClicked(album: AlbumEntity)
  }

  class ViewHolder(
      itemView: View,
      indicatorPressed: (View, Int) -> Unit,
      pressed: (View, Int) -> Unit
  ) : RecyclerView.ViewHolder(itemView) {
    private val artist: TextView by bindView(R.id.line_two)
    private val album: TextView by bindView(R.id.line_one)
    private val indicator: ImageView by bindView(R.id.overflow_menu)
    private val unknownArtist: String by lazy { string(R.string.unknown_artist) }
    private val emptyAlbum: String by lazy { string(R.string.non_album_tracks) }

    init {
      indicator.setOnClickListener { indicatorPressed(it, adapterPosition) }
      itemView.setOnClickListener { pressed(it, adapterPosition) }
    }

    fun bind(album: AlbumEntity) {
      val title = album.album
      val artist = album.artist
      this.album.text = if (title.isBlank()) emptyAlbum else title
      this.artist.text = if (artist.isBlank()) unknownArtist else artist
    }

    companion object {
      fun create(
          parent: ViewGroup,
          indicatorPressed: (View, Int) -> Unit,
          pressed: (View, Int) -> Unit
      ): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.ui_list_dual, parent, false)
        return ViewHolder(view, indicatorPressed, pressed)
      }
    }

    fun clear() {
      artist.text = ""
      album.text = ""
    }
  }
}
