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
import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.data.library.Album_Table
import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.data.library.Track_Table
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.sql.language.Where
import rx.Single
import rx.SingleSubscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class AlbumEntryAdapter
@Inject
constructor(context: Activity) : RecyclerView.Adapter<AlbumEntryAdapter.ViewHolder>() {

  private val inflater: LayoutInflater
  private var data: FlowCursorList<Album>? = null
  private var mListener: MenuItemSelectedListener? = null

  init {
    inflater = LayoutInflater.from(context)
  }

  fun init(filter: String?) {
    if (data != null) {
      return
    }

    val query: Where<Album>
    if (TextUtils.isEmpty(filter)) {
      query = SQLite.select().from<Album>(Album::class.java)
          .orderBy(Album_Table.artist, true)
          .orderBy(Album_Table.album, true)
    } else {
      query = SQLite.select().from<Album>(Album::class.java)
          .leftOuterJoin<Track>(Track::class.java).on(Track_Table.album.withTable().eq(Album_Table.album.withTable())).where(
          Track_Table.artist.withTable().like('%' + filter as String + '%')).groupBy(
          Track_Table.artist.withTable()).orderBy(Album_Table.artist.withTable(), true).orderBy(
          Album_Table.album.withTable(), true)
    }

    Single.create { subscriber: SingleSubscriber<in FlowCursorList<Album>> ->
      val list = query.cursorList()
      subscriber.onSuccess(list)
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ albums ->
      data = albums
      notifyDataSetChanged()
    }) { throwable -> Timber.v(throwable, "failed to load the data") }
  }

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
    if (data == null) {
      return
    }

    data!!.refresh()
    notifyDataSetChanged()
  }

  override fun getItemCount(): Int {
    return if (data != null) data!!.count else 0
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    mListener = listener
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
}
