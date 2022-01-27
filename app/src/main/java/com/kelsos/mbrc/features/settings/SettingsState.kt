package com.kelsos.mbrc.features.settings

import com.kelsos.mbrc.features.queue.Queue

data class SettingsState(
  val version: String,
  val revision: String,
  val buildTime: String,
  val callAction: CallAction,
  val libraryAction: Queue,
  val onlyAlbumArtists: Boolean,
  val checkPluginUpdate: Boolean,
  val debugLog: Boolean,
) {
  companion object {
    fun default() =
      SettingsState(
        version = "",
        revision = "",
        buildTime = "",
        callAction = CallAction.None,
        libraryAction = Queue.Now,
        onlyAlbumArtists = false,
        checkPluginUpdate = false,
        debugLog = false,
      )
  }
}
