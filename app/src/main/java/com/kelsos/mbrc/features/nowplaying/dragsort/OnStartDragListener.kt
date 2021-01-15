package com.kelsos.mbrc.features.nowplaying.dragsort

import androidx.recyclerview.widget.RecyclerView

interface OnStartDragListener {
  fun onStartDrag(start: Boolean, viewHolder: RecyclerView.ViewHolder)
}
