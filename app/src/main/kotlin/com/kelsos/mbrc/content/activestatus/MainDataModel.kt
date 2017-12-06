package com.kelsos.mbrc.content.activestatus

import com.kelsos.mbrc.content.activestatus.PlayerState.STOPPED
import com.kelsos.mbrc.content.activestatus.PlayerState.State
import com.kelsos.mbrc.content.activestatus.Repeat.Mode
import com.kelsos.mbrc.content.library.tracks.TrackInfo
import com.kelsos.mbrc.events.*
import com.kelsos.mbrc.events.ShuffleChange.ShuffleState
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.ui.navigation.main.LfmRating
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


  @get:LfmRating.Rating
  @setparam:LfmRating.Rating
  var lfmStatus: Int = LfmRating.NORMAL
    private set

  var pluginVersion: String = "1.0.0"
    set(value) {
      if (value.isNullOrEmpty()) {
        return
      }
      field = value.substring(0, value.lastIndexOf('.'))
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
      field = value

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

    lfmStatus = LfmRating.NORMAL
    pluginVersion = ""
  }

  fun setLfmRating(rating: String) {
    when (rating) {
      "Love" -> lfmStatus = LfmRating.LOVED
      "Ban" -> lfmStatus = LfmRating.BANNED
      else -> lfmStatus = LfmRating.NORMAL
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
    private set

}

