package com.kelsos.mbrc.ui.navigation.lyrics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.BindableViewHolder
import kotterknife.bindView

class LyricsAdapter : ListAdapter<String, LyricsAdapter.ViewHolder>(DIFF) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder.create(parent)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bindTo(getItem(position))
  }

  class ViewHolder(itemView: View) : BindableViewHolder<String>(itemView) {

    private val title: TextView by bindView(android.R.id.text1)

    override fun bindTo(item: String) {
      title.text = item
    }

    override fun clear() {
      title.text = ""
    }

    companion object {
      fun create(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.ui_list_lyrics_item, parent, false)
        return ViewHolder(view)
      }
    }
  }

  companion object {
    val DIFF = object : DiffUtil.ItemCallback<String>() {
      override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
      }

      override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
      }
    }
  }
}