package com.kelsos.mbrc.features.library

import com.kelsos.mbrc.ui.UiMessageBase

sealed class LibraryResult : UiMessageBase {
  object RefreshFailure : LibraryResult()
  object RefreshSuccess : LibraryResult()
}
