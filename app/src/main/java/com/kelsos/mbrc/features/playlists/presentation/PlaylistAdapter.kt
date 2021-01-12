package com.kelsos.mbrc.features.playlists.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.playlists.domain.Playlist
import com.kelsos.mbrc.ui.BindableViewHolder
import com.kelsos.mbrc.ui.OnViewItemPressed

class PlaylistAdapter : ListAdapter<Playlist, PlaylistAdapter.ViewHolder>(
  DIFF_CALLBACK
) {

  private var playlistPressedListener: OnPlaylistPressedListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder.create(parent) { position ->
      playlistPressedListener?.playlistPressed(getItem(position).url)
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bindTo(getItem(position))
  }

  fun setPlaylistPressedListener(playlistPressedListener: OnPlaylistPressedListener) {
    this.playlistPressedListener = playlistPressedListener
  }

  interface OnPlaylistPressedListener {
    fun playlistPressed(path: String)
  }

  class ViewHolder(itemView: View) : BindableViewHolder<Playlist>(itemView) {

    private val name: TextView = itemView.findViewById(R.id.line_one)
    private val context: ImageView = itemView.findViewById(R.id.ui_item_context_indicator)

    override fun bindTo(item: Playlist) {
      context.isVisible = false
      name.text = item.name
    }

    override fun clear() {
      name.text = ""
    }

    companion object {
      fun create(parent: ViewGroup, onHolderItemPressed: OnViewItemPressed): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.listitem_single, parent, false)
        return ViewHolder(view).apply {
          itemView.setOnClickListener { onHolderItemPressed(adapterPosition) }
        }
      }
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Playlist>() {
      override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
      }
    }
  }
}
