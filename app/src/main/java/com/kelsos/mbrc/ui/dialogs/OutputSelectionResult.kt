package com.kelsos.mbrc.ui.dialogs

import com.kelsos.mbrc.ui.UiMessageBase

sealed class OutputSelectionResult : UiMessageBase {
  object Success : OutputSelectionResult()
  object ConnectionError : OutputSelectionResult()
  object UnknownError : OutputSelectionResult()
}