package com.kelsos.mbrc.extensions

import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.list.FlowQueryList

fun <T> FlowCursorList<T>?.count(): Int {
  if (this == null) {
    return 0
  }
  return this.count.toInt()
}

fun <T> FlowQueryList<T>?.count(): Int {
  if (this == null) {
    return 0
  }
  return this.count.toInt()
}
