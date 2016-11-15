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
import com.kelsos.mbrc.repository.TrackCache
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainDataModel
@Inject
constructor(private val bus: RxBus,
            private val cache: TrackCache) {

    init {
        cache.restore().subscribe({
            trackInfo = it
        }, {
            Timber.v(it, "No state was previously stored")
        })
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

    @State var playState: String = PlayerState.UNDEFINED
        set(value) {
            @State val newState: String
            if (Const.PLAYING.equals(value, ignoreCase = true)) {
                newState = PlayerState.PLAYING
            } else if (Const.STOPPED.equals(value, ignoreCase = true)) {
                newState = PlayerState.STOPPED
            } else if (Const.PAUSED.equals(value, ignoreCase = true)) {
                newState = PlayerState.PAUSED
            } else {
                newState = PlayerState.UNDEFINED
            }

            field = newState

            bus.post(PlayStateChange(field))
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

    fun updateRemoteClient() {
        bus.post(RemoteClientMetaData(trackInfo))
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

    var trackInfo: TrackInfo = TrackInfo()
        get
        set(value) {
            field = value
            val event = TrackInfoChangeEvent(value)
            bus.post(event)
            updateRemoteClient()
            cache.persist(value).subscribe({
                Timber.v("Playing track info successfully persisted")
            }) {
                Timber.v(it, "Failed to perist the playing track info")
            }
        }

}

