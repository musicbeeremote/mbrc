package com.kelsos.mbrc.events

import android.support.annotation.StringRes
import com.kelsos.mbrc.content.active_status.PlayerState
import com.kelsos.mbrc.content.active_status.Repeat
import com.kelsos.mbrc.content.library.tracks.TrackInfo
import com.kelsos.mbrc.networking.DiscoveryStop.Reason
import com.kelsos.mbrc.networking.connections.Connection
import com.kelsos.mbrc.ui.navigation.main.LfmRating.Rating

class ConnectionSettingsChanged(val defaultId: Long)

class ConnectionStatusChangeEvent(@Connection.Status val status: Int)

class CoverChangedEvent(val path: String = "")

class DiscoveryStopped(@Reason val reason: Int)

class LfmRatingChanged(@Rating val status: Int)

class LibraryRefreshCompleteEvent

class LyricsUpdatedEvent(val lyrics: String)

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

class OnMainFragmentOptionsInflated

class PlayStateChange(@PlayerState.State val state: String)

class RatingChanged(val rating: Float)

class RemoteClientMetaData(val trackInfo: TrackInfo, val coverPath: String = "")

class RepeatChange(@Repeat.Mode val mode: String)

class RequestConnectionStateEvent

class ScrobbleChange(val isActive: Boolean)

class ShuffleChange(@ShuffleState val shuffleState: String) {

  @android.support.annotation.StringDef(OFF, AUTODJ, SHUFFLE)
  @Retention(AnnotationRetention.SOURCE)
  annotation class ShuffleState

  companion object {
    const val OFF = "off"
    const val AUTODJ = "autodj"
    const val SHUFFLE = "shuffle"
  }
}

class TrackInfoChangeEvent(val trackInfo: TrackInfo)

class TrackMovedEvent(val from: Int, val to: Int, val success: Boolean)

class TrackRemovalEvent(val index: Int, val success: Boolean)

class UpdatePositionEvent(val current: Int, val total: Int)

class VolumeChange {
  var volume: Int = 0
    private set
  var isMute: Boolean = false
    private set

  constructor(vol: Int) {
    this.volume = vol
    this.isMute = false
  }

  constructor() {
    this.volume = 0
    this.isMute = true
  }
}
