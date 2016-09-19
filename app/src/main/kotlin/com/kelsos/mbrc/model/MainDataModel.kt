package com.kelsos.mbrc.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.PlayerState.State
import com.kelsos.mbrc.annotations.Repeat
import com.kelsos.mbrc.annotations.Repeat.Mode
import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.data.MusicTrack
import com.kelsos.mbrc.data.Playlist
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.enums.LfmStatus
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.*
import com.kelsos.mbrc.events.ui.ShuffleChange.ShuffleState
import rx.Observable
import rx.Subscriber
import rx.schedulers.Schedulers
import java.util.*
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
  var cover: Bitmap? = null
    private set

  var shuffle: String? = null
    private set
  var isScrobblingEnabled: Boolean = false
    private set
  var isMute: Boolean = false
    private set
  @State
  private var playState: String? = null
  var lfmStatus: LfmStatus? = null
    private set
  private var pluginVersion: String? = null
  var pluginProtocol: Double = 0.toDouble()

  @Mode
  var repeat: String? = null
    private set

  init {
    repeat = Repeat.NONE
    title = Const.EMPTY
    artist = Const.EMPTY
    album = Const.EMPTY
    year = Const.EMPTY
    volume = 100


    shuffle = OFF
    isScrobblingEnabled = false
    isMute = false
    playState = PlayerState.UNDEFINED
    cover = null
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
    return pluginVersion
  }

  fun setPluginVersion(pluginVersion: String) {
    this.pluginVersion = pluginVersion.substring(0, pluginVersion.lastIndexOf('.'))
    bus.post(MessageEvent(ProtocolEventType.PluginVersionCheck))
  }

  fun setNowPlayingList(nowPlayingList: ArrayList<MusicTrack>) {
    bus.post(NowPlayingListAvailable(nowPlayingList,
        nowPlayingList.indexOf(MusicTrack(artist, title))))
  }

  fun setRating(rating: Double) {
    this.rating = rating.toFloat()
    bus.post(RatingChanged(this.rating))
  }

  private fun updateNotification() {
    bus.post(NotificationDataAvailable(artist, title, album, cover, playState))
  }

  fun setTrackInfo(artist: String, album: String, title: String, year: String) {
    this.artist = artist
    this.album = album
    this.year = year
    this.title = title
    val event = TrackInfoChangeEvent.builder().trackInfo(trackInfo).build()
    bus.post(event)
    updateNotification()
    updateRemoteClient()
  }

  private fun updateRemoteClient() {
    bus.post(RemoteClientMetaData(artist, title, album, cover))
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

  fun setCover(base64format: String?) {
    if (base64format == null || Const.EMPTY == base64format) {
      cover = null
      bus.post(CoverChangedEvent.builder().build())
      updateNotification()
      updateRemoteClient()
    } else {
      Observable.create { subscriber: Subscriber<in Bitmap> ->
        val decodedImage = Base64.decode(base64format, Base64.DEFAULT)
        subscriber.onNext(BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size))
        subscriber.onCompleted()
      }.subscribeOn(Schedulers.io()).subscribe(Action1<Bitmap> { this.setAlbumCover(it) }) { throwable ->
        cover = null
        bus.post(CoverChangedEvent.builder().build())
      }
    }
  }

  private fun setAlbumCover(cover: Bitmap) {
    this.cover = cover
    bus.post(CoverChangedEvent.builder().withCover(cover).build())
    updateNotification()
    updateRemoteClient()
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

    bus.post(PlayStateChange.builder().state(this.playState!!).build())
    updateNotification()
  }

  fun setPlaylists(playlists: List<Playlist>) {
    bus.post(PlaylistAvailable.create(playlists))
  }

  @State
  fun getPlayState(): String {
    return playState
  }

  val trackInfo: TrackInfo
    get() = TrackInfo(artist, title, album, year)

}

