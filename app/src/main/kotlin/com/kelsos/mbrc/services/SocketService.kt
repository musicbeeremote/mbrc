package com.kelsos.mbrc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.SocketAction.Action
import com.kelsos.mbrc.annotations.SocketAction.RESET
import com.kelsos.mbrc.annotations.SocketAction.RETRY
import com.kelsos.mbrc.annotations.SocketAction.START
import com.kelsos.mbrc.annotations.SocketAction.STOP
import com.kelsos.mbrc.annotations.SocketAction.TERMINATE
import com.kelsos.mbrc.constants.ApplicationEvents
import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.data.ConnectionSettings
import com.kelsos.mbrc.data.SocketMessage
import com.kelsos.mbrc.events.DefaultSettingsChangedEvent
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.NotifyUser
import com.kelsos.mbrc.mappers.InetAddressMapper
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.utilities.SocketActivityChecker
import com.kelsos.mbrc.utilities.SocketActivityChecker.PingTimeoutListener
import rx.Completable
import rx.Subscription
import timber.log.Timber
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
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
constructor(
    private val activityChecker: SocketActivityChecker,
    private val bus: RxBus,
    private val mapper: ObjectMapper,
    private val connectionRepository: ConnectionRepository
) : PingTimeoutListener {
  private var numOfRetries: Int = 0
  private var shouldStop: Boolean = false
  private var socket: Socket? = null
  private var output: PrintWriter? = null
  private val executor = Executors.newSingleThreadExecutor { Thread(it, "socket-thread") }

  private var subscription: Subscription? = null

  init {
    startSocket()
    numOfRetries = 0
    shouldStop = false
    socketManager(START)
    bus.register(this, DefaultSettingsChangedEvent::class.java) { socketManager(RESET) }
  }

  private fun startSocket() {
    if (subscription != null && !subscription!!.isUnsubscribed) {
      Timber.v("A subscription is already active")
      return
    }

    subscription = Completable.timer(DELAY.toLong(), TimeUnit.SECONDS).subscribe({
      val connectionSettings = connectionRepository.default

      if (connectionSettings == null) {
        socketManager(STOP)
        return@subscribe
      }

      Timber.v("Attempting connection on %s", connectionSettings)
      executor.execute(SocketConnection(connectionSettings))
      numOfRetries++
    }) { Timber.v(it, "Failed") }
  }

  fun socketManager(@Action action: Int) {
    when (action) {
      RESET -> {
        startSocket()
        cleanupSocket()
        shouldStop = false
        numOfRetries = 0
      }
      START -> {
        if (isConnected()) {
          return
        }
        startSocket()
      }
      RETRY -> {
        startSocket()
        cleanupSocket()

        if (shouldStop) {
          shouldStop = false
          numOfRetries = 0
          return
        }
      }
      STOP -> shouldStop = true
      TERMINATE -> {
        subscription?.unsubscribe()
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
  private fun isConnected(): Boolean {
    return socket?.isConnected ?: false
  }

  private fun cleanupSocket() {
    Timber.v("Socket cleanup")
    if (!isConnected()) {
      return
    }
    try {
      output?.flush()
      output?.close()
      output = null
      socket?.close()
      socket = null
    } catch (ignore: IOException) {

    }

  }

  @Synchronized fun sendData(message: SocketMessage) {
    try {
      if (isConnected()) {
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
    socketManager(RESET)
  }

  private inner class SocketConnection internal constructor(connectionSettings: ConnectionSettings) : Runnable {
    private val socketAddress: SocketAddress?
    private val mapper: InetAddressMapper = InetAddressMapper()

    init {
      socketAddress = mapper.map(connectionSettings)
    }

    override fun run() {
      Timber.v("Socket Start")
      bus.post(MessageEvent(ApplicationEvents.SocketHandshakeUpdate, false))
      if (null == socketAddress) {
        return
      }
      val input: BufferedReader
      try {
        socket = Socket()
        socket!!.connect(socketAddress)
        val out = OutputStreamWriter(socket!!.outputStream, Const.UTF_8)
        output = PrintWriter(BufferedWriter(out, SOCKET_BUFFER), true)
        val inputStreamReader = InputStreamReader(socket!!.inputStream, Const.UTF_8)
        input = BufferedReader(inputStreamReader, SOCKET_BUFFER)

        val socketStatus = socket!!.isConnected.toString()

        bus.post(MessageEvent(ApplicationEvents.SocketStatusChanged, socketStatus))
        activityChecker.start()
        activityChecker.setPingTimeoutListener(this@SocketService)
        while (socket!!.isConnected) {
          try {
            val incoming = input.readLine() ?: throw IOException("no data")
            if (incoming.isNotEmpty()) {
              bus.post(MessageEvent(ApplicationEvents.SocketDataAvailable, incoming))
            }
          } catch (e: IOException) {
            input.close()
            socket?.close()
            throw e
          }
        }
      } catch (e: SocketTimeoutException) {
        bus.post(NotifyUser(R.string.notification_connection_timeout))
        Timber.v(e)
      } catch (e: SocketException) {
        bus.post(NotifyUser(e.toString().substring(26)))
        Timber.v(e)
      } catch (ignored: IOException) {
        Timber.v(ignored, "IO")
      } catch (npe: NullPointerException) {
        Timber.d(npe, "NPE")
      } finally {
        output?.close()
        activityChecker.stop()
        activityChecker.setPingTimeoutListener(null)

        socket = null

        bus.post(MessageEvent(ApplicationEvents.SocketStatusChanged, false))
        if (numOfRetries < MAX_RETRIES) {
          Timber.d("Trying to reconnect. Try %d of %d", numOfRetries, MAX_RETRIES)
          socketManager(RETRY)
        }
        Timber.d("Socket closed")
      }
    }
  }

  companion object {
    private val DELAY = 3
    private val MAX_RETRIES = 3
    private val SOCKET_BUFFER = 2 * 4096
  }
}
