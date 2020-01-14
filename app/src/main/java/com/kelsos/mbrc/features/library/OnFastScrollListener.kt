package com.kelsos.mbrc.features.library

interface OnFastScrollListener {
  fun onStart()

  fun onComplete(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int)
}