package com.kelsos.mbrc.adapters

import android.app.Activity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
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
import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller.BubbleTextGetter
import com.raizlabs.android.dbflow.list.FlowCursorList
import javax.inject.Inject

class AlbumEntryAdapter
@Inject
constructor(context: Activity) : RecyclerView.Adapter<AlbumEntryAdapter.ViewHolder>(), BubbleTextGetter {

  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private var data: FlowCursorList<Album>? = null
  private var mListener: MenuItemSelectedListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.ui_list_dual, parent, false)
    val holder = ViewHolder(view)
    holder.indicator.setOnClickListener {
      val popupMenu = PopupMenu(it.context, it)
      popupMenu.inflate(R.menu.popup_album)
      popupMenu.setOnMenuItemClickListener {
        mListener?.onMenuItemSelected(it, data!!.getItem(holder.adapterPosition.toLong()))
        true
      }
      popupMenu.show()
    }

    holder.itemView.setOnClickListener { v ->
      mListener!!.onItemClicked(data!!.getItem(holder.adapterPosition.toLong()))
    }
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val entry = data!!.getItem(position.toLong())
    holder.album.text = if (TextUtils.isEmpty(entry.album)) holder.emptyAlbum else entry.album
    holder.artist.text = if (TextUtils.isEmpty(entry.artist)) holder.unknownArtist else entry.artist
  }

  fun refresh() {
    data?.refresh()
    notifyDataSetChanged()
  }

  override fun getItemCount(): Int {
    return data?.count ?: 0
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    mListener = listener
  }

  override fun getTextToShowInBubble(pos: Int): String {
    val artist = data?.getItem(pos.toLong())?.artist
    if (artist != null && artist.isNotBlank()) {
      return artist.substring(0, 1)
    }
    return "-"
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(menuItem: MenuItem, entry: Album)

    fun onItemClicked(album: Album)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.line_two) lateinit var artist: TextView
    @BindView(R.id.line_one) lateinit var album: TextView
    @BindView(R.id.ui_item_context_indicator) lateinit var indicator: LinearLayout
    @BindString(R.string.unknown_artist) lateinit var unknownArtist: String
    @BindString(R.string.non_album_tracks) lateinit var emptyAlbum: String

    init {
      ButterKnife.bind(this, itemView)
    }
  }

  fun update(albums: FlowCursorList<Album>) {
    data = albums
    notifyDataSetChanged()
  }
}
