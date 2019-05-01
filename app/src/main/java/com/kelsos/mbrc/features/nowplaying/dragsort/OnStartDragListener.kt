package com.kelsos.mbrc.features.nowplaying.dragsort

import androidx.recyclerview.widget.RecyclerView

interface OnStartDragListener {
  fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}