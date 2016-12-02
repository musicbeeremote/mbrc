package com.kelsos.mbrc.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R

class LyricsAdapter(private var data: List<String> = emptyList()) : RecyclerView.Adapter<LyricsAdapter.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.ui_list_lyrics_item, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val str = data[position]
    holder.title.text = str
  }

  fun updateLyrics(lyrics: List<String>) {
    this.data = lyrics
    notifyDataSetChanged()
  }

  override fun getItemCount(): Int {
    return data.size
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(android.R.id.text1) lateinit var title: TextView

    init {
      ButterKnife.bind(this, itemView)
    }
  }

  fun clear() {
    if (data.isNotEmpty()) {
      data = emptyList()
    }
  }
}
