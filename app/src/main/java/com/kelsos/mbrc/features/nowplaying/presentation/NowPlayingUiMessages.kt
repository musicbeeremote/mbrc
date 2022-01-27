package com.kelsos.mbrc.features.nowplaying.presentation

import com.kelsos.mbrc.ui.UiMessageBase

sealed class NowPlayingUiMessages : UiMessageBase {
  data object RefreshFailed : NowPlayingUiMessages()

  data object RefreshSuccess : NowPlayingUiMessages()
}
