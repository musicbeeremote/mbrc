package com.kelsos.mbrc.ui.navigation.playlists

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.playlists.Playlist
import kotterknife.bindView
import javax.inject.Inject

class PlaylistAdapter
@Inject constructor(context: Activity) : androidx.recyclerview.widget.RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private var data: List<Playlist>? = null
  private var playlistPressedListener: OnPlaylistPressedListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.listitem_single, parent, false)
    val viewHolder = ViewHolder(view)

    viewHolder.itemView.setOnClickListener {
      val path = data?.get(viewHolder.adapterPosition)?.url
      path?.let {
        playlistPressedListener?.playlistPressed(it)
      }
    }
    return viewHolder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val playlist = data?.get(holder.adapterPosition)
    playlist?.let {
      holder.name.text = playlist.name
    }
    holder.context.visibility = View.GONE
  }

  override fun getItemCount(): Int = data?.size?.toInt() ?: 0

  fun update(cursor: List<Playlist>) {
    this.data = cursor
    notifyDataSetChanged()
  }

  fun setPlaylistPressedListener(playlistPressedListener: OnPlaylistPressedListener) {
    this.playlistPressedListener = playlistPressedListener
  }

  interface OnPlaylistPressedListener {
    fun playlistPressed(path: String)
  }

  class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
    val name: TextView by bindView(R.id.line_one)
    val context: ImageView by bindView(R.id.ui_item_context_indicator)
  }
}