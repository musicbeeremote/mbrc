package com.kelsos.mbrc.utils

import androidx.recyclerview.widget.ListUpdateCallback
import arrow.core.Either

fun Either<Throwable, Unit>.result(): Any = fold({ it }, {})

val noopListUpdateCallback = object : ListUpdateCallback {
  override fun onInserted(position: Int, count: Int) = Unit
  override fun onRemoved(position: Int, count: Int) = Unit
  override fun onMoved(fromPosition: Int, toPosition: Int) = Unit
  override fun onChanged(position: Int, count: Int, payload: Any?) = Unit
}
