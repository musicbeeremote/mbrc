package com.kelsos.mbrc.adapters

import android.content.Context
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import javax.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.domain.PlaylistTrack
import java.util.*

class PlaylistAdapter
@Inject
constructor(context: Context) : RecyclerView.Adapter<PlaylistAdapter.PlaylistTrackViewHolder>() {

  private val inflater: LayoutInflater
  private var listener: MenuItemSelectedListener? = null
  private val data: MutableList<PlaylistTrack>

  init {
    inflater = LayoutInflater.from(context)
    data = ArrayList<PlaylistTrack>()
  }

  private fun showPopup(view: View, track: PlaylistTrack) {
    val popupMenu = PopupMenu(view.context, view)
    popupMenu.inflate(R.menu.popup_track)
    popupMenu.setOnMenuItemClickListener listener@{ menuItem ->
      if (listener != null) {
        listener!!.onMenuItemSelected(menuItem, track)
        return@listener true
      }
      false
    }
    popupMenu.show()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistTrackViewHolder {
    val view = inflater.inflate(R.layout.ui_list_dual, parent, false)
    return PlaylistTrackViewHolder(view)
  }

  override fun onBindViewHolder(holder: PlaylistTrackViewHolder, position: Int) {
    val track = data[position]
    holder.lineOne.text = track.title
    holder.lineTwo.text = track.artist
    holder.overflow.setOnClickListener { showPopup(it, track) }
    holder.itemView.setOnClickListener { listener?.onItemClicked(track) }
  }

  override fun getItemCount(): Int {
    return data.size
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener): PlaylistAdapter {
    this.listener = listener
    return this
  }

  fun update(data: List<PlaylistTrack>) {
    this.data.clear()
    this.data.addAll(data)
    notifyDataSetChanged()
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(menuItem: MenuItem, track: PlaylistTrack)

    fun onItemClicked(track: PlaylistTrack)
  }

  class PlaylistTrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.line_one) lateinit var lineOne: TextView
    @BindView(R.id.line_two) lateinit var lineTwo: TextView
    @BindView(R.id.ui_item_context_indicator) lateinit  var overflow: View

    init {
      ButterKnife.bind(this, itemView)
    }
  }
}
