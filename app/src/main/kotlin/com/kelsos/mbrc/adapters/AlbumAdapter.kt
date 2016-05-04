package com.kelsos.mbrc.adapters

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.utilities.FontUtils
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*

class AlbumAdapter
@Inject constructor(context: Context) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
  private val data: MutableList<Album>?
  private val robotoRegular: Typeface
  private var mListener: MenuItemSelectedListener? = null
  private val coversDir: File

  init {
    this.data = ArrayList<Album>()
    robotoRegular = FontUtils.getRobotoRegular(context)
    coversDir = File(context.filesDir, "covers")
  }

  fun updateData(data: List<Album>) {
    val previousSize = this.data!!.size
    this.data.addAll(data)
    notifyItemRangeInserted(previousSize, this.data.size - 1)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.griditem_album, parent, false)
    return ViewHolder(view, robotoRegular)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val album = data!![position]
    val itemView = holder.itemView
    val cover = album.cover

    holder.album.text = album.name
    holder.artist.text = album.artist

    holder.indicator.setOnClickListener {
      val popupMenu = PopupMenu(it.context, it)
      popupMenu.inflate(R.menu.popup_album)
      popupMenu.setOnMenuItemClickListener listener@{
        return@listener mListener?.onMenuItemSelected(it, album) ?: false
      }
      popupMenu.show()
    }

    itemView.setOnClickListener {
      mListener?.onItemClicked(album)
    }

    if (cover.isNotBlank()) {

      val image = File(coversDir, cover)

      Picasso.with(itemView.context)
          .load(image)
          .placeholder(R.drawable.ic_image_no_cover)
          .fit()
          .centerCrop()
          .tag(itemView.context)
          .into(holder.image)
    }
  }

  override fun getItemCount(): Int {
    return if (data == null) 0 else data.size
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    mListener = listener
  }

  fun clearData() {
    data!!.clear()
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(menuItem: MenuItem, album: Album): Boolean

    fun onItemClicked(album: Album)
  }

  class ViewHolder(itemView: View, typeface: Typeface) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.line_two) lateinit internal var artist: TextView
    @BindView(R.id.line_one) lateinit internal var album: TextView
    @BindView(R.id.ui_grid_image) lateinit internal var image: ImageView
    @BindView(R.id.ui_item_context_indicator) lateinit internal var indicator: LinearLayout

    init {
      ButterKnife.bind(this, itemView)
      album.typeface = typeface
      artist.typeface = typeface
    }
  }
}
