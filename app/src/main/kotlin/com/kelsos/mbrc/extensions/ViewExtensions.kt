package com.kelsos.mbrc.extensions

import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE


fun View?.hide() {
  this?.visibility = INVISIBLE
}

fun View?.show() {
  this?.visibility = VISIBLE
}

fun View?.isInvisible(): Boolean = this?.visibility == INVISIBLE
