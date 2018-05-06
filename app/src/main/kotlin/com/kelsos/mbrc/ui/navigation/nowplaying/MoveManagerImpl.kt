package com.kelsos.mbrc.ui.navigation.nowplaying

import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import javax.inject.Inject

class MoveManagerImpl
@Inject
constructor(dispatchers: AppCoroutineDispatchers) : MoveManager {
  private val job = SupervisorJob()
  private val scope = CoroutineScope(dispatchers.disk + job)
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

    notify = scope.async {
      delay(400)
      onMoveSubmit(originalPosition, finalPosition)
      originalPosition = -1
      finalPosition = -1
    }
  }
}
