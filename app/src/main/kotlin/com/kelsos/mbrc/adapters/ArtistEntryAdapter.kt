package com.kelsos.mbrc.adapters

import android.app.Activity
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindString
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.domain.Artist
import com.raizlabs.android.dbflow.list.FlowCursorList
import javax.inject.Inject

class ArtistEntryAdapter
@Inject constructor(context: Activity) : RecyclerView.Adapter<ArtistEntryAdapter.ViewHolder>() {
  private val inflater: LayoutInflater
  private var data: FlowCursorList<Artist>? = null
  private var mListener: MenuItemSelectedListener? = null

  init {
    inflater = LayoutInflater.from(context)
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    mListener = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.listitem_single, parent, false)
    val holder = ViewHolder(view)

    holder.indicator.setOnClickListener {
      val popupMenu = PopupMenu(it.context, it)
      popupMenu.inflate(R.menu.popup_artist)
      popupMenu.setOnMenuItemClickListener {
        mListener?.onMenuItemSelected(it, data!!.getItem(holder.adapterPosition.toLong()))
        true
      }
      popupMenu.show()
    }

    holder.itemView.setOnClickListener {
      mListener?.onItemClicked(data!!.getItem(holder.adapterPosition.toLong()))
    }
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val entry = data!!.getItem(position.toLong())
    holder.title.text = if (TextUtils.isEmpty(entry.name)) holder.empty else entry.name
  }

  /**
   * Returns the total number of items in the data set hold by the adapter.

   * @return The total number of items in this adapter.
   */
  override fun getItemCount(): Int {
    return data?.count ?: 0
  }

  fun refresh() {
    data?.refresh()
    notifyDataSetChanged()
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(menuItem: MenuItem, entry: Artist)

    fun onItemClicked(artist: Artist)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.line_one) lateinit var title: TextView
    @BindView(R.id.ui_item_context_indicator) lateinit var indicator: LinearLayout
    @BindString(R.string.empty) lateinit var empty: String

    init {
      ButterKnife.bind(this, itemView)
    }
  }

  fun update(data: FlowCursorList<Artist>) {
    this.data = data
    notifyDataSetChanged()
  }
}
