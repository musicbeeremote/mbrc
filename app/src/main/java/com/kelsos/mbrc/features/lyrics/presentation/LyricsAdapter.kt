package com.kelsos.mbrc.features.lyrics.presentation

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import java.util.concurrent.Executor

class LyricsAdapter(executor: Executor) : ListAdapter<String, LyricsViewHolder>(config(executor)) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LyricsViewHolder {
    return LyricsViewHolder.create(parent)
  }

  override fun onBindViewHolder(holder: LyricsViewHolder, position: Int) {
    holder.bindTo(getItem(position))
  }

  companion object {
    private val DIFF = object : DiffUtil.ItemCallback<String>() {
      override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
      }

      override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
      }
    }

    fun config(executor: Executor): AsyncDifferConfig<String> {
      return AsyncDifferConfig.Builder(DIFF)
        .setBackgroundThreadExecutor(executor)
        .build()
    }
  }
}
