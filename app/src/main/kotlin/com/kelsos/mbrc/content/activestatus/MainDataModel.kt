package com.kelsos.mbrc.content.activestatus

import com.kelsos.mbrc.content.library.tracks.TrackInfo
import com.kelsos.mbrc.events.ShuffleChange
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.ui.navigation.main.LfmRating
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainDataModel
@Inject
constructor() {
  var pluginUpdateAvailable: Boolean = false
  var pluginUpdateRequired: Boolean = false
  var minimumRequired: String = ""
  var position: Long = 0
  var duration: Long = 0
  var trackInfo: TrackInfo = TrackInfo()
  var coverPath: String = ""
  var rating: Float = 0f
  var volume: Int = 0

  @ShuffleChange.ShuffleState
  var shuffle: String = ShuffleChange.OFF
  var isScrobblingEnabled: Boolean = false
  var isMute: Boolean = false

  @get:LfmRating.Rating
  @setparam:LfmRating.Rating
  var lfmStatus: Int = LfmRating.NORMAL
    private set
  var apiOutOfDate: Boolean = false
    private set

  var pluginVersion: String = "1.0.0"
    set(value) {
      if (value.isEmpty()) {
        return
      }
      field = value.substring(0, value.lastIndexOf('.'))
    }

  var pluginProtocol: Int = 2
    set(value) {
      field = value
      if (value < Protocol.ProtocolVersionNumber) {
        apiOutOfDate = true
      }
    }

  @PlayerState.State
  var playState: String = PlayerState.UNDEFINED

  @Repeat.Mode
  var repeat: String
    private set

  init {
    repeat = Repeat.NONE
    rating = 0f

    lfmStatus = LfmRating.NORMAL
    pluginVersion = ""
  }

  fun setLfmRating(rating: String) {
    lfmStatus = when (rating) {
      "Love" -> LfmRating.LOVED
      "Ban" -> LfmRating.BANNED
      else -> LfmRating.NORMAL
    }
  }

  fun setRepeatState(repeat: String) {
    this.repeat = when {
      Protocol.ALL.equals(repeat, ignoreCase = true) -> Repeat.ALL
      Protocol.ONE.equals(repeat, ignoreCase = true) -> Repeat.ONE
      else -> Repeat.NONE
    }
  }
}
