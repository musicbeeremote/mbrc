package com.kelsos.mbrc.model

import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.PlayerState.STOPPED
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
import com.kelsos.mbrc.events.ui.LfmRatingChanged
import com.kelsos.mbrc.events.ui.PlayStateChange
import com.kelsos.mbrc.events.ui.RatingChanged
import com.kelsos.mbrc.events.ui.RepeatChange
import com.kelsos.mbrc.events.ui.ScrobbleChange
import com.kelsos.mbrc.events.ui.ShuffleChange
import com.kelsos.mbrc.events.ui.ShuffleChange.ShuffleState
import com.kelsos.mbrc.events.ui.VolumeChange
import com.kelsos.mbrc.repository.ModelCache
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.io.FileNotFoundException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainDataModel
@Inject
constructor(private val bus: RxBus,
            private val cache: ModelCache) {

  private var disposable: Disposable? = null
  private var _trackInfo: TrackInfo = TrackInfo()
  private var _coverPath: String = ""
  private var onPluginOutOfDate: (() -> Unit)? = null

  init {
    restoreStated()
  }

  private fun restoreStated() {
    cache.restoreInfo().subscribe({ trackInfo = it }, { onLoadError(it) })
    cache.restoreCover().subscribe({ coverPath = it }, { onLoadError(it) })
  }

  private fun onLoadError(it: Throwable?) {
    if (it is FileNotFoundException) {
      Timber.v("No state was previously stored")
    } else {
      Timber.v(it, "Error while loading the state")
    }
  }

  fun setOnPluginOutOfDate(method: (() -> Unit)?) {
    this.onPluginOutOfDate = method
  }

  var rating: Float = 0f
    set(value) {
      field = value
      bus.post(RatingChanged(field))
    }

  var volume: Int = 0
    get
    set(value) {
      if (value != field) {
        field = value
        bus.post(VolumeChange(field))
      }
    }

  @ShuffleState var shuffle: String = ShuffleChange.OFF
    set(value) {
      field = value
      bus.post(ShuffleChange(value))
    }

  var isScrobblingEnabled: Boolean = false
    set(value) {
      field = value
      bus.post(ScrobbleChange(field))
    }

  var isMute: Boolean = false
    set(value) {
      field = value
      bus.post(if (value) VolumeChange() else VolumeChange(volume))
    }


  var lfmStatus: LfmStatus = LfmStatus.NORMAL
    private set

  var pluginVersion: String = "1.0.0"
    set(value) {
      if (value.isNullOrEmpty()) {
        return
      }
      field = value.substring(0, value.lastIndexOf('.'))
      bus.post(MessageEvent(ProtocolEventType.PluginVersionCheck))
    }

  var pluginProtocol: Int = 2
    set(value) {
      field = value
      if (value < Protocol.ProtocolVersionNumber) {
        apiOutOfDate = true
        Completable.fromCallable { onPluginOutOfDate?.invoke() }
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({ })

      }
    }


  @State var playState: String = PlayerState.UNDEFINED
    set(value) {
      disposable?.dispose()

      @State val newState: String =
          when {
            Const.PLAYING.equals(value, ignoreCase = true) -> PlayerState.PLAYING
            Const.STOPPED.equals(value, ignoreCase = true) -> PlayerState.STOPPED
            Const.PAUSED.equals(value, ignoreCase = true) -> PlayerState.PAUSED
            else -> PlayerState.UNDEFINED
          }

      field = newState

      if (field != STOPPED) {
        bus.post(PlayStateChange(field))
      } else {
        disposable = Completable.timer(800, TimeUnit.MILLISECONDS).subscribe { bus.post(PlayStateChange(field)) }
      }
    }

  @Mode
  var repeat: String
    private set

  init {
    repeat = Repeat.NONE
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

  fun setRepeatState(repeat: String) {
    this.repeat = when {
      Protocol.ALL.equals(repeat, ignoreCase = true) -> Repeat.ALL
      Protocol.ONE.equals(repeat, ignoreCase = true) -> Repeat.ONE
      else -> Repeat.NONE
    }

    bus.post(RepeatChange(this.repeat))
  }


  var trackInfo: TrackInfo
    get() {
      return _trackInfo
    }
    set(value) {
      _trackInfo = value
      cache.persistInfo(value).subscribe({
        Timber.v("Playing track info successfully persisted")
      }) {
        Timber.v(it, "Failed to perist the playing track info")
      }
    }

  var coverPath: String
    get() {
      return _coverPath
    }
    set(value) {
      _coverPath = value
      cache.persistCover(value).subscribe({
        Timber.v("Playing track info successfully persisted")
      }) {
        Timber.v(it, "Failed to perist the playing track info")
      }
    }


  var apiOutOfDate: Boolean = false
    get
    private set

}

