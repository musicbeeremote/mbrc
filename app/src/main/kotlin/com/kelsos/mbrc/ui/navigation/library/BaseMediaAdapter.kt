package com.kelsos.mbrc.ui.navigation.library

import android.support.v7.widget.RecyclerView
import com.kelsos.mbrc.interfaces.data.Data

abstract class BaseMediaAdapter<M : Data, T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {

  private var data: List<M> = emptyList()

  fun setList(data: List<M>) {
    this.data = data
    notifyDataSetChanged()
  }

  fun getItem(position: Int): M? = data[position]

  override fun getItemCount(): Int = data.size
}
