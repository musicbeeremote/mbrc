package com.kelsos.mbrc.features.nowplaying.presentation

import com.kelsos.mbrc.ui.UiMessageBase

sealed class NowPlayingUiMessages : UiMessageBase {
  object RefreshFailed : NowPlayingUiMessages()
  object RefreshSuccess : NowPlayingUiMessages()
}