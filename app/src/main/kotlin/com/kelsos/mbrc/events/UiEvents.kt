package com.kelsos.mbrc.events

import androidx.annotation.StringRes
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import com.kelsos.mbrc.networking.connections.Connection

class ConnectionSettingsChanged(val defaultId: Long)

class ConnectionStatusChangeEvent(@Connection.Status val status: Int)

class LibraryRefreshCompleteEvent

class NotifyUser {
  val message: String
  val resId: Int
  var isFromResource: Boolean = false
    private set

  constructor(message: String) {
    this.message = message
    this.isFromResource = false
    this.resId = -1
  }

  constructor(@StringRes resId: Int) {
    this.resId = resId
    this.isFromResource = true
    this.message = ""
  }
}

class PlayStateChange(@PlayerState.State val state: String, val position: Long = 0)

class RatingChanged(val rating: Float)

class RemoteClientMetaData(
  val track: PlayingTrackModel,
  val coverPath: String = ""
)

class TrackRemovalEvent(val index: Int, val success: Boolean)
