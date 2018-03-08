package com.kelsos.mbrc.ui.drag

import android.support.v7.widget.RecyclerView

interface OnStartDragListener {
  fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}