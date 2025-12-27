package com.kelsos.mbrc.features.nowplaying

import com.kelsos.mbrc.common.mvvm.UiMessageBase

sealed class NowPlayingUiMessages : UiMessageBase {
  class RefreshFailed(val throwable: Throwable) : NowPlayingUiMessages()

  data object RefreshSucceeded : NowPlayingUiMessages()

  data object NetworkUnavailable : NowPlayingUiMessages()

  data object PlayFailed : NowPlayingUiMessages()

  data object RemoveFailed : NowPlayingUiMessages()

  data object MoveFailed : NowPlayingUiMessages()

  data class SearchSuccess(val trackTitle: String) : NowPlayingUiMessages()

  data object SearchNotFound : NowPlayingUiMessages()
}
