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
import javax.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.ui.SquareImageView
import com.kelsos.mbrc.utilities.FontUtils
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*

class TrackEntryAdapter
@Inject constructor(context: Context) : RecyclerView.Adapter<TrackEntryAdapter.ViewHolder>() {
  private val coversDir: File
  private var data: MutableList<Track>? = null
  private val robotoRegular: Typeface
  private var mListener: MenuItemSelectedListener? = null

  init {
    this.data = ArrayList<Track>()
    robotoRegular = FontUtils.getRobotoRegular(context)
    coversDir = File(context.filesDir, "covers")
  }

  fun updateData(data: MutableList<Track>) {
    this.data = data
    notifyDataSetChanged()
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    mListener = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.ui_list_library_track, parent, false)
    return ViewHolder(view, robotoRegular)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val entry = data!![position]

    holder.title.text = entry.title
    holder.artist.text = entry.artist
    val cover = entry.cover
    if (cover.isNotBlank()) {

      val image = File(coversDir, cover)

      Picasso.with(holder.itemView.context)
          .load(image)
          .placeholder(R.drawable.ic_image_no_cover)
          .fit()
          .centerCrop()
          .tag(holder.itemView.context)
          .into(holder.cover)
    }

    holder.indicator.setOnClickListener {
      val popupMenu = PopupMenu(it.context, it)
      popupMenu.inflate(R.menu.popup_track)
      popupMenu.setOnMenuItemClickListener listener@{ menuItem ->
        if (mListener != null) {
          mListener!!.onMenuItemSelected(menuItem, entry)
          return@listener true
        }
        false
      }
      popupMenu.show()
    }

    holder.itemView.setOnClickListener {
      mListener?.onItemClicked(entry)
    }
  }

  /**
   * Returns the total number of items in the data set hold by the adapter.

   * @return The total number of items in this adapter.
   */
  override fun getItemCount(): Int {
    return data!!.size
  }

  fun clearData() {
    data!!.clear()
  }

  fun appendData(tracks: List<Track>) {
    val previousSize = data!!.size
    data!!.addAll(tracks)
    notifyItemRangeInserted(previousSize, data!!.size - 1)
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(item: MenuItem, track: Track)

    fun onItemClicked(track: Track)
  }

  class ViewHolder(itemView: View, typeface: Typeface) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.track_cover) lateinit var cover: SquareImageView
    @BindView(R.id.line_one) lateinit var artist: TextView
    @BindView(R.id.line_two) lateinit var title: TextView
    @BindView(R.id.ui_item_context_indicator) lateinit var indicator: LinearLayout

    init {
      ButterKnife.bind(this, itemView)
      title.typeface = typeface
      artist.typeface = typeface
    }
  }
}
