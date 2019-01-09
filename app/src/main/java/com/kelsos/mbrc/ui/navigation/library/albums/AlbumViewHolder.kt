package com.kelsos.mbrc.ui.navigation.library.albums

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.albums.key
import com.kelsos.mbrc.databinding.UiListDualBinding
import com.kelsos.mbrc.ui.BindableViewHolder
import com.kelsos.mbrc.ui.widgets.SquareImageView
import com.squareup.picasso.Picasso
import java.io.File

class AlbumViewHolder(
  binding: UiListDualBinding,
  private val cache: File
) : BindableViewHolder<Album>(binding) {
  private val artist: TextView = binding.lineTwo
  private val album: TextView = binding.lineOne
  private val image: SquareImageView = binding.cover
  private val indicator: ImageView = binding.overflowMenu
  private val unknownArtist: String by lazy { itemView.context.getString(R.string.unknown_artist) }
  private val emptyAlbum: String by lazy { itemView.context.getString(R.string.non_album_tracks) }

  init {
    image.isGone = false
  }

  override fun bindTo(item: Album) {
    val title = item.album
    val artist = item.artist
    this.album.text = if (title.isBlank()) emptyAlbum else title
    this.artist.text = if (artist.isBlank()) unknownArtist else artist

    Picasso.get()
      .load(File(cache, item.key()))
      .noFade()
      .config(Bitmap.Config.RGB_565)
      .error(R.drawable.ic_image_no_cover)
      .placeholder(R.drawable.ic_image_no_cover)
      .resizeDimen(R.dimen.list_album_size, R.dimen.list_album_size)
      .centerCrop()
      .into(image)
  }

  fun onIndicatorClick(onClick: (view: View, position: Int) -> Unit) {
    indicator.setOnClickListener { onClick(it, bindingAdapterPosition) }
  }

  override fun clear() {
    album.text = ""
    artist.text = ""
    image.setImageResource(R.drawable.ic_image_no_cover)
  }

  companion object {
    fun create(parent: ViewGroup): AlbumViewHolder {
      val layoutInflater = LayoutInflater.from(parent.context)
      val binding = UiListDualBinding.inflate(layoutInflater, parent, false)
      val cache = File(parent.context.cacheDir, "covers")
      return AlbumViewHolder(binding, cache)
    }
  }
}
