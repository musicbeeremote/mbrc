package com.kelsos.mbrc.feature.playback.player

import com.kelsos.mbrc.core.common.mvvm.UiMessageBase

sealed class PlayerUiMessage : UiMessageBase {
  object ShowChangelog : PlayerUiMessage()
}
