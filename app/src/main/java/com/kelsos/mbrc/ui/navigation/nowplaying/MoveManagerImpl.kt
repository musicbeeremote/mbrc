package com.kelsos.mbrc.ui.navigation.nowplaying

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class MoveManagerImpl : MoveManager {

  private var originalPosition: Int = -1
  private var finalPosition: Int = -1

  private lateinit var onMoveSubmit: (originalPosition: Int, finalPosition: Int) -> Unit
  private var notify: Deferred<Unit>? = null

  override fun onMoveSubmit(onMoveSubmit: (originalPosition: Int, finalPosition: Int) -> Unit) {
    this.onMoveSubmit = onMoveSubmit
  }

  override suspend fun move(from: Int, to: Int) {
    notify?.cancel()
    if (originalPosition < 0) {
      originalPosition = from
    }

    if (finalPosition != to) {
      finalPosition = to
    }

    // TODO: maybe global scope is not the greatest idea ever
    notify = GlobalScope.async {
      delay(400)
      onMoveSubmit(originalPosition, finalPosition)
      originalPosition = -1
      finalPosition = -1
    }
  }
}