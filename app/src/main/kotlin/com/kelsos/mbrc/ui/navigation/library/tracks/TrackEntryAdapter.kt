package com.kelsos.mbrc.ui.navigation.library.tracks

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.view.isGone
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.key
import com.kelsos.mbrc.databinding.UiListDualBinding
import com.kelsos.mbrc.ui.navigation.library.popup
import com.squareup.picasso.Picasso
import java.io.File
import javax.inject.Inject

class TrackEntryAdapter
@Inject
constructor() : PagingDataAdapter<Track, TrackEntryAdapter.ViewHolder>(DIFF_CALLBACK) {

  private var listener: MenuItemSelectedListener? = null
  private var coverMode: Boolean = false

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  fun setCoverMode(coverMode: Boolean) {
    this.coverMode = coverMode
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val holder = ViewHolder.create(parent)
    holder.onIndicatorClick { view, position ->
      view.popup(R.menu.popup_track) { id ->
        val track = getItem(position) ?: return@popup
        listener?.onMenuItemSelected(id, track)
      }
    }

    holder.onPress { position ->
      val track = getItem(position) ?: return@onPress
      listener?.onItemClicked(track)
    }
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val track = getItem(position)
    if (track != null) {
      holder.bindTo(track, coverMode)
    } else {
      holder.clear()
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Track>() {
      override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
      }
    }
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(@IdRes itemId: Int, track: Track)

    fun onItemClicked(track: Track)
  }

  class ViewHolder(
    binding: UiListDualBinding,
    private val cache: File
  ) : RecyclerView.ViewHolder(binding.root) {
    private val artist: TextView = binding.lineTwo
    private val title: TextView = binding.lineOne
    private val indicator: ImageView = binding.overflowMenu
    private val unknownArtist: String by lazy {
      itemView.context.getString(R.string.unknown_artist)
    }
    private val image: ImageView = binding.cover

    fun bindTo(track: Track, coverMode: Boolean = false) {
      title.text = track.title
      artist.text = if (track.artist.isBlank()) unknownArtist else track.artist
      image.isGone = !coverMode

      Picasso.get()
        .load(File(cache, track.key()))
        .noFade()
        .config(Bitmap.Config.RGB_565)
        .error(R.drawable.ic_image_no_cover)
        .placeholder(R.drawable.ic_image_no_cover)
        .resizeDimen(R.dimen.list_album_size, R.dimen.list_album_size)
        .centerCrop()
        .into(image)
    }

    fun clear() {
      title.text = ""
      artist.text = ""
      image.setImageResource(R.drawable.ic_image_no_cover)
    }

    fun onIndicatorClick(onClick: (view: View, position: Int) -> Unit) {
      indicator.setOnClickListener { onClick(it, bindingAdapterPosition) }
    }

    fun onPress(onPress: (position: Int) -> Unit) {
      itemView.setOnClickListener { onPress(bindingAdapterPosition) }
    }

    companion object {
      fun create(parent: ViewGroup): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.ui_list_dual, parent, false)
        val cache = File(parent.context.cacheDir, "covers")
        val binding = UiListDualBinding.bind(view)
        return ViewHolder(binding, cache)
      }
    }
  }
}
