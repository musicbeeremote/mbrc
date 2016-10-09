package com.kelsos.mbrc.model

import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.PlayerState.State
import com.kelsos.mbrc.annotations.Repeat
import com.kelsos.mbrc.annotations.Repeat.Mode
import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.enums.LfmStatus
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.*
import com.kelsos.mbrc.events.ui.ShuffleChange.ShuffleState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainDataModel
@Inject
constructor(private val bus: RxBus) {
  private var rating: Float = 0.toFloat()
  var title: String
    private set
  var artist: String
    private set
  private var album: String
  private var year: String

  private var volume: Int = 0

  var shuffle: String
    private set
  var isScrobblingEnabled: Boolean = false
    private set
  var isMute: Boolean = false
    private set

  @State private var playState: String
  var lfmStatus: LfmStatus = LfmStatus.NORMAL
    private set
  private var pluginVersion: String? = null
  var pluginProtocol: Int = 2

  @Mode
  var repeat: String
    private set

  init {
    repeat = Repeat.NONE
    title = Const.EMPTY
    artist = Const.EMPTY
    album = Const.EMPTY
    year = Const.EMPTY
    volume = 100


    shuffle = ShuffleChange.OFF
    isScrobblingEnabled = false
    isMute = false
    playState = PlayerState.UNDEFINED
    rating = 0f

    lfmStatus = LfmStatus.NORMAL
    pluginVersion = Const.EMPTY
  }

  fun setLfmRating(rating: String) {
    when (rating) {
      "Love" -> lfmStatus = LfmStatus.LOVED
      "Ban" -> lfmStatus = LfmStatus.BANNED
      else -> lfmStatus = LfmStatus.NORMAL
    }

    bus.post(LfmRatingChanged(lfmStatus))
  }

  fun getPluginVersion(): String {
    return pluginVersion ?: "0.0"
  }

  fun setPluginVersion(pluginVersion: String) {
    this.pluginVersion = pluginVersion.substring(0, pluginVersion.lastIndexOf('.'))
    bus.post(MessageEvent(ProtocolEventType.PluginVersionCheck))
  }

  fun setRating(rating: Double) {
    this.rating = rating.toFloat()
    bus.post(RatingChanged(this.rating))
  }

  private fun updateNotification() {
    bus.post(NotificationDataAvailable(artist, title, album, playState))
  }

  private fun updateRemoteClient() {
    bus.post(RemoteClientMetaData(artist, title, album))
  }

  fun getVolume(): Int {
    return this.volume
  }

  fun setVolume(volume: Int) {
    if (volume != this.volume) {
      this.volume = volume
      bus.post(VolumeChange(this.volume))
    }
  }

  fun setRepeatState(repeat: String) {
    if (Protocol.ALL.equals(repeat, ignoreCase = true)) {
      this.repeat = Repeat.ALL
    } else if (Protocol.ONE.equals(repeat, ignoreCase = true)) {
      this.repeat = Repeat.ONE
    } else {
      this.repeat = Repeat.NONE
    }

    bus.post(RepeatChange(this.repeat))
  }

  fun setShuffleState(@ShuffleState shuffleState: String) {
    shuffle = shuffleState
    bus.post(ShuffleChange(shuffle))
  }

  fun setScrobbleState(scrobbleButtonActive: Boolean) {
    isScrobblingEnabled = scrobbleButtonActive
    bus.post(ScrobbleChange(isScrobblingEnabled))
  }

  fun setMuteState(isMuteActive: Boolean) {
    this.isMute = isMuteActive
    bus.post(if (isMuteActive) VolumeChange() else VolumeChange(volume))
  }

  fun setPlayState(playState: String) {
    @State val newState: String
    if (Const.PLAYING.equals(playState, ignoreCase = true)) {
      newState = PlayerState.PLAYING
    } else if (Const.STOPPED.equals(playState, ignoreCase = true)) {
      newState = PlayerState.STOPPED
    } else if (Const.PAUSED.equals(playState, ignoreCase = true)) {
      newState = PlayerState.PAUSED
    } else {
      newState = PlayerState.UNDEFINED
    }

    this.playState = newState

    bus.post(PlayStateChange(this.playState))
    updateNotification()
  }

  @State
  fun getPlayState(): String {
    return playState
  }

  var trackInfo: TrackInfo = TrackInfo()
    get
    set(value) {
      field = value
      val event = TrackInfoChangeEvent(value)
      bus.post(event)
      updateNotification()
      updateRemoteClient()
    }

}

