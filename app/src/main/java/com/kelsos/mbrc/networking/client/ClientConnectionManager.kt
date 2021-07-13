package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.networking.SocketActivityChecker
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionState
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.networking.connections.toSocketAddress
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import timber.log.Timber
import java.net.Socket
import java.net.SocketAddress
import java.util.concurrent.Executors.newSingleThreadExecutor

class ClientConnectionManager(
  private val activityChecker: SocketActivityChecker,
  private val messageQueue: MessageQueue,
  private val messageHandler: MessageHandler,
  private val moshi: Moshi,
  private val connectionRepository: ConnectionRepository,
  private val connectionState: ConnectionState,
  private val dispatchers: AppCoroutineDispatchers
) : IClientConnectionManager {
  private var executor = getExecutor()

  private fun getExecutor() = newSingleThreadExecutor { Thread(it, "SocketWorker") }
  private var job = SupervisorJob()
  private var scope = CoroutineScope(job + dispatchers.io)
  private var connection: Connection? = null

  override fun start() {
    stop()

    job = SupervisorJob()
    scope = CoroutineScope(job + dispatchers.io)

    scope.launch {
      delay(DELAY_MS)
      realStart()
    }
  }

  private suspend fun realStart() {
    if (executor.isShutdown) {
      executor = getExecutor()
    }

    if (connection?.isConnected == true) {
      Timber.v("connection is already active")
      return
    }

    delay(DELAY_MS)

    val default = connectionRepository.getDefault() ?: return
    Timber.v("Attempting connection on $default")
    val onConnection: (Boolean) -> Unit = { connected ->
      scope.launch {
        if (!connected) {
          activityChecker.stop()
          connectionState.connection.emit(ConnectionStatus.Off)
        } else {
          connectionState.connection.emit(ConnectionStatus.On)
          messageQueue.queue(SocketMessage.player())
        }
      }
    }

    val result = runCatching { Connection.connect(default.toSocketAddress()) }
    if (result.isFailure) {
      Timber.e(checkNotNull(result.exceptionOrNull()), "Connection failed")
      return
    }
    val connection = Connection(result.getOrThrow(), moshi, dispatchers)

    scope.launch {
      messageQueue.messages.collect { message ->
        withContext(dispatchers.network) {
          val sendResult = connection.send(message)
          if (sendResult.isFailure) {
            Timber.e(checkNotNull(sendResult.exceptionOrNull()), "Send failed")
          }
        }
      }
    }
    scope.launch {
      connection.messages.collect {
        messageHandler.process(it)
      }
    }

    executor.execute(ConnectionRunnable(connection, onConnection))

    activityChecker.start()
    activityChecker.setPingTimeoutListener {
      Timber.v("Timeout received resetting socket")
      connection.cleanup()
      activityChecker.stop()
    }
  }

  override fun stop() {
    job.cancel()
    activityChecker.stop()
    connection?.cleanup()
    executor.shutdownNow()
  }

  private class ConnectionRunnable(
    private val connection: Connection,
    private val connected: (Boolean) -> Unit
  ) : Runnable {

    override fun run() {
      Timber.v("Socket connection is running")
      connected(connection.isConnected)
      connection.listen()
      connected(connection.isConnected)
    }
  }

  companion object {
    private const val DELAY_MS = 2000L
  }
}

class Connection(
  private val socket: Socket,
  moshi: Moshi,
  dispatchers: AppCoroutineDispatchers
) {
  private val sink = socket.sink().buffer()
  private val source = socket.source().buffer()
  private val adapter = moshi.adapter(SocketMessage::class.java)
  private val job = SupervisorJob()
  private val scope = CoroutineScope(job + dispatchers.io)
  private val _messages = MutableSharedFlow<SocketMessage>()
  val messages: Flow<SocketMessage> get() = _messages

  val isConnected get() = socket.isConnected

  fun cleanup() {
    job.cancel()
    if (sink.isOpen) {
      sink.flush()
      sink.close()
    }
    if (source.isOpen) {
      source.close()
    }

    if (socket.isConnected) {
      socket.close()
    }
  }

  fun send(message: SocketMessage) = runCatching {
    if (!isConnected) {
      Timber.d("Socket was not connected: skipping $message")
      return@runCatching
    }
    val address = socket.remoteSocketAddress
    Timber.v("Sending to mbrc:/$address (connected: $isConnected)::$message")
    adapter.toJson(sink, message)
    sink.writeUtf8(NEWLINE)
    sink.flush()
  }

  private fun emitMessages(rawMessage: String) {
    val replies = rawMessage.split("\r\n".toRegex())
      .dropLastWhile(String::isEmpty)

    for (reply in replies) {
      val result = runCatching {
        val message = checkNotNull(adapter.fromJson(reply))
        scope.launch {
          _messages.emit(message)
        }
      }

      if (result.isFailure) {
        val throwable = result.exceptionOrNull()
        Timber.e(throwable!!, "Failed proceessing $reply")
      }
    }
  }

  fun listen() {
    while (isConnected) {
      val result = runCatching {
        val rawMessage = checkNotNull(source.readUtf8Line()) { "no data" }
        if (rawMessage.isNotEmpty()) {
          emitMessages(rawMessage)
        }
      }
      if (result.isFailure) {
        Timber.e(checkNotNull(result.exceptionOrNull()), "Listener terminated")
        cleanup()
        return
      }
    }
  }

  companion object {
    private const val SO_TIMEOUT = 30_000
    private const val NEWLINE = "\r\n"

    fun connect(address: SocketAddress): Socket {
      val socket = Socket()
      socket.soTimeout = SO_TIMEOUT
      socket.connect(address)
      return socket
    }
  }
}
