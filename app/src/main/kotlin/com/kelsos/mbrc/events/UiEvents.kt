package com.kelsos.mbrc.events

import androidx.annotation.StringDef
import androidx.annotation.StringRes
import com.kelsos.mbrc.annotations.Connection
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.Repeat
import com.kelsos.mbrc.content.library.tracks.TrackInfo
import com.kelsos.mbrc.enums.LfmStatus
import com.kelsos.mbrc.networking.DiscoveryStop.Reason

class ConnectionSettingsChanged(val defaultId: Long)

class ConnectionStatusChangeEvent(@Connection.Status val status: Int)

class CoverChangedEvent(val path: String = "")

class DiscoveryStopped(@Reason val reason: Int)

class LfmRatingChanged(val status: LfmStatus)

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

class PlayStateChange(@PlayerState.State val state: String, val position: Long = 0)

class RatingChanged(val rating: Float)

class RemoteClientMetaData(
  val trackInfo: TrackInfo,
  val coverPath: String = "",
  val duration: Long = 0
)

class RepeatChange(@Repeat.Mode val mode: String)

class RequestConnectionStateEvent

class ScrobbleChange(val isActive: Boolean)

class ShuffleChange(@ShuffleState val shuffleState: String) {

  @StringDef(OFF, AUTODJ, SHUFFLE)
  @Retention(AnnotationRetention.SOURCE)
  annotation class ShuffleState

  companion object {
    const val OFF = "off"
    const val AUTODJ = "autodj"
    const val SHUFFLE = "shuffle"
  }
}

class TrackInfoChangeEvent(val trackInfo: TrackInfo)

class TrackMoved(val from: Int, val to: Int, val success: Boolean)

class TrackRemoval(val index: Int, val success: Boolean)

class UpdatePosition(val current: Int, val total: Int)

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
