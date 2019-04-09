package com.kelsos.mbrc.ui.navigation.library

import com.kelsos.mbrc.ui.UiMessageBase

sealed class LibraryResult : UiMessageBase {
  object RefreshFailure : LibraryResult()
  object RefreshSuccess : LibraryResult()
}