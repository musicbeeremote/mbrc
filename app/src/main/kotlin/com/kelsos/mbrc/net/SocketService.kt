package com.kelsos.mbrc.net

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.annotations.Connection
import com.kelsos.mbrc.dto.WebSocketMessage
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.repository.ConnectionRepository
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class SocketService
@Inject
constructor(private val connectionRepository: ConnectionRepository,
            private val mapper: ObjectMapper,
            client: OkHttpClient,
            private val rxBus: RxBus)
  : WebSocketListener {
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
          .scheme("http")
          .host(it.address)
          .port(it.port)
          .build()
      val request = Request.Builder().url(url).build()

      Timber.v("[WebSocket] attempting to connect to [%s]", it)
      WebSocketCall.create(client, request).enqueue(this)
    }
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
    Timber.e("Socket failed with message: ${e.message}")
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
