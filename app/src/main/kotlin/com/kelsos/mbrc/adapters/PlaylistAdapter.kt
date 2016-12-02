package com.kelsos.mbrc.adapters

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.domain.Playlist
import com.kelsos.mbrc.ui.navigation.playlists.tracks.PlaylistTrackActivity
import com.raizlabs.android.dbflow.list.FlowCursorList
import javax.inject.Inject

class PlaylistAdapter
@Inject constructor(context: Activity) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

  private val inflater: LayoutInflater
  private var data: FlowCursorList<Playlist>? = null
  private var playlistPressedListener: OnPlaylistPressedListener? = null

  init {
    inflater = LayoutInflater.from(context)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.listitem_single, parent, false)
    val viewHolder = ViewHolder(view)

    viewHolder.itemView.setOnClickListener { view: View? ->
      val playlist = data?.getItem(viewHolder.adapterPosition.toLong())
      playlist?.let {
        startPlaylistActivity(it, view)
      }

    }
    return viewHolder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val playlist = data?.getItem(holder.adapterPosition.toLong())
    playlist?.let {
      holder.name.text = playlist.name
    }
    holder.context.setOnClickListener {
      val menu = PopupMenu(it.context, it)
      menu.inflate(R.menu.popup_playlist)
      menu.show()

      menu.setOnMenuItemClickListener listener@ {
        if (it.itemId == R.id.playlist_tracks) {
          //startPlaylistActivity(playlist, it)
          return@listener true
        } else if (it.itemId == R.id.playlist_play) {
          val adapterPosition = holder.adapterPosition.toLong()
          val selectedPlaylist = data?.getItem(adapterPosition)
          //todo listener return
          return@listener true
        }
        false
      }
    }

  }

  override fun getItemCount(): Int {
    return data?.count ?: 0
  }

  private fun startPlaylistActivity(playlist: Playlist, v: View?) {
    if (v == null) {
      return
    }

    val bundle = Bundle()
    bundle.putString(PlaylistTrackActivity.NAME, playlist.name)
    bundle.putLong(PlaylistTrackActivity.ID, playlist.id)
    val intent = Intent(v.context, PlaylistTrackActivity::class.java)
    intent.putExtras(bundle)
    v.context.startActivity(intent)
  }

  fun update(cursor: FlowCursorList<Playlist>) {
    this.data = cursor
    notifyDataSetChanged()
  }

  fun setPlaylistPressedListener(playlistPressedListener: OnPlaylistPressedListener) {
    this.playlistPressedListener = playlistPressedListener
  }

  interface OnPlaylistPressedListener {
    fun playlistPressed(path: String)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.line_one) lateinit var name: TextView
    @BindView(R.id.ui_item_context_indicator) lateinit var context: LinearLayout

    init {
      ButterKnife.bind(this, itemView)
    }
  }
}
