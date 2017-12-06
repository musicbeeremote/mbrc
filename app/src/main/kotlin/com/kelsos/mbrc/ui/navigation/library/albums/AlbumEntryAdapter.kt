package com.kelsos.mbrc.ui.navigation.library.albums

import android.app.Activity
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.albums.key
import com.kelsos.mbrc.databinding.UiListDualBinding
import com.kelsos.mbrc.extensions.count
import com.kelsos.mbrc.ui.widgets.SquareImageView
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.squareup.picasso.Picasso
import java.io.File
import javax.inject.Inject

class AlbumEntryAdapter
@Inject
constructor(context: Activity) : RecyclerView.Adapter<AlbumEntryAdapter.ViewHolder>() {

  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private var data: FlowCursorList<Album>? = null
  private var listener: MenuItemSelectedListener? = null
  private val cache = File(context.cacheDir, "covers")

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.ui_list_dual, parent, false)
    val holder = ViewHolder(view)
    holder.indicator.setOnClickListener {
      val popupMenu = PopupMenu(it.context, it)
      popupMenu.inflate(R.menu.popup_album)
      popupMenu.setOnMenuItemClickListener { menuItem ->
        val data = this.data ?: return@setOnMenuItemClickListener false
        val position = holder.bindingAdapterPosition.toLong()
        val album = data.getItem(position) ?: return@setOnMenuItemClickListener false
        listener?.onMenuItemSelected(menuItem, album)
        true
      }
      popupMenu.show()
    }

    holder.itemView.setOnClickListener {
      val data = this.data ?: return@setOnClickListener
      val position = holder.bindingAdapterPosition.toLong()
      val album = data.getItem(position) ?: return@setOnClickListener
      listener?.onItemClicked(album)
    }
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val data = this.data ?: return
    val item = data.getItem(position.toLong()) ?: return
    val (artist, album, _, _) = item
    holder.album.text = if (album.isNullOrBlank()) holder.emptyAlbum else album
    holder.artist.text = if (artist.isNullOrBlank()) holder.unknownArtist else artist
    Picasso.get()
      .load(File(cache, item.key()))
      .noFade()
      .config(Bitmap.Config.RGB_565)
      .error(R.drawable.ic_image_no_cover)
      .placeholder(R.drawable.ic_image_no_cover)
      .resizeDimen(R.dimen.list_album_size, R.dimen.list_album_size)
      .centerCrop()
      .into(holder.image)
  }

  fun refresh() {
    data?.refresh()
    notifyDataSetChanged()
  }

  override fun getItemCount(): Int = data.count()

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(menuItem: MenuItem, album: Album)

    fun onItemClicked(album: Album)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val artist: TextView
    val album: TextView
    val image: SquareImageView
    val indicator: LinearLayout
    val unknownArtist: String by lazy { itemView.context.getString(R.string.unknown_artist) }
    val emptyAlbum: String by lazy { itemView.context.getString(R.string.non_album_tracks) }

    init {
      val binding = UiListDualBinding.bind(itemView)
      artist = binding.lineTwo
      album = binding.lineOne
      indicator = binding.uiItemContextIndicator
      image = binding.cover
      image.isGone = false
    }
  }

  fun update(albums: FlowCursorList<Album>) {
    data = albums
    notifyDataSetChanged()
  }
}
