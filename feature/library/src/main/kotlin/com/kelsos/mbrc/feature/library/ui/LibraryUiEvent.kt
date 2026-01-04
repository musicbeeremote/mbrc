package com.kelsos.mbrc.feature.library.ui

import com.kelsos.mbrc.core.common.mvvm.UiMessageBase
import com.kelsos.mbrc.feature.library.data.LibraryStats

sealed class LibraryUiEvent : UiMessageBase {
  class LibraryStatsReady(val stats: LibraryStats) : LibraryUiEvent()

  class UpdateAlbumArtistOnly(val enabled: Boolean) : LibraryUiEvent()

  object NetworkUnavailable : LibraryUiEvent()

  object PlayAllSuccess : LibraryUiEvent()

  object PlayAllFailed : LibraryUiEvent()
}
