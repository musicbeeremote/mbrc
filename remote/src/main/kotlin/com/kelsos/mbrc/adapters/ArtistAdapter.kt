package com.kelsos.mbrc.adapters

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.utilities.FontUtils
import java.util.*

class ArtistAdapter
@Inject
constructor(context: Context) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {
  private val data: MutableList<Artist>?
  private val robotoRegular: Typeface
  private var mListener: MenuItemSelectedListener? = null

  init {
    this.data = ArrayList<Artist>()
    robotoRegular = FontUtils.getRobotoRegular(context)
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    mListener = listener
  }

  fun updateData(data: List<Artist>) {
    this.data!!.addAll(data)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.ui_list_single, parent, false)
    return ViewHolder(view, robotoRegular)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val entry = data!![position]
    holder.title.text = entry.name

    holder.indicator.setOnClickListener { v ->
      val popupMenu = PopupMenu(v.context, v)
      popupMenu.inflate(R.menu.popup_artist)
      popupMenu.setOnMenuItemClickListener listener@{ menuItem ->
        if (mListener != null) {
          mListener!!.onMenuItemSelected(menuItem, entry)
          return@listener true
        }
        false
      }
      popupMenu.show()
    }

    holder.itemView.setOnClickListener { v ->
      if (mListener != null) {
        mListener!!.onItemClicked(entry)
      }
    }
  }

  /**
   * Returns the total number of items in the data set hold by the adapter.

   * @return The total number of items in this adapter.
   */
  override fun getItemCount(): Int {
    return if (data == null) 0 else data.size
  }

  fun clear() {
    data!!.clear()
    notifyDataSetChanged()
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(menuItem: MenuItem, entry: Artist)
    fun onItemClicked(artist: Artist)
  }

  class ViewHolder(itemView: View, typeface: Typeface) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.line_one) internal lateinit var title: TextView
    @BindView(R.id.ui_item_context_indicator) internal lateinit var indicator: LinearLayout

    init {
      ButterKnife.bind(this, itemView)
      title.typeface = typeface
    }
  }
}
