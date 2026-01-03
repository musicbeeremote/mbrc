package com.kelsos.mbrc.feature.minicontrol

sealed class MiniControlAction {
  data object PlayPrevious : MiniControlAction()

  data object PlayNext : MiniControlAction()

  data object PlayPause : MiniControlAction()
}
