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
import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.data.library.Artist_Table
import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.data.library.Track_Table
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.sql.language.Where
import rx.Emitter
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class ArtistEntryAdapter
@Inject constructor(context: Activity) : RecyclerView.Adapter<ArtistEntryAdapter.ViewHolder>() {
  private val inflater: LayoutInflater
  private var data: FlowCursorList<Artist>? = null
  private var mListener: MenuItemSelectedListener? = null

  init {
    inflater = LayoutInflater.from(context)
  }

  fun init(filter: String?) {
    if (data != null) {
      return
    }

    val query: Where<Artist>

    if (TextUtils.isEmpty(filter)) {
      query = SQLite.select().from<Artist>(Artist::class.java).orderBy(Artist_Table.artist, true)
    } else {
      query = SQLite.select().distinct()
          .from<Artist>(Artist::class.java)
          .innerJoin<Track>(Track::class.java)
          .on(Artist_Table.artist.withTable()
              .eq(Track_Table.artist.withTable()))
          .where(Track_Table.genre.`is`(filter))
          .orderBy(Artist_Table.artist.withTable(), true).
          groupBy(Artist_Table.artist.withTable())
    }

    Observable.fromEmitter<FlowCursorList<Artist>>({
      val cursor = FlowCursorList.Builder<Artist>(Artist::class.java).modelQueriable(query).build()
      it.onNext(cursor)
      it.onCompleted()
    }, Emitter.BackpressureMode.LATEST)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread()).subscribe({
      data = it
      notifyDataSetChanged()
    }) { Timber.v(it, "failed to load the data") }
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    mListener = listener
  }

  /**
   * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
   * an item.
   *
   *
   * This new ViewHolder should be constructed with a new View that can represent the items
   * of the given type. You can either create a new View manually or inflate it from an XML
   * layout file.
   *
   *
   * The new ViewHolder will be used to display items of the adapter using
   * [.onBindViewHolder]. Since it will be re-used to display different
   * items in the data set, it is a good idea to cache references to sub views of the View to
   * avoid unnecessary [View.findViewById] calls.

   * @param parent The ViewGroup into which the new View will be added after it is bound to
   * * an adapter position.
   * *
   * @param viewType The view type of the new View.
   * *
   * @return A new ViewHolder that holds a View of the given view type.
   * *
   * @see .getItemViewType
   * @see .onBindViewHolder
   */
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

  /**
   * Called by RecyclerView to display the data at the specified position. This method
   * should update the contents of the [ViewHolder.itemView] to reflect the item at
   * the given position.
   *
   *
   * Note that unlike [android.widget.ListView], RecyclerView will not call this
   * method again if the position of the item changes in the data set unless the item itself
   * is invalidated or the new position cannot be determined. For this reason, you should only
   * use the `position` parameter while acquiring the related data item inside this
   * method and should not keep a copy of it. If you need the position of an item later on
   * (e.g. in a click listener), use [ViewHolder.getPosition] which will have the
   * updated position.

   * @param holder The ViewHolder which should be updated to represent the contents of the
   * * item at the given position in the data set.
   * *
   * @param position The position of the item within the adapter's data set.
   */
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val entry = data!!.getItem(position.toLong())
    holder.title.text = if (TextUtils.isEmpty(entry.artist)) holder.empty else entry.artist
  }

  /**
   * Returns the total number of items in the data set hold by the adapter.

   * @return The total number of items in this adapter.
   */
  override fun getItemCount(): Int {
    return data?.count ?: 0
  }

  fun refresh() {
    data!!.refresh()
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
}
