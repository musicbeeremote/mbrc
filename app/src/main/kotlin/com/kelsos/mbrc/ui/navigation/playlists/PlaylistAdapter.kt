package com.kelsos.mbrc.ui.navigation.playlists

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.playlists.Playlist
import com.kelsos.mbrc.databinding.ListitemSingleBinding
import javax.inject.Inject

class PlaylistAdapter
@Inject
constructor(context: Activity) : PagingDataAdapter<Playlist, PlaylistAdapter.ViewHolder>(
  PLAYLIST_COMPARATOR
) {

  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private var playlistPressedListener: OnPlaylistPressedListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.listitem_single, parent, false)
    val viewHolder = ViewHolder(view)

    viewHolder.itemView.setOnClickListener {
      val path = getItem(viewHolder.bindingAdapterPosition)?.url
      path?.let {
        playlistPressedListener?.playlistPressed(it)
      }
    }
    return viewHolder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val playlist = getItem(holder.bindingAdapterPosition)
    playlist?.let {
      holder.name.text = playlist.name
    }
    holder.context.visibility = View.GONE
  }

  fun setPlaylistPressedListener(playlistPressedListener: OnPlaylistPressedListener) {
    this.playlistPressedListener = playlistPressedListener
  }

  interface OnPlaylistPressedListener {
    fun playlistPressed(path: String)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView
    val context: ImageView

    init {
      val binding = ListitemSingleBinding.bind(itemView)
      name = binding.lineOne
      context = binding.uiItemContextIndicator
    }
  }

  companion object {
    val PLAYLIST_COMPARATOR = object : DiffUtil.ItemCallback<Playlist>() {
      override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.name == newItem.name
      }
    }
  }
}
