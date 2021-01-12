package com.kelsos.mbrc.features.library.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.extensions.string
import com.kelsos.mbrc.databinding.ListitemDualBinding
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.ui.BindableViewHolder

class AlbumViewHolder(
  binding: ListitemDualBinding,
  indicatorPressed: (View, Int) -> Unit,
  pressed: (View, Int) -> Unit
) : BindableViewHolder<Album>(binding.root) {
  private val artist: TextView = binding.lineTwo
  private val album: TextView = binding.lineOne
  private val indicator: ImageView = binding.overflowMenu
  private val unknownArtist: String by lazy { string(R.string.unknown_artist) }
  private val emptyAlbum: String by lazy { string(R.string.non_album_tracks) }

  init {
    indicator.setOnClickListener { indicatorPressed(it, adapterPosition) }
    itemView.setOnClickListener { pressed(it, adapterPosition) }
  }

  override fun bindTo(item: Album) {
    val title = item.album
    val artist = item.artist
    this.album.text = if (title.isBlank()) emptyAlbum else title
    this.artist.text = if (artist.isBlank()) unknownArtist else artist
  }

  override fun clear() {
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
      val binding: ListitemDualBinding = DataBindingUtil.inflate(
        inflater,
        R.layout.listitem_dual,
        parent,
        false
      )
      return AlbumViewHolder(
        binding,
        indicatorPressed,
        pressed
      )
    }
  }
}
