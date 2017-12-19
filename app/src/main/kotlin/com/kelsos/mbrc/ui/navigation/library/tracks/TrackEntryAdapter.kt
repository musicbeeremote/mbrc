package com.kelsos.mbrc.ui.navigation.library.tracks

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
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.key
import com.kelsos.mbrc.databinding.UiListDualBinding
import com.squareup.picasso.Picasso
import java.io.File
import javax.inject.Inject

class TrackEntryAdapter
@Inject
constructor(context: Activity) : RecyclerView.Adapter<TrackEntryAdapter.ViewHolder>() {
  private var data: List<Track>? = null
  private var listener: MenuItemSelectedListener? = null
  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private val cache = File(context.cacheDir, "covers")
  private var coverMode: Boolean = false

  fun setCoverMode(enabled: Boolean) {
    coverMode = enabled
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.ui_list_dual, parent, false)
    val holder = ViewHolder(view)
    holder.indicator.setOnClickListener { createPopup(view, holder) }

    holder.itemView.setOnClickListener {
      val position = holder.bindingAdapterPosition
      val track = data?.get(position) ?: return@setOnClickListener
      listener?.onItemClicked(track)
    }
    return holder
  }

  private fun createPopup(it: View, holder: ViewHolder) {
    val popupMenu = PopupMenu(it.context, it)
    popupMenu.inflate(R.menu.popup_track)
    popupMenu.setOnMenuItemClickListener { menuItem ->
      val position = holder.bindingAdapterPosition
      val track = data?.get(position) ?: return@setOnMenuItemClickListener false
      listener?.onMenuItemSelected(menuItem, track)
      true
    }
    popupMenu.show()
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val entry = data?.get(holder.bindingAdapterPosition) ?: return
    holder.title.text = entry.title
    val artist = entry.artist
    holder.artist.text = if (artist.isBlank()) holder.unknownArtist else artist
    holder.image.isGone = !coverMode

    Picasso.get()
      .load(File(cache, entry.key()))
      .noFade()
      .config(Bitmap.Config.RGB_565)
      .error(R.drawable.ic_image_no_cover)
      .placeholder(R.drawable.ic_image_no_cover)
      .resizeDimen(R.dimen.list_album_size, R.dimen.list_album_size)
      .centerCrop()
      .into(holder.image)
  }

  override fun getItemCount(): Int = data?.size ?: 0

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(menuItem: MenuItem, track: Track)

    fun onItemClicked(track: Track)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val artist: TextView
    val title: TextView
    val indicator: ImageView
    val unknownArtist: String by lazy { itemView.context.getString(R.string.unknown_artist) }
    val image: ImageView

    init {
      val binding = UiListDualBinding.bind(itemView)
      artist = binding.lineTwo
      title = binding.lineOne
      indicator = binding.overflowMenu
      image = binding.cover
    }
  }

  fun update(cursor: List<Track>) {
    data = cursor
    notifyDataSetChanged()
  }
}
