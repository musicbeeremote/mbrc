package com.kelsos.mbrc.features.library

interface OnFastScrollListener {
  fun onStart(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int)

  fun onComplete(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int)
}