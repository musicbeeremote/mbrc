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
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
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
  private var socket: Socket? = null
  private var output: PrintWriter? = null
  private val executor = Executors.newSingleThreadExecutor { Thread(it, "socket-thread") }

  private var disposable: Disposable? = null

  init {
    startSocket()
    numOfRetries = 0
    shouldStop = false
    socketManager(START)
    bus.register(this, DefaultSettingsChangedEvent::class.java) { socketManager(RESET) }
    bus.register(this, SendProtocolMessage::class.java) { sendData(it.message) }
    bus.register(this, UserAction::class.java) {
      sendData(SocketMessage.create(it.context, it.data))
    }
  }

  private fun startSocket() {
    disposable?.let {
      if (!it.isDisposed) {
        return
      }
    }

    disposable = Completable.timer(DELAY.toLong(), TimeUnit.SECONDS).subscribe({
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

  fun socketManager(@Action action: Int) = when (action) {
    RESET -> reset()
    START -> start()
    RETRY -> retry()
    STOP -> stop()
    TERMINATE -> terminate()
    else -> throw IllegalArgumentException("There is no such action")
  }

  private fun terminate() {
    disposable?.dispose()
    shouldStop = true
    cleanupSocket()
  }

  private fun stop() {
    shouldStop = true
  }

  private fun retry() {
    startSocket()
    cleanupSocket()

    if (shouldStop) {
      shouldStop = false
      numOfRetries = 0
      return
    }
  }

  private fun start() {
    if (isConnected()) {
      return
    }
    startSocket()
  }

  private fun reset() {
    startSocket()
    cleanupSocket()
    shouldStop = false
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
    Timber.v("Socket cleanup")
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
    async(CommonPool) {
      if (isConnected()) {
        val messageString = "${mapper.writeValueAsString(message)}\r\n"
        Timber.v("Sending -> $messageString")
        writeToSocket(messageString)
      }
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
    Timber.v("Timeout received resetting socket")
    socketManager(RESET)
  }

  private inner class SocketConnection
  internal constructor(connectionSettings: ConnectionSettingsEntity) : Runnable {
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

        bus.post(SocketStatusChangedEvent(false))
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