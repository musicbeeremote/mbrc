package com.kelsos.mbrc.ui.navigation.lyrics

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kelsos.mbrc.databinding.UiListLyricsItemBinding
import com.kelsos.mbrc.ui.BindableViewHolder

class LyricsAdapter : ListAdapter<String, LyricsAdapter.ViewHolder>(DIFF) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder.create(parent)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bindTo(getItem(position))
  }

  class ViewHolder(
    binding: UiListLyricsItemBinding
  ) : BindableViewHolder<String>(binding) {

    private val title: TextView = binding.text1

    override fun bindTo(item: String) {
      title.text = item
    }

    override fun clear() {
      title.text = ""
    }

    companion object {
      fun create(parent: ViewGroup): ViewHolder {
        val binding = UiListLyricsItemBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
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
