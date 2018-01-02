package com.kelsos.mbrc.ui.navigation.library.genres

import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.Action
import com.kelsos.mbrc.extensions.string
import com.kelsos.mbrc.ui.navigation.library.BaseMediaAdapter
import com.kelsos.mbrc.ui.navigation.library.popup
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller.BubbleTextGetter
import kotterknife.bindView
import javax.inject.Inject

class GenreEntryAdapter
@Inject
constructor() : BaseMediaAdapter<GenreEntity, GenreEntryAdapter.ViewHolder>(),
    BubbleTextGetter {

  private var listener: MenuItemSelectedListener? = null
  private val indicatorPressed: (View, Int) -> Unit = { view, position ->
    view.popup(R.menu.popup_genre) {
      val action = when (it) {
        R.id.popup_genre_play -> LibraryPopup.NOW
        R.id.popup_genre_artists -> LibraryPopup.PROFILE
        R.id.popup_genre_queue_next -> LibraryPopup.NEXT
        R.id.popup_genre_queue_last -> LibraryPopup.LAST
        else -> throw IllegalArgumentException("invalid menuItem id $it")
      }
      val genreEntity = getItem(position)

      genreEntity?.run {
        listener?.onMenuItemSelected(action, this)
      }
    }
  }

  private val pressed: (View, Int) -> Unit = { _, position ->
    val genreEntity = getItem(position)
    genreEntity?.let {
      listener?.onItemClicked(it)
    }
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder.create(parent, indicatorPressed, pressed)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val genre = getItem(holder.adapterPosition)
    if (genre != null) {
      holder.bindTo(genre)
    } else {
      holder.clear()
    }
  }

  override fun getTextToShowInBubble(pos: Int): String {
    val genre = getItem(pos)?.genre
    genre?.let {
      if (it.isNotBlank()) {
        return it.substring(0, 1)
      }
    }

    return "-"
  }

  companion object {
    val DIFF_CALLBACK = object : DiffCallback<GenreEntity>() {
      override fun areItemsTheSame(oldItem: GenreEntity, newItem: GenreEntity): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: GenreEntity, newItem: GenreEntity): Boolean {
        return oldItem == newItem
      }
    }
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(@Action action: String, entry: GenreEntity): Boolean

    fun onItemClicked(genre: GenreEntity)
  }

  class ViewHolder(
      itemView: View,
      indicatorPressed: (view: View, position: Int) -> Unit,
      pressed: (view: View, position: Int) -> Unit
  ) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView by bindView(R.id.line_one)
    private val indicator: ImageView by bindView(R.id.ui_item_context_indicator)
    private val empty: String by lazy { string(R.string.empty) }

    init {
      indicator.setOnClickListener { indicatorPressed(it, adapterPosition) }
      itemView.setOnClickListener { pressed(it, adapterPosition) }
    }

    companion object {
      fun create(
          parent: ViewGroup,
          indicatorPressed: (view: View, position: Int) -> Unit,
          pressed: (view: View, position: Int) -> Unit
      ): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.listitem_single, parent, false)
        return ViewHolder(view, indicatorPressed, pressed)
      }
    }

    fun bindTo(genre: GenreEntity) {
      title.text = if (genre.genre.isBlank()) empty else genre.genre
    }

    fun clear() {
      title.text = ""
    }
  }
}
