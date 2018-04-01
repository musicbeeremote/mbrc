package com.kelsos.mbrc.ui.navigation.radio

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.kelsos.mbrc.content.radios.RadioStationEntity
import javax.inject.Inject

class RadioAdapter
@Inject
constructor() : PagedListAdapter<RadioStationEntity, RadioViewHolder>(DIFF) {
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
    val DIFF = object : DiffUtil.ItemCallback<RadioStationEntity>() {
      override fun areItemsTheSame(oldItem: RadioStationEntity?, newItem: RadioStationEntity?): Boolean {
        return oldItem?.id == newItem?.id
      }

      override fun areContentsTheSame(oldItem: RadioStationEntity?, newItem: RadioStationEntity?): Boolean {
        return oldItem == newItem
      }
    }
  }
}