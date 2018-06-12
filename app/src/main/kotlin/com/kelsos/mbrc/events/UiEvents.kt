package com.kelsos.mbrc.events

import androidx.annotation.StringRes
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import com.kelsos.mbrc.networking.connections.Connection

class ConnectionSettingsChanged(val defaultId: Long)

class ConnectionStatusChangeEvent(@Connection.Status val status: Int)

class NotifyUser(@StringRes val resId: Int) {
  val message: String
  var isFromResource: Boolean = false
    private set

  init {
    this.isFromResource = true
    this.message = ""
  }
}

class PlayStateChange(@PlayerState.State val state: String, val position: Long = 0)

class RemoteClientMetaData(
  val track: PlayingTrackModel,
  val coverPath: String = ""
)
