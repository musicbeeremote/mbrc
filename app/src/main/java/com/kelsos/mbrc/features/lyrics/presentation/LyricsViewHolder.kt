package com.kelsos.mbrc.features.lyrics.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.kelsos.mbrc.databinding.ListitemLyricsBinding
import com.kelsos.mbrc.ui.BindableViewHolder

class LyricsViewHolder(binding: ListitemLyricsBinding) : BindableViewHolder<String>(binding) {

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
      val binding = ListitemLyricsBinding.inflate(inflater, parent, false)
      return LyricsViewHolder(binding)
    }
  }
}
