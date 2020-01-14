package com.kelsos.mbrc.features.library.tracks

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.UiListDualBinding
import com.kelsos.mbrc.ui.BindableViewHolder
import com.squareup.picasso.Picasso
import java.io.File

class TrackViewHolder(
  binding: UiListDualBinding,
  private val cache: File,
  private val coverMode: Boolean
) : BindableViewHolder<Track>(binding) {
  private val artist: TextView = binding.lineTwo
  private val title: TextView = binding.lineOne
  private val indicator: ImageView = binding.overflowMenu
  private val unknownArtist: String by lazy { itemView.context.getString(R.string.unknown_artist) }
  private val image: ImageView = binding.cover

  override fun bindTo(item: Track) {
    title.text = item.title
    artist.text = if (item.artist.isBlank()) unknownArtist else item.artist
    image.isGone = !coverMode

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

  override fun clear() {
    title.text = ""
    artist.text = ""
    image.setImageResource(R.drawable.ic_image_no_cover)
  }

  fun onIndicatorClick(onClick: (view: View, position: Int) -> Unit) {
    indicator.setOnClickListener { onClick(it, bindingAdapterPosition) }
  }

  companion object {
    fun create(parent: ViewGroup, coverMode: Boolean): TrackViewHolder {
      val layoutInflater = LayoutInflater.from(parent.context)
      val binding = UiListDualBinding.inflate(layoutInflater, parent, false)
      val cache = File(parent.context.cacheDir, "covers")
      return TrackViewHolder(binding, cache, coverMode)
    }
  }
}
