package com.kelsos.mbrc.features.lyrics.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.kelsos.mbrc.databinding.UiListLyricsItemBinding
import com.kelsos.mbrc.ui.BindableViewHolder

class LyricsViewHolder(binding: UiListLyricsItemBinding) : BindableViewHolder<String>(binding) {

  private val title: TextView = binding.text1

  override fun bindTo(item: String) {
    title.text = item
  }

  override fun clear() {
    title.text = ""
  }

  companion object {
    fun create(parent: ViewGroup): LyricsViewHolder {
      val inflater = LayoutInflater.from(parent.context)
      val binding = UiListLyricsItemBinding.inflate(inflater, parent, false)
      return LyricsViewHolder(binding)
    }
  }
}
