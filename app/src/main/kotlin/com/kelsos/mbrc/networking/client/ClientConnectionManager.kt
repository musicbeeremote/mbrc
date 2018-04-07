package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.networking.SocketActivityChecker
import com.kelsos.mbrc.networking.SocketActivityChecker.PingTimeoutListener
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.networking.connections.InetAddressMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
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
import java.util.concurrent.Executors
import javax.inject.Inject

class ClientConnectionManager
@Inject
constructor(
  private val activityChecker: SocketActivityChecker,
  private val messageQueue: MessageQueue,
  private val messageHandler: MessageHandler,
  private val messageSerializer: MessageSerializer
) : IClientConnectionManager, PingTimeoutListener {

  private lateinit var connectionSettings: ConnectionSettingsEntity
  private lateinit var onConnectionChange: (Boolean) -> Unit

  private var executor = getExecutor()

  private fun getExecutor() = Executors.newSingleThreadExecutor { Thread(it, "socket-thread") }
  private val job = SupervisorJob()
  private val context = job + executor.asCoroutineDispatcher()
  private val scope = CoroutineScope(context)
  private var connection: SocketConnection? = null

  private var pendingConnection: Deferred<Unit>? = null

  init {
    messageQueue.setOnMessageAvailable { sendData(it) }
  }

  override fun setDefaultConnectionSettings(connectionSettings: ConnectionSettingsEntity) {
    this.connectionSettings = connectionSettings
  }

  override fun setOnConnectionChangeListener(onConnectionChange: (Boolean) -> Unit) {
    this.onConnectionChange = onConnectionChange
  }

  override fun start() {
    pendingConnection?.cancel()
    pendingConnection = scope.async {
      stop()
      delay(2000)
      realStart()
    }
  }

  private fun realStart() {
    if (executor.isShutdown) {
      executor = getExecutor()
    }

    if (connection?.isConnected() == true) {
      Timber.v("connection is already active")
      return
    }

    scope.launch {
      delay(2000)

      if (!::connectionSettings.isInitialized) {
        Timber.v("no connection settings aborting")
        return@launch
      }

      Timber.v("Attempting connection on $connectionSettings")
      connection = SocketConnection(
        connectionSettings,
        messageHandler,
        { connected ->
          onConnectionChange(connected)

          if (!connected) {
            // activityChecker.stop()
          } else {
            messageQueue.queue(SocketMessage.player())
          }
        }
      ) {
      }.apply {
        messageHandler.start()
        messageQueue.start()
        executor.execute(this)
        activityChecker.start()
      }
    }
  }

  override fun stop() {
    messageHandler.stop()
    messageQueue.stop()
    activityChecker.stop()
    connection?.cleanupSocket()
    executor.shutdownNow()
  }

  @Synchronized
  private fun sendData(message: SocketMessage) {
    connection?.sendMessage(messageSerializer.serialize(message))
  }

  override fun onTimeout() {
    Timber.v("Timeout received resetting socket")
    connection?.cleanupSocket()
    activityChecker.stop()
  }

  private class SocketConnection(
    connectionSettings: ConnectionSettingsEntity,
    private val messageHandler: MessageHandler,
    private val connected: (Boolean) -> Unit,
    private val error: (Throwable) -> Unit
  ) : Runnable {
    private val socketAddress: SocketAddress?
    private val mapper: InetAddressMapper = InetAddressMapper()

    private var socket: Socket? = null
    private var output: PrintWriter? = null

    init {
      socketAddress = mapper.map(connectionSettings)
    }

    /**
     * Returns true if the socket is not null and it is connected, false in any other case.

     * @return Boolean
     */
    fun isConnected(): Boolean {
      return socket?.isConnected ?: false
    }

    fun cleanupSocket() {
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

    fun sendMessage(messageString: String) {
      Timber.v("Sending (${isConnected()})")
      if (isConnected()) {
        writeToSocket(messageString)
      }
    }

    private fun writeToSocket(message: String) {
      val output = output ?: throw IOException("output was null")
      output.print(message)

      if (output.checkError()) {
        throw IOException("Output stream encountered an error")
      }
    }

    override fun run() {
      Timber.v("Socket connection is running")
      connected(false)

      if (null == socketAddress) {
        return
      }

      try {
        socket = with(connect()) {
          val out = OutputStreamWriter(outputStream, "UTF-8")
          output = PrintWriter(
            BufferedWriter(
              out,
              SOCKET_BUFFER
            ),
            true
          )

          val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
          val input = BufferedReader(
            inputStreamReader,
            SOCKET_BUFFER
          )

          connected(isConnected)

          while (isConnected) {
            try {
              val incoming = input.readLine() ?: throw IOException("no data")
              if (incoming.isNotEmpty()) {
                messageHandler.handleMessage(incoming)
              }
            } catch (e: IOException) {
              input.close()
              close()
              throw e
            }
          }
          this
        }
      } catch (e: Exception) {
        error(e)
      } finally {
        output?.close()
        socket = null
        connected(false)
        Timber.d("Socket connection terminated")
      }
    }

    private fun connect(): Socket {
      return Socket().apply {
        soTimeout = 20000
        connect(socketAddress)
      }.also {
        socket = it
      }
    }
  }

  companion object {
    private const val SOCKET_BUFFER = 2 * 4096
  }
}
