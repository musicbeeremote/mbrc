package com.kelsos.mbrc.net

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Singleton
import com.kelsos.mbrc.annotations.Connection
import com.kelsos.mbrc.dto.WebSocketMessage
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.utilities.RxBus
import com.kelsos.mbrc.utilities.SettingsManager
import okhttp3.*
import okhttp3.ws.WebSocket
import okhttp3.ws.WebSocketCall
import okhttp3.ws.WebSocketListener
import okio.Buffer
import rx.Observable
import rx.Subscription
import rx.subjects.PublishSubject
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Singleton class SocketService
@Inject
constructor(private val settingsManager: SettingsManager, private val mapper: ObjectMapper, client: OkHttpClient)
: WebSocketListener {
  private val messagePublisher: PublishSubject<String>
  private val client: OkHttpClient
  private var connected: Boolean = false
  private val executor = Executors.newSingleThreadExecutor()
  private var subscription: Subscription? = null
  @Inject private lateinit var rxBus: RxBus
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

    settingsManager.default.filter { !it.address.isNullOrEmpty() || it.port == 0 }
        .map {
          HttpUrl.Builder()
              .scheme("http")
              .host(it.address)
              .port(it.port)
              .build()
        }.subscribe({
      val request = Request.Builder().url(it).build()

      Timber.v("[WebSocket] attempting to connect to [%s]", it)
      WebSocketCall.create(client, request).enqueue(this)
    }) { Timber.e(it, "While connecting to the websocket") }
  }

  @Throws(IOException::class)
  private fun processIncoming(incoming: String) {
    val message = mapper.readValue(incoming, WebSocketMessage::class.java)

    if (Notification.CLIENT_NOT_ALLOWED == message.message) {
      return
    }

    rxBus.post(message)
    Timber.v("[Incoming] %s", message)
  }

  override fun onOpen(webSocket: WebSocket, response: Response) {
    this.webSocket = webSocket
    this.connected = true
    rxBus.post(ConnectionStatusChangeEvent(Connection.ON))
    val message = "{\"message\":\"connected\"}"
    Send(webSocket, message)

    stopPing()
    startPing(webSocket)
  }

  fun startPing(webSocket: WebSocket) {
    subscription = Observable.interval(15, TimeUnit.SECONDS).subscribe {
      try {
        webSocket.sendPing(Buffer())
        Timber.v("send ping")
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }
  }

  fun stopPing() {
    subscription?.unsubscribe()
  }

  private fun Send(webSocket: WebSocket, message: String) {
    executor.execute {
      try {
        webSocket.sendMessage(RequestBody.create(WebSocket.TEXT, message.toByteArray()))
      } catch (e: IOException) {
        Timber.e(e, "Failed to send the message")
      }
    }
  }

  override fun onFailure(e: IOException, response: Response?) {
    stopPing()
    this.connected = false
    Timber.e(e, "[Websocket] io ex")
    rxBus.post(ConnectionStatusChangeEvent(Connection.OFF))
  }

  @Throws(IOException::class)
  override fun onMessage(responseBody: ResponseBody) {
    messagePublisher.onNext(responseBody.string())
  }

  override fun onPong(payload: Buffer?) {
    Timber.v("pong")
  }

  override fun onClose(code: Int, reason: String) {
    this.connected = false
    subscription?.unsubscribe()
    webSocket = null
    Timber.v("[Websocket] closing (%d) %s", code, reason)
    rxBus.post(ConnectionStatusChangeEvent(Connection.OFF))
  }

  fun disconnect() {
    stopPing()

    try {
      webSocket?.close(1000, "Disconnecting")
    } catch (e: IOException) {
      Timber.v(e, "While closing the websocket")
    }
  }
}
