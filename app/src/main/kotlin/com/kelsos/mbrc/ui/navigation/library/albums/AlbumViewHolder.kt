package com.kelsos.mbrc.ui.navigation.library.albums


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.Group
import androidx.core.view.isVisible
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.extensions.string
import com.kelsos.mbrc.ui.BindableViewHolder
import kotterknife.bindView

class AlbumViewHolder(
  itemView: View,
  indicatorPressed: (View, Int) -> Unit,
  pressed: (View, Int) -> Unit
) : BindableViewHolder<AlbumEntity>(itemView) {
  private val artist: TextView by bindView(R.id.line_two)
  private val album: TextView by bindView(R.id.line_one)
  private val indicator: ImageView by bindView(R.id.overflow_menu)
  private val loading: Group by bindView(R.id.listitem_loading)
  private val unknownArtist: String by lazy { string(R.string.unknown_artist) }
  private val emptyAlbum: String by lazy { string(R.string.non_album_tracks) }

  init {
    indicator.setOnClickListener { indicatorPressed(it, adapterPosition) }
    itemView.setOnClickListener { pressed(it, adapterPosition) }
  }

  override fun bindTo(item: AlbumEntity) {
    loading.isVisible = false
    val title = item.album
    val artist = item.artist
    this.album.text = if (title.isBlank()) emptyAlbum else title
    this.artist.text = if (artist.isBlank()) unknownArtist else artist
  }

  override fun clear() {
    loading.isVisible = true
    artist.text = ""
    album.text = ""
  }

  companion object {
    fun create(
      parent: ViewGroup,
      indicatorPressed: (View, Int) -> Unit,
      pressed: (View, Int) -> Unit
    ): AlbumViewHolder {
      val inflater: LayoutInflater = LayoutInflater.from(parent.context)
      val view = inflater.inflate(R.layout.ui_list_dual, parent, false)
      return AlbumViewHolder(view, indicatorPressed, pressed)
    }
  }
}