package com.kelsos.mbrc.extensions

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.kelsos.mbrc.adapters.ArtistEntryAdapter
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView

fun EmptyRecyclerView.initLinear(adapter: ArtistEntryAdapter, emptyView: View) {
  this.adapter = adapter
  this.emptyView = emptyView
  this.layoutManager = LinearLayoutManager(this.context)
}
