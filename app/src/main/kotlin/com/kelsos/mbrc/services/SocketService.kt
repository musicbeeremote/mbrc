package com.kelsos.mbrc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.R
import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.constants.SocketEventType
import com.kelsos.mbrc.data.ConnectionSettings
import com.kelsos.mbrc.data.SocketMessage
import com.kelsos.mbrc.enums.SocketAction
import com.kelsos.mbrc.events.DefaultSettingsChangedEvent
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.NotifyUser
import com.kelsos.mbrc.mappers.InetAddressMapper
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.utilities.SocketActivityChecker
import rx.Completable
import rx.Subscription
import timber.log.Timber
import java.io.*
import java.net.Socket
import java.net.SocketAddress
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketService
@Inject
constructor(private val activityChecker: SocketActivityChecker,
            private val bus: RxBus,
            private val mapper: ObjectMapper,
            private val connectionRepository: ConnectionRepository) : SocketActivityChecker.PingTimeoutListener {
  private var numOfRetries: Int = 0
  private var shouldStop: Boolean = false
  private var socket: Socket? = null
  private var output: PrintWriter? = null
  private val executor = Executors.newSingleThreadExecutor()

  private var subscription: Subscription? = null

  init {

    startSocket()
    numOfRetries = 0
    shouldStop = false
    socketManager(SocketAction.START)
    bus.register(this, DefaultSettingsChangedEvent::class.java) { event -> socketManager(SocketAction.RESET) }
  }

  private fun startSocket() {
    if (subscription != null && !subscription!!.isUnsubscribed) {
      Timber.v("A subscription is already active")
      return
    }

    subscription = Completable.timer(DELAY.toLong(), TimeUnit.SECONDS).subscribe({
      val connectionSettings = connectionRepository.default

      if (connectionSettings == null) {
        socketManager(SocketAction.STOP)
        return@subscribe
      }
      executor.execute(SocketConnection(connectionSettings))
      numOfRetries++
    }) { throwable -> Timber.v(throwable, "Failed") }
  }

  fun socketManager(action: SocketAction) {
    when (action) {
      SocketAction.RESET -> {
        startSocket()
        cleanupSocket()
        shouldStop = false
        numOfRetries = 0
      }
      SocketAction.START -> {
        startSocket()
        if (sIsConnected()) {
          return
        }
      }
      SocketAction.RETRY -> {
        startSocket()
        cleanupSocket()

        if (shouldStop) {
          shouldStop = false
          numOfRetries = 0
          return
        }
      }
      SocketAction.STOP -> shouldStop = true
      SocketAction.TERMINATE -> {
        if (subscription != null && !subscription!!.isUnsubscribed) {
          subscription!!.unsubscribe()
        }
        shouldStop = true
        cleanupSocket()
      }
      else -> {
      }
    }
  }

  /**
   * Returns true if the socket is not null and it is connected, false in any other case.

   * @return Boolean
   */
  private fun sIsConnected(): Boolean {
    return socket != null && socket!!.isConnected
  }

  private fun cleanupSocket() {
    Timber.v("Socket cleanup")
    if (!sIsConnected()) {
      return
    }
    try {
      if (output != null) {
        output!!.flush()
        output!!.close()
        output = null
      }
      socket!!.close()
      socket = null
    } catch (ignore: IOException) {

    }

  }

  @Synchronized fun sendData(message: SocketMessage) {
    try {
      if (sIsConnected()) {
        output!!.print(mapper.writeValueAsString(message) + Const.NEWLINE)
        if (output!!.checkError()) {
          throw Exception("Check error")
        }
      }
    } catch (ignored: Exception) {
      Timber.d(ignored, "Trying to send a message")
    }

  }

  override fun onTimeout() {
    Timber.v("Timeout received resetting socket")
    socketManager(SocketAction.RESET)
  }

  private inner class SocketConnection internal constructor(connectionSettings: ConnectionSettings) : Runnable {
    private val socketAddress: SocketAddress?
    private val mapper: InetAddressMapper

    init {
      mapper = InetAddressMapper()
      socketAddress = mapper.map(connectionSettings)
    }

    override fun run() {
      Timber.v("Socket Startsa")
      bus.post(MessageEvent(SocketEventType.SocketHandshakeUpdate, false))
      if (null == socketAddress) {
        return
      }
      val input: BufferedReader
      try {
        socket = Socket()
        socket!!.connect(socketAddress)
        val out = OutputStreamWriter(socket!!.outputStream, Const.UTF_8)
        output = PrintWriter(BufferedWriter(out, SOCKET_BUFFER), true)
        val `in` = InputStreamReader(socket!!.inputStream, Const.UTF_8)
        input = BufferedReader(`in`, SOCKET_BUFFER)

        val socketStatus = socket!!.isConnected.toString()

        bus.post(MessageEvent(SocketEventType.SocketStatusChanged, socketStatus))
        activityChecker.start()
        activityChecker.setPingTimeoutListener(this@SocketService)
        while (socket!!.isConnected) {
          try {
            val incoming = input.readLine() ?: throw IOException()
            if (incoming.length > 0) {
              bus.post(MessageEvent(SocketEventType.SocketDataAvailable, incoming))
            }
          } catch (e: IOException) {
            input.close()
            if (socket != null) {
              socket!!.close()
            }
            throw e
          }

        }
      } catch (e: SocketTimeoutException) {
        bus.post(NotifyUser(R.string.notification_connection_timeout))
      } catch (e: SocketException) {
        bus.post(NotifyUser(e.toString().substring(26)))
      } catch (ignored: IOException) {
      } catch (npe: NullPointerException) {
        Timber.d(npe, "NPE")
      } finally {
        if (output != null) {
          output!!.close()
        }

        activityChecker.stop()
        activityChecker.setPingTimeoutListener(null)

        socket = null

        bus.post(MessageEvent(SocketEventType.SocketStatusChanged, false))
        if (numOfRetries < MAX_RETRIES) {
          Timber.d("Trying to reconnect. Try %d of %d", numOfRetries, MAX_RETRIES)
          socketManager(SocketAction.RETRY)
        }
        Timber.d("Socket closed")
      }
    }
  }

  companion object {
    private val DELAY = 3
    private val MAX_RETRIES = 3
    private val SOCKET_BUFFER = 4096
  }
}
