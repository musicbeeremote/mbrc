package com.kelsos.mbrc.ui.navigation.nowplaying

interface MoveManager {
  suspend fun move(from: Int, to: Int)

  fun onMoveSubmit(onMoveSubmit: (originalPosition: Int, finalPosition: Int) -> Unit)
}
