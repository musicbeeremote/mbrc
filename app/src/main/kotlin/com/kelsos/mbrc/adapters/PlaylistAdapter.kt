package com.kelsos.mbrc.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import javax.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.domain.Playlist
import com.kelsos.mbrc.ui.activities.PlaylistTrackActivity
import com.kelsos.mbrc.utilities.FontUtils
import java.util.*

class PlaylistAdapter
@Inject constructor(context: Context) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
  private val inflater: LayoutInflater
  private val robotoRegular: Typeface
  private val data: MutableList<Playlist>
  private var onPlaylistPlayPressedListener: OnPlaylistPlayPressedListener? = null

  init {
    robotoRegular = FontUtils.getRobotoRegular(context)
    inflater = LayoutInflater.from(context)
    data = ArrayList<Playlist>()
  }

  fun setOnPlaylistPlayPressedListener(onPlaylistPlayPressedListener: OnPlaylistPlayPressedListener) {
    this.onPlaylistPlayPressedListener = onPlaylistPlayPressedListener
  }

  fun updateData(data: List<Playlist>) {
    this.data.clear()
    this.data.addAll(data)
    notifyDataSetChanged()
  }

  override fun getItemId(position: Int): Long {
    return data[position].id
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.listitem_single, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val playlist = data[position]
    holder.lineOne.typeface = robotoRegular
    holder.lineOne.text = playlist.name
    holder.itemView.setOnClickListener { v1 -> startPlaylistActivity(data[holder.adapterPosition], v1) }

    holder.overflow.setOnClickListener { v ->
      val menu = PopupMenu(v.context, v)
      menu.inflate(R.menu.popup_playlist)
      menu.show()

      menu.setOnMenuItemClickListener listener@{
        if (it.itemId == R.id.playlist_tracks) {
          startPlaylistActivity(playlist, v)
          return@listener true
        } else if (it.itemId == R.id.playlist_play) {
            onPlaylistPlayPressedListener?.playlistPlayPressed(data[holder.adapterPosition], holder.adapterPosition)
          return@listener true
        }
        false
      }
    }
  }

  private fun startPlaylistActivity(playlist: Playlist, v: View) {
    val bundle = Bundle()
    bundle.putString(PlaylistTrackActivity.NAME, playlist.name)
    bundle.putLong(PlaylistTrackActivity.ID, playlist.id)
    val intent = Intent(v.context, PlaylistTrackActivity::class.java)
    intent.putExtras(bundle)
    v.context.startActivity(intent)
  }

  override fun getItemCount(): Int {
    return data.size
  }

  interface OnPlaylistPlayPressedListener {
    fun playlistPlayPressed(playlist: Playlist, position: Int)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.line_one) lateinit var lineOne: TextView
    @BindView(R.id.ui_item_context_indicator) lateinit var overflow: LinearLayout

    init {
      ButterKnife.bind(this, itemView)
    }
  }
}
