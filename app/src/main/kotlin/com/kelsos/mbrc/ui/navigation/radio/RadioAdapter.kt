package com.kelsos.mbrc.ui.navigation.radio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.radios.RadioStation
import com.kelsos.mbrc.databinding.ListitemSingleBinding
import javax.inject.Inject

class RadioAdapter
@Inject
constructor() : PagingDataAdapter<RadioStation, RadioAdapter.ViewHolder>(
  RADIO_COMPARATOR
) {
  private var radioPressedListener: OnRadioPressedListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater: LayoutInflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.listitem_single, parent, false)
    val viewHolder = ViewHolder(view)

    viewHolder.itemView.setOnClickListener {
      val path = getItem(viewHolder.bindingAdapterPosition)?.url
      path?.let {
        radioPressedListener?.onRadioPressed(it)
      }
    }
    return viewHolder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val radio = getItem(holder.bindingAdapterPosition)
    radio?.let {
      holder.name.text = radio.name
    }
    holder.context.visibility = View.GONE
  }

  fun setOnRadioPressedListener(onRadioPressedListener: OnRadioPressedListener?) {
    this.radioPressedListener = onRadioPressedListener
  }

  interface OnRadioPressedListener {
    fun onRadioPressed(path: String)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView
    val context: ImageView
    init {
      val binding = ListitemSingleBinding.bind(itemView)
      name = binding.lineOne
      context = binding.uiItemContextIndicator
    }
  }

  companion object {
    val RADIO_COMPARATOR = object : DiffUtil.ItemCallback<RadioStation>() {
      override fun areItemsTheSame(oldItem: RadioStation, newItem: RadioStation): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: RadioStation, newItem: RadioStation): Boolean {
        return oldItem.name == newItem.name
      }
    }
  }
}
