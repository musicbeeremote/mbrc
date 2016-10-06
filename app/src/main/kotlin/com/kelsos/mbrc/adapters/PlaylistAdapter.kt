package com.kelsos.mbrc.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.data.Playlist
import java.util.*
import javax.inject.Inject

class PlaylistAdapter
@Inject constructor(context: Activity) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

  private val inflater: LayoutInflater
  private var data: MutableList<Playlist>
  private var playlistPressedListener: OnPlaylistPressedListener? = null

  init {
    inflater = LayoutInflater.from(context)
    data = ArrayList<Playlist>()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.listitem_single, parent, false)
    val viewHolder = ViewHolder(view)

    viewHolder.itemView.setOnClickListener {
      playlistPressedListener?.playlistPressed(data[viewHolder.adapterPosition].url)
    }
    return viewHolder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val playlist = data[holder.adapterPosition]
    holder.name.text = playlist.name
    holder.context.visibility = View.GONE
  }

  override fun getItemCount(): Int {
    return data.size
  }

  fun update(playlist: List<Playlist>) {
    this.data.clear()
    this.data.addAll(playlist)
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
