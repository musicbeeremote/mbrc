package com.kelsos.mbrc.net

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.annotations.Connection
import com.kelsos.mbrc.dto.WebSocketMessage
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.repository.ConnectionRepository
import okhttp3.*
import rx.Subscription
import rx.subjects.PublishSubject
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class SocketService
@Inject
constructor(private val connectionRepository: ConnectionRepository,
            private val mapper: ObjectMapper,
            client: OkHttpClient,
            private val bus: RxBus)
  : WebSocketListener() {
  private val messagePublisher: PublishSubject<String>
  private val client: OkHttpClient
  private var connected: Boolean = false
  private val executor = Executors.newSingleThreadExecutor()
  private var subscription: Subscription? = null
  private var webSocket: WebSocket? = null

  init {
    val newBuilder = client.newBuilder()
    newBuilder.interceptors().clear()
    this.client = newBuilder.build()

    messagePublisher = PublishSubject.create<String>()
    messagePublisher.io().subscribe {
      try {
        processIncoming(it)
      } catch (e: IOException) {
        Timber.v(e, "Failed with incoming message");
      }
    }
  }

  fun startWebSocket() {
    if (connected) {
      return
    }

    connectionRepository.default?.let {
      val url = HttpUrl.Builder()
          .scheme("ws")
          .host(it.address)
          .port(it.port)
          .build()
      val request = Request.Builder().url(url).build()

      Timber.v("[WebSocket] attempting to connect to [%s]", it)

      client.newWebSocket(request, this)
    }
  }

  @Throws(IOException::class)
  private fun processIncoming(incoming: String) {
    val message = mapper.readValue(incoming, WebSocketMessage::class.java)

    if (Notification.CLIENT_NOT_ALLOWED == message.message) {
      return
    }

    bus.post(message)
    Timber.v("[Incoming] %s", message)
  }

  override fun onOpen(webSocket: WebSocket?, response: Response?) {
    super.onOpen(webSocket, response)
    this.webSocket = webSocket
    this.connected = true
    bus.post(ConnectionStatusChangeEvent(Connection.ON))
    val message = "{\"message\":\"connected\"}"
    webSocket?.let {
      Send(it, message)
    }

  }

  private fun Send(webSocket: WebSocket, message: String) {
    executor.execute {
      try {
        webSocket.send(message)
      } catch (e: IOException) {
        Timber.e(e, "Failed to send the message")
      }
    }
  }

  override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
    super.onFailure(webSocket, t, response)
    this.connected = false
    Timber.e("Socket failed with message: ${t?.message}")
    bus.post(ConnectionStatusChangeEvent(Connection.OFF))
  }


  override fun onMessage(webSocket: WebSocket?, text: String?) {
    super.onMessage(webSocket, text)
    messagePublisher.onNext(text)
  }

  override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
    super.onClosed(webSocket, code, reason)
    this.connected = false
    subscription?.unsubscribe()
    this.webSocket = null
    Timber.v("[Websocket] closing (%d) %s", code, reason)
    bus.post(ConnectionStatusChangeEvent(Connection.OFF))
  }


  fun disconnect() {
    try {
      webSocket?.close(1000, "Disconnecting")
    } catch (e: IOException) {
      Timber.v(e, "While closing the websocket")
    }
  }
}
