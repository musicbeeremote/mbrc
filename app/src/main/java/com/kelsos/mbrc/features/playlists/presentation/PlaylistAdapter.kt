package com.kelsos.mbrc.features.playlists.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.databinding.ListitemSingleBinding
import com.kelsos.mbrc.features.playlists.domain.Playlist
import com.kelsos.mbrc.ui.BindableViewHolder

class PlaylistAdapter : PagingDataAdapter<Playlist, PlaylistAdapter.ViewHolder>(
  PLAYLIST_COMPARATOR
) {
  private var playlistPressedListener: OnPlaylistPressedListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val viewHolder = ViewHolder.create(parent)
    viewHolder.onPress { position ->
      val item = getItem(position) ?: return@onPress
      playlistPressedListener?.playlistPressed(item.url)
    }
    return viewHolder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    getItem(position)?.let { playlist ->
      holder.bindTo(playlist)
    }
  }

  fun setPlaylistPressedListener(playlistPressedListener: OnPlaylistPressedListener) {
    this.playlistPressedListener = playlistPressedListener
  }

  interface OnPlaylistPressedListener {
    fun playlistPressed(path: String)
  }

  class ViewHolder(binding: ListitemSingleBinding) : BindableViewHolder<Playlist>(binding) {
    private val name: TextView = binding.lineOne
    private val context: ImageView = binding.uiItemContextIndicator

    override fun bindTo(item: Playlist) {
      context.isVisible = false
      name.text = item.name
    }

    override fun clear() {
      name.text = ""
    }

    companion object {
      fun create(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListitemSingleBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
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
