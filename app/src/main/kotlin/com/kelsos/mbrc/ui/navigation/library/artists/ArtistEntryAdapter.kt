package com.kelsos.mbrc.ui.navigation.library.artists

import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.Action
import com.kelsos.mbrc.ui.navigation.library.BaseMediaAdapter
import com.kelsos.mbrc.ui.navigation.library.popup
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller.BubbleTextGetter
import com.kelsos.mbrc.utilities.Checks.ifNotNull
import kotterknife.bindView
import javax.inject.Inject

class ArtistEntryAdapter
@Inject constructor() : BaseMediaAdapter<ArtistEntity, ArtistEntryAdapter.ViewHolder>(), BubbleTextGetter {

  private var listener: MenuItemSelectedListener? = null
  private val indicatorPressed: (View, Int) -> Unit = { view, position ->
    view.popup(R.menu.popup_artist) {

      val action = when (it) {
        R.id.popup_artist_album -> LibraryPopup.PROFILE
        R.id.popup_artist_queue_next -> LibraryPopup.NEXT
        R.id.popup_artist_queue_last -> LibraryPopup.LAST
        R.id.popup_artist_play -> LibraryPopup.NOW
        else -> throw IllegalArgumentException("invalid menuItem id $it")
      }

      ifNotNull(listener, getItem(position)) { listener, artist ->
        listener.onMenuItemSelected(action, artist)
      }
    }
  }

  private val pressed: (View, Int) -> Unit = { _, position ->
    ifNotNull(listener, getItem(position)) { listener, artist ->
      listener.onItemClicked(artist)
    }
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder.create(parent, indicatorPressed, pressed)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val artistEntity = getItem(position)
    if (artistEntity != null) {
      holder.bindTo(artistEntity)
    } else {
      holder.clear()
    }

  }

  override fun getTextToShowInBubble(pos: Int): String {
    val artist = getItem(pos)?.artist
    if (artist != null && artist.isNotBlank()) {
      return artist.substring(0, 1)
    }
    return "-"
  }

  companion object {
    val DIFF_CALLBACK = object : DiffCallback<ArtistEntity>() {
      override fun areItemsTheSame(oldItem: ArtistEntity, newItem: ArtistEntity): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: ArtistEntity, newItem: ArtistEntity): Boolean {
        return oldItem == newItem
      }
    }
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(@Action action: String, entry: ArtistEntity)

    fun onItemClicked(artist: ArtistEntity)
  }

  class ViewHolder(
      itemView: View,
      indicatorPressed: (View, Int) -> Unit,
      pressed: (View, Int) -> Unit
  ) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView by bindView(R.id.line_one)
    private val indicator: ImageView by bindView(R.id.ui_item_context_indicator)
    private val empty: String = itemView.context.getString(R.string.empty)

    init {
      indicator.setOnClickListener { indicatorPressed(it, adapterPosition) }
      itemView.setOnClickListener { pressed(it, adapterPosition) }
    }

    companion object {
      fun create(
          parent: ViewGroup,
          indicatorPressed: (View, Int) -> Unit,
          pressed: (View, Int) -> Unit
      ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.listitem_single, parent, false)
        return ViewHolder(view, indicatorPressed, pressed)
      }
    }

    fun bindTo(artistEntity: ArtistEntity) {
      title.text = if (artistEntity.artist.isBlank()) {
        empty
      } else {
        artistEntity.artist
      }
    }

    fun clear() {
      title.text = ""
    }
  }
}
