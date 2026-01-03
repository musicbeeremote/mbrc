package com.kelsos.mbrc.feature.library.tracks

import com.kelsos.mbrc.core.common.mvvm.UiMessageBase

sealed class TrackUiMessage : UiMessageBase {
  data class QueueSuccess(val tracksCount: Int) : TrackUiMessage()

  object QueueFailed : TrackUiMessage()

  object NetworkUnavailable : TrackUiMessage()
}
