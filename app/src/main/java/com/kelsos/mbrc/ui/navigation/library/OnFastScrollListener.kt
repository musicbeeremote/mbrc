package com.kelsos.mbrc.ui.navigation.library

interface OnFastScrollListener {
  fun onStart()

  fun onComplete(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int)
}