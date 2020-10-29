package com.kelsos.mbrc.adapters

import android.app.Activity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindString
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.data.library.Genre
import com.raizlabs.android.dbflow.list.FlowCursorList
import javax.inject.Inject

class GenreEntryAdapter
@Inject
constructor(context: Activity) : RecyclerView.Adapter<GenreEntryAdapter.ViewHolder>() {
  private var data: FlowCursorList<Genre>? = null
  private var listener: MenuItemSelectedListener? = null
  private val inflater: LayoutInflater = LayoutInflater.from(context)

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
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
    return ViewHolder(view)
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
    holder.title.text = if (TextUtils.isEmpty(entry.genre)) holder.empty else entry.genre

    holder.indicator.setOnClickListener {
      val popupMenu = PopupMenu(it.context, it)
      popupMenu.inflate(R.menu.popup_genre)
      popupMenu.setOnMenuItemClickListener { menuItem ->
        return@setOnMenuItemClickListener listener?.onMenuItemSelected(menuItem, entry) ?: false

      }
      popupMenu.show()
    }

    holder.itemView.setOnClickListener {
      if (listener != null) {
        listener!!.onItemClicked(entry)
      }
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
    fun onMenuItemSelected(menuItem: MenuItem, genre: Genre): Boolean

    fun onItemClicked(genre: Genre)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.line_one)
    lateinit var title: TextView
    @BindView(R.id.ui_item_context_indicator)
    lateinit var indicator: LinearLayout
    @BindString(R.string.empty)
    lateinit var empty: String

    init {
      ButterKnife.bind(this, itemView)
    }
  }

  fun update(cursor: FlowCursorList<Genre>) {
    data = cursor
    notifyDataSetChanged()
  }
}
