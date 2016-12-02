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
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.ui.widgets.SquareImageView
import com.raizlabs.android.dbflow.list.FlowCursorList
import javax.inject.Inject

class TrackEntryAdapter
@Inject
constructor(context: Activity) : RecyclerView.Adapter<TrackEntryAdapter.ViewHolder>() {
  private var data: FlowCursorList<Track>? = null
  private var mListener: MenuItemSelectedListener? = null
  private val inflater: LayoutInflater

  init {
    inflater = LayoutInflater.from(context)
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    mListener = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.ui_list_dual, parent, false)
    val holder = ViewHolder(view)
    holder.indicator.setOnClickListener {
      val popupMenu = PopupMenu(it.context, it)
      popupMenu.inflate(R.menu.popup_track)
      popupMenu.setOnMenuItemClickListener { menuItem ->
        if (mListener == null) {
          return@setOnMenuItemClickListener false
        }
        mListener!!.onMenuItemSelected(menuItem, data!!.getItem(holder.adapterPosition.toLong()))
        true
      }
      popupMenu.show()
    }


    holder.itemView.setOnClickListener { v ->
      if (mListener == null) {
        return@setOnClickListener
      }
      mListener!!.onItemClicked(data!!.getItem(holder.adapterPosition.toLong()))
    }
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val entry = data!!.getItem(position.toLong())
    holder.title.text = entry.title
    val artist = entry.artist
    holder.artist.text = if (TextUtils.isEmpty(artist)) holder.unknownArtist else artist
    val cover = entry.cover
    if (cover.isNotBlank()) {

//      val image = File(coversDir, cover)
//
//      Picasso.with(holder.itemView.context)
//          .load(image)
//          .placeholder(R.drawable.ic_image_no_cover)
//          .fit()
//          .centerCrop()
//          .tag(holder.itemView.context)
//          .into(holder.cover)
    }
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
    fun onMenuItemSelected(menuItem: MenuItem, entry: Track)

    fun onItemClicked(track: Track)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.track_cover) lateinit var cover: SquareImageView
    @BindView(R.id.line_two) lateinit var artist: TextView
    @BindView(R.id.line_one) lateinit var title: TextView
    @BindView(R.id.ui_item_context_indicator) lateinit var indicator: LinearLayout
    @BindString(R.string.unknown_artist) lateinit var unknownArtist: String

    init {
      ButterKnife.bind(this, itemView)
    }
  }

  fun update(cursor: FlowCursorList<Track>) {
    data = cursor
    notifyDataSetChanged()
  }
}
