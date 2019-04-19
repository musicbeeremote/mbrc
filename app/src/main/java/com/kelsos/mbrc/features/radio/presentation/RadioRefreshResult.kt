package com.kelsos.mbrc.features.radio.presentation

import com.kelsos.mbrc.ui.UiMessageBase

sealed class RadioRefreshResult : UiMessageBase {
  object RefreshSuccess : RadioRefreshResult()
  object RefreshFailed : RadioRefreshResult()
}