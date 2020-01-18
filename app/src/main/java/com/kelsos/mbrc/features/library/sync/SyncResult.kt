package com.kelsos.mbrc.features.library.sync

import com.kelsos.mbrc.ui.UiMessageBase

sealed class SyncResult : UiMessageBase {
  object SUCCESS : SyncResult()
  object NOOP : SyncResult()
  object FAILED : SyncResult()
}