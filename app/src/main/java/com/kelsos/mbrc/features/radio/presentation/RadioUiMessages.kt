package com.kelsos.mbrc.features.radio.presentation

import com.kelsos.mbrc.ui.UiMessageBase

sealed class RadioUiMessages : UiMessageBase {
  object QueueFailed : RadioUiMessages()
  object QueueSuccess : RadioUiMessages()
  object NetworkError : RadioUiMessages()
  object RefreshSuccess : RadioUiMessages()
  object RefreshFailed : RadioUiMessages()
}
