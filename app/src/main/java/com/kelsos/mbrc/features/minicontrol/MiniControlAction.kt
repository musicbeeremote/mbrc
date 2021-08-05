package com.kelsos.mbrc.features.minicontrol

sealed class MiniControlAction {
  object PlayPrevious : MiniControlAction()
  object PlayNext : MiniControlAction()
  object PlayPause : MiniControlAction()
}
