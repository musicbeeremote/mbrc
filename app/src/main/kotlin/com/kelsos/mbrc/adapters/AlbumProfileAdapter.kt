package com.kelsos.mbrc.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.domain.Track
import java.util.*

class AlbumProfileAdapter
@Inject constructor(context: Context) : RecyclerView.Adapter<AlbumProfileAdapter.ViewHolder>() {

  private val inflater: LayoutInflater
  private val data: MutableList<Track>
  private var listener: MenuItemSelectedListener? = null

  init {
    inflater = LayoutInflater.from(context)
    data = ArrayList<Track>()
  }

  fun updateData(data: List<Track>) {
    this.data.clear()
    this.data.addAll(data)
    notifyDataSetChanged()
  }

  private fun showPopup(view: View, track: Track) {
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

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.listitem_single, parent, false)
    val holder = ViewHolder(view)
    holder.overflow.setOnClickListener { v ->
      val position = holder.adapterPosition
      val track = data[position]
      showPopup(v, track)
    }
    holder.itemView.setOnClickListener { v ->
      if (listener != null) {
        val position = holder.adapterPosition
        val track = data[position]
        listener!!.onItemClicked(track)
      }
    }
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val track = data[position]
    holder.lineOne.text = track.title
  }

  override fun getItemCount(): Int {
    return data.size
  }

  fun setListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(menuItem: MenuItem, entry: Track)

    fun onItemClicked(track: Track)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.line_one) lateinit internal var lineOne: TextView
    @BindView(R.id.ui_item_context_indicator) lateinit internal var overflow: View

    init {
      ButterKnife.bind(this, itemView)
    }
  }
}
