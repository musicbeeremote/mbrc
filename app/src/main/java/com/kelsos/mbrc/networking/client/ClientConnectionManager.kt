package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.networking.SocketActivityChecker
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettings
import com.kelsos.mbrc.networking.connections.ConnectionState
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.networking.connections.toSocketAddress
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.BufferedWriter
import java.io.IOException
import java.net.Socket
import java.net.SocketAddress
import java.util.concurrent.Executors

class ClientConnectionManager(
  private val activityChecker: SocketActivityChecker,
  private val messageQueue: MessageQueue,
  private val messageHandler: MessageHandler,
  private val moshi: Moshi,
  private val connectionRepository: ConnectionRepository,
  private val connectionState: ConnectionState,
  dispatchers: AppCoroutineDispatchers
) : IClientConnectionManager {

  private val adapter by lazy { moshi.adapter(SocketMessage::class.java) }
  private var executor = getExecutor()

  private fun getExecutor() = Executors.newSingleThreadExecutor { Thread(it, "socket-thread") }
  private val job = SupervisorJob()
  private val scope = CoroutineScope(job + dispatchers.io)
  private var connection: SocketConnection? = null

  private var connect: Job? = null
  private var messageJob: Job? = null

  override fun start() {
    connect?.cancel()
    connect = scope.launch {
      stop()
      delay(DELAY_MS)
      realStart()
    }
  }

  private suspend fun realStart() {
    if (executor.isShutdown) {
      executor = getExecutor()
    }

    if (connection?.isConnected() == true) {
      Timber.v("connection is already active")
      return
    }

    delay(DELAY_MS)

    val default = connectionRepository.getDefault() ?: return
    Timber.v("Attempting connection on $default")
    val onConnection: suspend (Boolean) -> Unit = { connected ->
      if (!connected) {
        activityChecker.stop()
        connectionState.connection.emit(ConnectionStatus.Off)
      } else {
        connectionState.connection.emit(ConnectionStatus.On)
        messageQueue.queue(SocketMessage.player())
      }
    }

    messageJob?.cancel()
    messageJob = scope.launch {
      messageQueue.messages.collect { message ->
        send(message)
      }
    }

    connection = SocketConnection(
      default,
      messageHandler,
      scope,
      onConnection
    )

    messageHandler.start()
    executor.execute(connection)
    activityChecker.start()
    activityChecker.setPingTimeoutListener {
      Timber.v("Timeout received resetting socket")
      connection?.cleanupSocket()
      activityChecker.stop()
    }
  }

  override fun stop() {
    messageHandler.stop()
    activityChecker.stop()
    connection?.cleanupSocket()
    messageJob?.cancel()
    executor.shutdownNow()
  }

  @Synchronized
  private fun send(message: SocketMessage) {
    Timber.v("Preparing to sending ${message.context}:${message.data}")
    connection?.sendMessage("${adapter.toJson(message)}\r\n")
  }

  private class SocketConnection(
    connectionSettings: ConnectionSettings,
    private val messageHandler: MessageHandler,
    private val scope: CoroutineScope,
    private val connected: suspend (Boolean) -> Unit
  ) : Runnable {
    private val socketAddress: SocketAddress = connectionSettings.toSocketAddress()

    private var socket: Socket? = null
    private var output: BufferedWriter? = null

    /**
     * Returns true if the socket is not null and it is connected, false in any other case.

     * @return Boolean
     */
    fun isConnected(): Boolean {
      return socket?.isConnected ?: false
    }

    fun cleanupSocket() {
      Timber.v("Cleaning up socket connection")
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
      Timber.v("Sending to mbrc:/$socketAddress (connected: ${isConnected()})")
      if (isConnected()) {
        writeToSocket(messageString)
      }
    }

    private fun writeToSocket(message: String) {
      val output = output ?: throw IOException("output was null")
      output.write(message)
      output.flush()
    }

    override fun run() {
      Timber.v("Socket connection is running")
      scope.launch { connected(false) }

      try {
        val socket = connect().also {
          this.socket = it
        }

        val charset = charset("UTF-8")
        output = socket.getOutputStream().bufferedWriter(charset)
        val input = socket.getInputStream().bufferedReader(charset)

        scope.launch { connected(socket.isConnected) }

        while (socket.isConnected) {
          try {
            val rawMessage = input.readLine() ?: throw IOException("no data")
            if (rawMessage.isNotEmpty()) {
              scope.launch { messageHandler.handleMessage(rawMessage) }
            }
          } catch (e: IOException) {
            input.close()
            throw e
          }
        }
      } catch (e: Exception) {
        error(e)
      } finally {
        scope.launch { connected(false) }
        output?.close()
        socket = null
        Timber.d("Socket connection terminated")
      }
    }

    private fun connect(): Socket {
      val socket = Socket()
      socket.soTimeout = SO_TIMEOUT
      socket.connect(socketAddress)
      return socket
    }
  }

  companion object {
    private const val DELAY_MS = 2000L
    private const val SO_TIMEOUT = 20_000
  }
}
