package com.kelsos.mbrc.messaging

import com.kelsos.mbrc.constants.SocketNotification
import com.kelsos.mbrc.dto.WebSocketMessage
import com.kelsos.mbrc.events.bus.RxBus
import rx.Subscription
import rx.subjects.PublishSubject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class SocketMessageHandler
@Inject constructor(bus: RxBus) {

  private val actions: MutableMap<String, () -> Subscription>
  private val activeSubscriptions: MutableMap<String, Subscription>

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
    TODO()
  }

  fun getRepeatAction(bus: RxBus): () -> Subscription = {
    TODO()
  }

  fun getPlayStatusAction(bus: RxBus): () -> Subscription = {
    TODO()
  }

  fun getTrackAction(bus: RxBus): () -> Subscription = {
    TODO()
  }

  fun getLyricsAction(bus: RxBus): () -> Subscription = {
    TODO()
  }

  fun getCoverAction(bus: RxBus): () -> Subscription = {
    TODO()
  }

  fun getVolumeAction(bus: RxBus): () -> Subscription = {
    TODO()
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
