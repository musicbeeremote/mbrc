package com.kelsos.mbrc.features.player

import com.kelsos.mbrc.ui.UiMessageBase

sealed class PlayerUiMessage : UiMessageBase {
  object ShowChangelog : PlayerUiMessage()
  object ShowPluginUpdate : PlayerUiMessage()
}
