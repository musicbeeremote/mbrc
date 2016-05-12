package com.kelsos.mbrc.messaging

import com.google.inject.Inject
import com.google.inject.Singleton
import com.kelsos.mbrc.constants.SocketNotification
import com.kelsos.mbrc.dto.WebSocketMessage
import com.kelsos.mbrc.events.ui.CoverChangedEvent
import com.kelsos.mbrc.events.ui.LyricsChangedEvent
import com.kelsos.mbrc.events.ui.MuteChangeEvent
import com.kelsos.mbrc.events.ui.PlayStateChange
import com.kelsos.mbrc.events.ui.RepeatChange
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent
import com.kelsos.mbrc.events.ui.VolumeChangeEvent
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.interactors.MuteInteractor
import com.kelsos.mbrc.interactors.PlayerStateInteractor
import com.kelsos.mbrc.interactors.RepeatInteractor
import com.kelsos.mbrc.interactors.TrackCoverInteractor
import com.kelsos.mbrc.interactors.TrackInfoInteractor
import com.kelsos.mbrc.interactors.TrackLyricsInteractor
import com.kelsos.mbrc.interactors.VolumeInteractor
import com.kelsos.mbrc.utilities.RxBus
import rx.Subscription
import rx.subjects.PublishSubject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

@Singleton class SocketMessageHandler
@Inject constructor(bus: RxBus) {

  private val actions: MutableMap<String, () -> Subscription>
  private val activeSubscriptions: MutableMap<String, Subscription>
  @Inject private lateinit var volumeInteractor: VolumeInteractor
  @Inject private lateinit var coverInteractor: TrackCoverInteractor
  @Inject private lateinit var lyricsInteractor: TrackLyricsInteractor
  @Inject private lateinit var trackInfoInteractor: TrackInfoInteractor
  @Inject private lateinit var repeatInteractor: RepeatInteractor
  @Inject private lateinit var playerStateInteractor: PlayerStateInteractor
  @Inject private lateinit var muteInteractor: MuteInteractor

  private val volumeThrottler = PublishSubject.create<WebSocketMessage>()

  init {
    Timber.v("Initializing Socket Handler")
    bus.register(WebSocketMessage::class.java, { this.onWebSocketMessage(it) }, false)

    volumeThrottler.throttleLast(1, TimeUnit.SECONDS).subscribe({ this.handleVolume(it) })

    actions = HashMap<String, () -> Subscription>()
    activeSubscriptions = HashMap<String, Subscription>()
    actions.put(SocketNotification.VOLUME, getVolumeAction(bus))
    actions.put(SocketNotification.COVER, getCoverAction(bus))
    actions.put(SocketNotification.LYRICS, getLyricsAction(bus))
    actions.put(SocketNotification.TRACK, getTrackAction(bus))
    actions.put(SocketNotification.PLAY_STATUS, getPlayStatusAction(bus))
    actions.put(SocketNotification.REPEAT, getRepeatAction(bus))
    actions.put(SocketNotification.MUTE, getMuteAction(bus))
  }

  fun getMuteAction(bus: RxBus): () -> Subscription = {
    muteInteractor.getMuteState().io().doOnTerminate {
      activeSubscriptions.remove(SocketNotification.MUTE)
    }.subscribe {
      bus.post(MuteChangeEvent(it))
    }
  }

  fun getRepeatAction(bus: RxBus): () -> Subscription = {
    repeatInteractor.getRepeat().io().doOnTerminate {
      activeSubscriptions.remove(SocketNotification.REPEAT)
    }.subscribe {
      bus.post(RepeatChange(it))
    }
  }

  fun getPlayStatusAction(bus: RxBus): () -> Subscription = {
    playerStateInteractor.getState().io().doOnTerminate {
      activeSubscriptions.remove(SocketNotification.PLAY_STATUS)
    }.subscribe {
      bus.post(PlayStateChange(it))
    }
  }

  fun getTrackAction(bus: RxBus): () -> Subscription = {
    trackInfoInteractor.load(true).io().doOnTerminate {
      activeSubscriptions.remove(SocketNotification.TRACK)
    }.subscribe {
      bus.post(TrackInfoChangeEvent(it))
    }
  }

  fun getLyricsAction(bus: RxBus): () -> Subscription = {
    lyricsInteractor.execute(true).io().doOnTerminate {
      activeSubscriptions.remove(SocketNotification.LYRICS)
    }.subscribe {
      bus.post(LyricsChangedEvent(it))
    }
  }

  fun getCoverAction(bus: RxBus): () -> Subscription = {
    coverInteractor.load(true).io().doOnTerminate {
      activeSubscriptions.remove(SocketNotification.COVER)
    }.subscribe {
      bus.post(CoverChangedEvent(it))
    }
  }

  fun getVolumeAction(bus: RxBus): () -> Subscription = {
    volumeInteractor.getVolume().io().doOnTerminate {
      activeSubscriptions.remove(SocketNotification.VOLUME)
    }.subscribe {
      bus.post(VolumeChangeEvent(it))
    }
  }

  private fun onWebSocketMessage(message: WebSocketMessage) {
    val action = message.message
    Timber.v("Processing $action")

    val actionSubscription = activeSubscriptions[action]
    if (actionSubscription?.isUnsubscribed ?: false) {
      Timber.v("There is already an active operation for the received action $action")
      return
    }

    if (SocketNotification.VOLUME != action) {
      handle(action)
    } else {
      volumeThrottler.onNext(message)
    }
  }

  private fun handleVolume(message: WebSocketMessage) {
    handle(message.message)
  }

  private fun handle(action: String) {
    val socketAction = actions[action]
    val subscription = socketAction?.invoke()
    subscription?.let { activeSubscriptions.put(action, it) }
  }
}
