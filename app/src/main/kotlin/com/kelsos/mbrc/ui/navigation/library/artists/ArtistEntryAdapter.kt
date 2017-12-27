package com.kelsos.mbrc.ui.navigation.library.artists

import android.arch.paging.PagedListAdapter
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.ui.navigation.library.popup
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller.BubbleTextGetter
import com.kelsos.mbrc.utilities.Checks.ifNotNull
import kotterknife.bindView
import javax.inject.Inject

class ArtistEntryAdapter
@Inject constructor() : PagedListAdapter<ArtistEntity, ArtistEntryAdapter.ViewHolder>(DIFF_CALLBACK), BubbleTextGetter {
  private var listener: MenuItemSelectedListener? = null

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

    val holder = ViewHolder.create(parent)
    holder.onIndicatorClick { view, position ->
      view.popup(R.menu.popup_artist) {
        ifNotNull(listener, getItem(position)) { listener, artist ->
          listener.onMenuItemSelected(it, artist)
        }
      }
    }

    holder.onPress { position ->
      ifNotNull(listener, getItem(position)) { listener, artist ->
        listener.onItemClicked(artist)
      }

    }
    return holder
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
    fun onMenuItemSelected(itemId: Int, entry: ArtistEntity)

    fun onItemClicked(artist: ArtistEntity)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView by bindView(R.id.line_one)
    private val indicator: ImageView by bindView(R.id.ui_item_context_indicator)
    private val empty: String = itemView.context.getString(R.string.empty)

    companion object {
      fun create(parent: ViewGroup): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.listitem_single, parent, false)
        return ViewHolder(view)
      }
    }

    fun bindTo(artistEntity: ArtistEntity) {
      title.text = if (artistEntity.artist.isBlank()) {
        empty
      } else {
        artistEntity.artist
      }
    }

    fun onIndicatorClick(onClick: (view: View, position: Int) -> Unit) {
      indicator.setOnClickListener { onClick(it, adapterPosition) }
    }

    fun onPress(onPress: (position: Int) -> Unit) {
      itemView.setOnClickListener { onPress(adapterPosition) }
    }

    fun clear() {
      title.text = ""
    }
  }
}
