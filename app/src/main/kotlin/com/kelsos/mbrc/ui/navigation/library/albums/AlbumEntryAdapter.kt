package com.kelsos.mbrc.ui.navigation.library.albums

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
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.albums.key
import com.kelsos.mbrc.databinding.UiListDualBinding
import com.kelsos.mbrc.ui.navigation.library.popup
import com.kelsos.mbrc.ui.widgets.SquareImageView
import com.squareup.picasso.Picasso
import java.io.File
import javax.inject.Inject

class AlbumEntryAdapter
@Inject
constructor() : PagingDataAdapter<Album, AlbumEntryAdapter.ViewHolder>(DIFF_CALLBACK) {

  private var listener: MenuItemSelectedListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val holder = ViewHolder.create(parent)
    holder.onIndicatorClick { view, position ->
      view.popup(R.menu.popup_album) { id ->
        val album = getItem(position) ?: return@popup
        listener?.onMenuItemSelected(id, album)
      }
    }

    holder.onPress { position ->
      val album = getItem(position) ?: return@onPress
      listener?.onItemClicked(album)
    }
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val album = getItem(position)

    if (album != null) {
      holder.bindTo(album)
    } else {
      holder.clear()
    }
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Album>() {
      override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem.album == newItem.album &&
          oldItem.artist == newItem.artist
      }
    }
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(@IdRes itemId: Int, album: Album)

    fun onItemClicked(album: Album)
  }

  class ViewHolder(
    binding: UiListDualBinding,
    private val cache: File
  ) : RecyclerView.ViewHolder(binding.root) {
    private val artist: TextView = binding.lineTwo
    private val album: TextView = binding.lineOne
    private val image: SquareImageView = binding.cover
    private val indicator: ImageView = binding.overflowMenu
    private val unknownArtist: String by lazy {
      itemView.context.getString(R.string.unknown_artist)
    }
    private val emptyAlbum: String by lazy { itemView.context.getString(R.string.non_album_tracks) }

    init {
      image.isGone = false
    }

    fun bindTo(album: Album) {
      val title = album.album
      val artist = album.artist
      this.album.text = if (title.isBlank()) emptyAlbum else title
      this.artist.text = if (artist.isBlank()) unknownArtist else artist

      Picasso.get()
        .load(File(cache, album.key()))
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

    fun onPress(onPress: (position: Int) -> Unit) {
      itemView.setOnClickListener { onPress(bindingAdapterPosition) }
    }

    fun clear() {
      album.text = ""
      artist.text = ""
      image.setImageResource(R.drawable.ic_image_no_cover)
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
