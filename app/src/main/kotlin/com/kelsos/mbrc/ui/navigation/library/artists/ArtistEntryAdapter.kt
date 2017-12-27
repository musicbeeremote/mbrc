package com.kelsos.mbrc.ui.navigation.library.artists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.databinding.ListitemSingleBinding
import com.kelsos.mbrc.ui.navigation.library.popup
import javax.inject.Inject

class ArtistEntryAdapter
@Inject
constructor() : PagingDataAdapter<Artist, ArtistEntryAdapter.ViewHolder>(DIFF_CALLBACK) {
  private var listener: MenuItemSelectedListener? = null

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val holder = ViewHolder.create(parent)
    holder.onIndicatorClick { view, position ->
      view.popup(R.menu.popup_artist) { id ->
        val artist = getItem(position) ?: return@popup
        listener?.onMenuItemSelected(id, artist)
      }
    }

    holder.onPress { position ->
      val artist = getItem(position) ?: return@onPress
      listener?.onItemClicked(artist)
    }
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val artist = getItem(position)
    if (artist != null) {
      holder.bindTo(artist)
    } else {
      holder.clear()
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Artist>() {
      override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
        return oldItem == newItem
      }
    }
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(itemId: Int, artist: Artist)

    fun onItemClicked(artist: Artist)
  }

  class ViewHolder(
    binding: ListitemSingleBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    private val title: TextView = binding.lineOne
    private val indicator: ImageView = binding.uiItemContextIndicator
    private val empty: String = itemView.context.getString(R.string.empty)

    fun bindTo(artist: Artist) {
      title.text = if (artist.artist.isBlank()) {
        empty
      } else {
        artist.artist
      }
    }

    fun clear() {
      title.text = ""
    }

    fun onIndicatorClick(onClick: (view: View, position: Int) -> Unit) {
      indicator.setOnClickListener { onClick(it, bindingAdapterPosition) }
    }

    fun onPress(onPress: (position: Int) -> Unit) {
      itemView.setOnClickListener { onPress(bindingAdapterPosition) }
    }

    companion object {
      fun create(parent: ViewGroup): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.listitem_single, parent, false)
        val binding = ListitemSingleBinding.bind(view)
        return ViewHolder(binding)
      }
    }
  }
}
