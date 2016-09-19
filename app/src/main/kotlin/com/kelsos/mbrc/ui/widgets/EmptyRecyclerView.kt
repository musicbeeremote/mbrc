package com.kelsos.mbrc.ui.widgets

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

class EmptyRecyclerView : RecyclerView {
  internal var emptyView: View? = null

  constructor(context: Context) : super(context) {
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
  }

  constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
  }

  internal fun checkIfEmpty() {
    if (emptyView != null) {
      emptyView!!.visibility = if (adapter.itemCount > 0) View.GONE else View.VISIBLE
    }
  }

  internal val observer: RecyclerView.AdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
    override fun onChanged() {
      super.onChanged()
      checkIfEmpty()
    }
  }

  override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
    val oldAdapter = getAdapter()
    oldAdapter?.unregisterAdapterDataObserver(observer)
    super.setAdapter(adapter)
    adapter?.registerAdapterDataObserver(observer)
  }

  fun setEmptyView(emptyView: View?) {
    this.emptyView = emptyView
    checkIfEmpty()
  }
}

