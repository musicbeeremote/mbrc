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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
  private val job = SupervisorJob()
  private val context = job + executor.asCoroutineDispatcher()
  private val scope = CoroutineScope(context)

  init {
    startSocket()
    numOfRetries = 0
    shouldStop = false
    socketManager(START)
    bus.register(this, DefaultSettingsChangedEvent::class.java) { socketManager(RESET) }
  }

  private fun startSocket() {
    context.cancelChildren()
    scope.launch {
      try {
        delay(3000)
        val settings = connectionRepository.getDefault()
        if (settings == null) {
          socketManager(STOP)
          return@launch
        }

        Timber.v("Starting connection to mbrc://${settings.address}:${settings.port}", )
        executor.execute(SocketConnection(settings))
        numOfRetries++
      } catch (e: Exception) {
        Timber.v(e, "Connection was unsuccessful")
      }
    }
  }

  fun socketManager(@Action action: Int) {
    Timber.v("received action $action")
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
        context.cancelChildren()
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

  @Synchronized
  fun sendData(message: SocketMessage) {
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

  private inner class SocketConnection(connectionSettings: ConnectionSettings) :
    Runnable {
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
    private const val MAX_RETRIES = 3
    private const val SOCKET_BUFFER = 2 * 4096
  }
}
