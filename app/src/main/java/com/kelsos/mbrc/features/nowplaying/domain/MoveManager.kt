package com.kelsos.mbrc.features.nowplaying.domain

interface MoveManager {
  suspend fun move(from: Int, to: Int)

  fun onMoveSubmit(onMoveSubmit: (originalPosition: Int, finalPosition: Int) -> Unit)
}