package com.kelsos.mbrc.ui.navigation.library.genres

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.databinding.ListitemSingleBinding
import com.kelsos.mbrc.ui.navigation.library.popup
import javax.inject.Inject

class GenreEntryAdapter
@Inject
constructor() : PagingDataAdapter<Genre, GenreEntryAdapter.ViewHolder>(DIFF_CALLBACK) {

  private var listener: MenuItemSelectedListener? = null

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener) {
    this.listener = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val holder = ViewHolder.create(parent)
    holder.onIndicatorClick { view, position ->
      view.popup(R.menu.popup_genre) { id ->
        val genre = getItem(position) ?: return@popup
        listener?.onMenuItemSelected(id, genre)
      }
    }
    holder.onPress { position ->
      val genre = getItem(position) ?: return@onPress
      listener?.onItemClicked(genre)
    }
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val genre = getItem(holder.bindingAdapterPosition)
    if (genre != null) {
      holder.bindTo(genre)
    } else {
      holder.clear()
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Genre>() {
      override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
        return oldItem == newItem
      }
    }
  }

  interface MenuItemSelectedListener {
    fun onMenuItemSelected(@IdRes itemId: Int, genre: Genre): Boolean

    fun onItemClicked(genre: Genre)
  }

  class ViewHolder(
    binding: ListitemSingleBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    private val title: TextView = binding.lineOne
    private val indicator: ImageView = binding.uiItemContextIndicator
    private val empty: String by lazy { itemView.context.getString(R.string.empty) }

    fun bindTo(genre: Genre) {
      title.text = if (genre.genre.isBlank()) empty else genre.genre
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
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.listitem_single, parent, false)
        val binding = ListitemSingleBinding.bind(view)
        return ViewHolder(binding)
      }
    }
  }
}
