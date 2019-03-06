package com.kelsos.mbrc.ui.navigation.radio

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.content.radios.RadioStation

class RadioAdapter : PagedListAdapter<RadioStation, RadioViewHolder>(DIFF) {

  private var radioPressedListener: OnRadioPressedListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder {
    return RadioViewHolder.create(parent) { position ->
      getItem(position)?.let {
        radioPressedListener?.onRadioPressed(it.url)
      }
    }
  }

  override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
    getItem(position)?.let {
      holder.bindTo(it)
    }
  }

  fun setOnRadioPressedListener(onRadioPressedListener: OnRadioPressedListener?) {
    this.radioPressedListener = onRadioPressedListener
  }

  interface OnRadioPressedListener {
    fun onRadioPressed(path: String)
  }

  companion object {
    val DIFF = object : DiffUtil.ItemCallback<RadioStation>() {
      override fun areItemsTheSame(
        oldItem: RadioStation,
        newItem: RadioStation
      ): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(
        oldItem: RadioStation,
        newItem: RadioStation
      ): Boolean {
        return oldItem == newItem
      }
    }
  }
}