package com.kelsos.mbrc.ui.navigation.nowplaying

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import javax.inject.Inject

class MoveManagerImpl
@Inject constructor() : MoveManager {

  private var originalPosition: Int = -1
  private var finalPosition: Int = -1

  private lateinit var onMoveSubmit: (originalPosition: Int, finalPosition: Int) -> Unit
  private var notify: Deferred<Unit>? = null

  override fun onMoveSubmit(onMoveSubmit: (originalPosition: Int, finalPosition: Int) -> Unit) {
    this.onMoveSubmit = onMoveSubmit
  }

  override fun move(from: Int, to: Int) {
    notify?.cancel()
    if (originalPosition < 0) {
      originalPosition = from
    }

    if (finalPosition != to) {
      finalPosition = to
    }

    notify = async(CommonPool) {
      delay(400)
      onMoveSubmit(originalPosition, finalPosition)
      originalPosition = -1
      finalPosition = -1
    }
  }
}