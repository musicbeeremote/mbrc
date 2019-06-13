package com.kelsos.mbrc.features.lyrics.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.BindableViewHolder
import kotterknife.bindView

class LyricsViewHolder(itemView: View) : BindableViewHolder<String>(itemView) {

  private val title: TextView by bindView(android.R.id.text1)

  override fun bindTo(item: String) {
    title.text = item
  }

  override fun clear() {
    title.text = ""
  }

  companion object {
    fun create(parent: ViewGroup): LyricsViewHolder {
      val layoutInflater = LayoutInflater.from(parent.context)
      val view = layoutInflater.inflate(R.layout.ui_list_lyrics_item, parent, false)
      return LyricsViewHolder(view)
    }
  }
}