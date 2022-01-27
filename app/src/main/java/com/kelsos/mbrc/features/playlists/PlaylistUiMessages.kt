package com.kelsos.mbrc.features.playlists

import com.kelsos.mbrc.ui.UiMessageBase

sealed class PlaylistUiMessages : UiMessageBase {
  object RefreshFailed : PlaylistUiMessages()

  object RefreshSuccess : PlaylistUiMessages()
}
