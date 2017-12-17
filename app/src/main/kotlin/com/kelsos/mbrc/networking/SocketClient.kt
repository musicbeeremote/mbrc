package com.kelsos.mbrc.networking

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.R
import com.kelsos.mbrc.events.NotifyUser
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.networking.SocketAction.Action
import com.kelsos.mbrc.networking.SocketAction.RESET
import com.kelsos.mbrc.networking.SocketAction.RETRY
import com.kelsos.mbrc.networking.SocketAction.START
import com.kelsos.mbrc.networking.SocketAction.STOP
import com.kelsos.mbrc.networking.SocketAction.TERMINATE
import com.kelsos.mbrc.networking.SocketActivityChecker.PingTimeoutListener
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.networking.connections.InetAddressMapper
import com.kelsos.mbrc.preferences.DefaultSettingsChangedEvent
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
import java.net.NoRouteToHostException
import java.net.Socket
import java.net.SocketAddress
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketClient
@Inject
constructor(
  private val activityChecker: SocketActivityChecker,
  private val bus: RxBus,
  private val mapper: ObjectMapper,
  private val connectionRepository: ConnectionRepository
) : PingTimeoutListener {
  private var numOfRetries: Int = 0
  private var shouldStop: Boolean = false
  private var connecting: Boolean = false
  private var socket: Socket? = null
  private var output: PrintWriter? = null
  private val executor = Executors.newSingleThreadExecutor { Thread(it, "socket-thread") }
  private val job = SupervisorJob()
  private val context = job + executor.asCoroutineDispatcher()
  private val scope = CoroutineScope(context)

  init {
    resetState()
    bus.register(this, DefaultSettingsChangedEvent::class.java) { socketManager(RESET) }
    bus.register(this, SendProtocolMessage::class.java) { sendData(it.message) }
    bus.register(this, UserAction::class.java) {
      sendData(SocketMessage.create(it.context, it.data))
    }
  }

  private fun startSocket() {
    context.cancelChildren()
    scope.launch {
      Timber.d("Trying to connect. Try %d of %d", (numOfRetries + 1), MAX_RETRIES)
      try {
        delay(3000)
        val settings = connectionRepository.getDefault()
        if (settings == null) {
          socketManager(STOP)
          // TODO: Terminate Service
          return@launch
        }

        Timber.v("Connecting to mbrc://${settings.address}:${settings.port}")
        executor.execute(SocketConnection(settings))
        numOfRetries++
      } catch (e: Exception) {
        Timber.v(e, "Connection failed")
      }
    }
  }

  fun socketManager(@Action action: Int) = when (action) {
    RESET -> reset()
    START -> start()
    RETRY -> retry()
    STOP -> stop()
    TERMINATE -> terminate()
    else -> throw IllegalArgumentException("There is no such action")
  }

  private fun terminate() {
    context.cancelChildren()
    shouldStop = true
    cleanupSocket()
    resetState()
  }

  private fun stop() {
    shouldStop = true
  }

  private fun retry() {
    startSocket()
    cleanupSocket()

    if (shouldStop) {
      resetState()
      return
    }
  }

  private fun start() {
    if (isConnected() || connecting) {
      return
    }
    connecting = true
    startSocket()
  }

  private fun reset() {
    shouldStop = false
    resetState()
    startSocket()
    cleanupSocket()
  }

  private fun resetState() {
    connecting = false
    numOfRetries = 0
  }

  /**
   * Returns true if the socket is not null and it is connected, false in any other case.

   * @return Boolean
   */
  private fun isConnected(): Boolean {
    return socket?.isConnected ?: false
  }

  private fun cleanupSocket() {
    Timber.v("Cleaning up socket")
    if (!isConnected()) {
      return
    }
    try {
      output?.let {
        it.flush()
        it.close()
      }
      output = null
      socket?.close()
      socket = null
    } catch (ignore: IOException) {
    }
  }

  @Synchronized
  fun sendData(message: SocketMessage) {
    if (!isConnected()) {
      return
    }
    try {
      val messageString = "${mapper.writeValueAsString(message)}\r\n"
      Timber.v("Sending -> $messageString")
      writeToSocket(message = messageString)
    } catch (ignored: Exception) {
      Timber.d(ignored, "Send failed")
    }
  }

  private fun writeToSocket(message: String) {
    val output = output ?: throw IOException("output was null")
    output.print(message)

    if (output.checkError()) {
      throw IOException("Output stream encountered an error")
    }
  }

  override fun onTimeout() {
    Timber.v("Timeout received. Resetting socket")
    socketManager(RESET)
  }

  private inner class SocketConnection(connectionSettings: ConnectionSettingsEntity) : Runnable {
    private val socketAddress: SocketAddress?
    private val mapper: InetAddressMapper = InetAddressMapper()

    init {
      socketAddress = mapper.map(connectionSettings)
    }

    override fun run() {
      Timber.v("Socket Start")
      bus.post(SocketHandshakeUpdateEvent(false))
      if (null == socketAddress) {
        return
      }
      val input: BufferedReader
      try {
        socket = Socket().apply {
          soTimeout = 20000
          connect(socketAddress)
        }

        val outputStream = socket?.outputStream
        val out = OutputStreamWriter(outputStream, "UTF-8")
        output = PrintWriter(BufferedWriter(out, SOCKET_BUFFER), true)

        val inputStream = socket?.inputStream
        val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
        input = BufferedReader(inputStreamReader, SOCKET_BUFFER)

        val socketStatus = socket!!.isConnected

        bus.post(SocketStatusChangedEvent(socketStatus))
        activityChecker.start()
        activityChecker.setPingTimeoutListener(this@SocketClient)
        while (socket!!.isConnected) {
          try {
            val incoming = input.readLine() ?: throw IOException("no data")
            if (incoming.isNotEmpty()) {
              bus.post(SocketDataAvailableEvent(incoming))
            }
          } catch (e: IOException) {
            input.close()
            socket?.close()
            throw e
          }
        }
      } catch (e: SocketTimeoutException) {
        bus.post(NotifyUser(R.string.notification_connection_timeout))
      } catch (e: NoRouteToHostException) {
        bus.post(NotifyUser(R.string.notification_no_route))
      } catch (e: SocketException) {
        bus.post(NotifyUser(e.toString().substring(26)))
        Timber.v(e)
      } catch (ignored: IOException) {
        Timber.v(ignored, "IO")
      } catch (npe: NullPointerException) {
        Timber.e(npe, "NPE")
      } finally {
        output?.close()
        activityChecker.stop()
        activityChecker.setPingTimeoutListener(null)

        socket = null

        Timber.d("Socket closed")
        bus.post(SocketStatusChangedEvent(false))
        if (numOfRetries < MAX_RETRIES && !shouldStop) {
          socketManager(RETRY)
        } else {
          // TODO terminate service
        }
      }
    }
  }

  companion object {
    private const val MAX_RETRIES = 3
    private const val SOCKET_BUFFER = 2 * 4096
  }
}
