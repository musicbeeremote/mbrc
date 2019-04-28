package com.kelsos.mbrc.features.nowplaying.domain

import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class MoveManagerImpl(dispatchers: AppCoroutineDispatchers) :
  MoveManager {
  private var originalPosition: Int = -1
  private var finalPosition: Int = -1
  private val moveJob: Job = Job()
  private val moveScope: CoroutineScope = CoroutineScope(dispatchers.disk + moveJob)
  private var deferred: Deferred<Unit>? = null
  private lateinit var onMoveSubmit: (originalPosition: Int, finalPosition: Int) -> Unit

  override fun onMoveSubmit(onMoveSubmit: (originalPosition: Int, finalPosition: Int) -> Unit) {
    this.onMoveSubmit = onMoveSubmit
  }

  override suspend fun move(from: Int, to: Int) {
    deferred?.cancel()

    if (originalPosition < 0) {
      originalPosition = from
    }

    if (finalPosition != to) {
      finalPosition = to
    }

    deferred = moveScope.async {
      delay(400)
      onMoveSubmit(originalPosition, finalPosition)
      originalPosition = -1
      finalPosition = -1
    }
  }
}