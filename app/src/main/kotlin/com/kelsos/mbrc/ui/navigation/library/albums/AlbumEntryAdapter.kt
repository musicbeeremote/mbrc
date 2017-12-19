package com.kelsos.mbrc.ui.navigation.library.albums

import android.app.Activity
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.albums.key
import com.kelsos.mbrc.databinding.UiListDualBinding
import com.kelsos.mbrc.ui.widgets.SquareImageView
import com.squareup.picasso.Picasso
import java.io.File
import javax.inject.Inject

class AlbumEntryAdapter
@Inject
constructor(context: Activity) : RecyclerView.Adapter<AlbumEntryAdapter.ViewHolder>() {

  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private var data: List<Album> = emptyList()
  private var listener: MenuItemSelectedListener? = null
  private val cache = File(context.cacheDir, "covers")

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.ui_list_dual, parent, false)
    val holder = ViewHolder(view, cache)
    holder.setIndicatorOnClickListener {
      val popupMenu = PopupMenu(it.context, it)
      popupMenu.inflate(R.menu.popup_album)
      popupMenu.setOnMenuItemClickListener { menuItem ->
        val data = this.data
        val position = holder.bindingAdapterPosition
        val album = data[position]
        listener?.onMenuItemSelected(menuItem, album)
        true
      }
      popupMenu.show()
    }

    holder.itemView.setOnClickListener {
      val position = holder.bindingAdapterPosition
      val album = data[position]
      listener?.onItemClicked(album)
    }
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val album = data[holder.bindingAdapterPosition]
    holder.bind(album)
  }

  override fun getItemCount(): Int = data.size

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(menuItem: MenuItem, album: Album)

    fun onItemClicked(album: Album)
  }

  class ViewHolder(itemView: View, private val cache: File) : RecyclerView.ViewHolder(itemView) {
    private val artist: TextView
    private val album: TextView
    private val image: SquareImageView
    private val indicator: ImageView
    private val unknownArtist: String by lazy {
      itemView.context.getString(R.string.unknown_artist)
    }
    private val emptyAlbum: String by lazy { itemView.context.getString(R.string.non_album_tracks) }

    init {
      val binding = UiListDualBinding.bind(itemView)
      artist = binding.lineTwo
      album = binding.lineOne
      indicator = binding.overflowMenu
      image = binding.cover
      image.isGone = false
    }

    fun bind(album: Album) {
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

    fun setIndicatorOnClickListener(listener: (view: View) -> Unit) {
      indicator.setOnClickListener { listener(it) }
    }
  }

  fun update(albums: List<Album>) {
    data = albums
    notifyDataSetChanged()
  }
}
