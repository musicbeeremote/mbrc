package com.kelsos.mbrc.core.networking

import com.kelsos.mbrc.core.common.state.ConnectionStatePublisher
import com.kelsos.mbrc.core.common.state.ConnectionStatus
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.common.utilities.coroutines.ScopeBase
import com.kelsos.mbrc.core.networking.client.PendingCommandBuffer
import com.kelsos.mbrc.core.networking.client.SocketMessage
import com.kelsos.mbrc.core.networking.client.UiMessage
import com.kelsos.mbrc.core.networking.client.UiMessageQueue
import com.kelsos.mbrc.core.networking.discovery.DiscoveryStop
import com.squareup.moshi.Moshi
import java.io.IOException
import java.net.Socket
import java.net.SocketAddress
import java.net.SocketException
import java.net.SocketTimeoutException
import kotlin.math.pow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okio.buffer
import okio.sink
import okio.source
import timber.log.Timber

interface ClientConnectionManager {
  fun start(cycleInfo: ConnectionCycleInfo? = null)

  fun stop()
}

/**
 * Raised when a message could not be written because the socket is no longer usable. Distinct from
 * a write that failed mid-flight: nothing reached the wire, so the command is safe to replay.
 */
class SocketNotConnectedException(message: SocketMessage) :
  IOException("Socket was not connected: $message")

sealed class NetworkError : Exception() {
  data class ConnectionTimeout(override val cause: Throwable?) : NetworkError()

  data class ConnectionRefused(override val cause: Throwable?) : NetworkError()

  data class SocketError(override val cause: Throwable) : NetworkError()

  data class UnknownError(override val cause: Throwable) : NetworkError()
}

data class ConnectionConfig(
  val maxRetries: Int = 3,
  val initialDelay: Long = 1000L,
  val maxDelay: Long = 30000L,
  val backoffMultiplier: Double = 2.0,
  // Slightly less than ping timeout
  val readTimeoutMs: Long = 35000L,
  // Healthcheck every 20 seconds
  val healthCheckIntervalMs: Long = 20000L,
  // Safety ceiling for a single inbound line. Heap-relative; measured worst case on a 15k-track
  // library is ~244 KB per 800-item page, so this leaves ~100x headroom while preventing a
  // malformed/un-terminated frame from growing the read buffer until the heap is exhausted.
  val maxMessageBytes: Long = defaultMaxMessageBytes()
)

class ClientConnectionManagerImpl(
  private val activityChecker: SocketActivityChecker,
  private val messageHandler: MessageHandler,
  private val moshi: Moshi,
  private val connectionProvider: ConnectionProvider,
  private val connectionState: ConnectionStatePublisher,
  private val dispatchers: AppCoroutineDispatchers,
  private val uiMessageQueue: UiMessageQueue,
  private val pendingCommands: PendingCommandBuffer
) : ScopeBase(dispatchers.io),
  ClientConnectionManager {
  // Written when a connection is set up or torn down, read by the outgoing pump, which runs on a
  // different thread of the io pool.
  @Volatile
  private var connection: Connection? = null

  @Volatile
  private var connectionScope: CoroutineScope? = null
  private val connectionConfig = ConnectionConfig()
  private var currentCycleInfo: ConnectionCycleInfo? = null

  // Outlives individual connections, and stop(): see startOutgoingPump.
  private val pumpScope = CoroutineScope(SupervisorJob() + dispatchers.io)

  init {
    startOutgoingPump()
  }

  @Volatile
  private var isStopping = false

  // Track pending socket for cancellation during connect
  @Volatile
  private var pendingSocket: Socket? = null

  override fun start(cycleInfo: ConnectionCycleInfo?) {
    // Don't restart if already connected
    val currentStatus = connectionState.connection.value
    if (currentStatus == ConnectionStatus.Connected) {
      Timber.v("Already connected, ignoring start request")
      return
    }

    stop()
    isStopping = false
    onStart() // Reset coroutine scope after stop
    currentCycleInfo = cycleInfo
    launch {
      delay(DELAY_MS)
      if (isStopping) return@launch
      // Double-check in case connection succeeded during delay
      val statusAfterDelay = connectionState.connection.firstOrNull()
      if (statusAfterDelay == ConnectionStatus.Connected) {
        return@launch
      }
      // Emit connecting state only if not already connected
      connectionState.updateConnection(
        ConnectionStatus.Connecting(
          cycle = cycleInfo?.cycle,
          maxCycles = cycleInfo?.maxCycles ?: DEFAULT_MAX_CYCLES
        )
      )
      attemptConnection()
    }
  }

  private suspend fun attemptConnection() {
    val connectionSettings = getConnectionSettings()
    if (connectionSettings == null) {
      Timber.v("No connection settings available, going offline")
      connectionState.updateConnection(ConnectionStatus.Offline)
      return
    }
    Timber.v("Attempting connection on $connectionSettings")

    attemptConnectionWithRetry(connectionSettings.toSocketAddress())
  }

  private suspend fun attemptConnectionWithRetry(address: SocketAddress) {
    repeat(connectionConfig.maxRetries) { attempt ->
      // Check if stop was requested
      if (isStopping) {
        Timber.v("Connection attempt cancelled - stop requested")
        return
      }

      if (attempt > 0) {
        val delayMs =
          minOf(
            connectionConfig.initialDelay *
              connectionConfig.backoffMultiplier.pow(attempt - 1).toLong(),
            connectionConfig.maxDelay
          )
        Timber.v(
          "Retrying connection in ${delayMs}ms (attempt ${attempt + 1}/${connectionConfig.maxRetries})"
        )
        delay(delayMs)

        // Check again after delay
        if (isStopping) {
          Timber.v("Connection attempt cancelled after delay - stop requested")
          return
        }
      }

      val result = runCatching { connectWithTracking(address) }
      result.fold(
        onSuccess = { socket ->
          if (isStopping) {
            // Stop was requested during connect, close the socket
            Timber.v("Connection cancelled during connect - closing socket")
            runCatching { socket.close() }
            return
          }
          Timber.v("Connection successful on attempt ${attempt + 1}")
          setupConnection(socket)
          return
        },
        onFailure = { exception ->
          if (isStopping) {
            Timber.v("Connection attempt cancelled - stop requested")
            return
          }

          val networkError = classifyNetworkError(exception)
          Timber.w("Connection attempt ${attempt + 1} failed: ${networkError::class.simpleName}")

          if (attempt == connectionConfig.maxRetries - 1) {
            Timber.e(exception, "All connection attempts failed")
            handleConnectionFailure(networkError)
            uiMessageQueue.messages.emit(UiMessage.ConnectionError.AllRetriesExhausted)
          }
        }
      )
    }
  }

  private fun connectWithTracking(address: SocketAddress): Socket {
    val socket = Socket()
    pendingSocket = socket
    try {
      socket.soTimeout = Connection.SO_TIMEOUT
      socket.tcpNoDelay = true
      socket.keepAlive = true
      socket.connect(address, Connection.CONNECT_TIMEOUT)
      return socket
    } finally {
      pendingSocket = null
    }
  }

  private fun classifyNetworkError(exception: Throwable): NetworkError = when (exception) {
    is SocketTimeoutException -> NetworkError.ConnectionTimeout(exception)

    is SocketException ->
      if (exception.message?.contains("refused") == true) {
        NetworkError.ConnectionRefused(exception)
      } else {
        NetworkError.SocketError(exception)
      }

    is IOException -> NetworkError.SocketError(exception)

    else -> NetworkError.UnknownError(exception)
  }

  private suspend fun handleConnectionFailure(networkError: NetworkError) {
    // Only set Offline if we're not in a reconnection loop
    // When cycleInfo is present, ServiceLifecycleManager is managing reconnection
    // and will update the state with the next cycle
    if (currentCycleInfo == null) {
      connectionState.updateConnection(ConnectionStatus.Offline)
    }

    val uiMessage =
      when (networkError) {
        is NetworkError.ConnectionTimeout -> UiMessage.ConnectionError.ConnectionTimeout

        is NetworkError.ConnectionRefused -> UiMessage.ConnectionError.ConnectionRefused

        is NetworkError.SocketError -> {
          val message = networkError.cause.message
          when {
            message?.contains("Network is unreachable", ignoreCase = true) == true ->
              UiMessage.ConnectionError.NetworkUnavailable

            message?.contains("No route to host", ignoreCase = true) == true ->
              UiMessage.ConnectionError.ServerNotFound

            else ->
              UiMessage.ConnectionError.UnknownConnectionError(
                message ?: "Socket connection failed"
              )
          }
        }

        is NetworkError.UnknownError ->
          UiMessage.ConnectionError.UnknownConnectionError(
            networkError.cause.message ?: "Unknown connection error"
          )
      }

    uiMessageQueue.messages.emit(uiMessage)
  }

  private suspend fun getConnectionSettings() =
    connectionProvider.getDefault() ?: discoverConnection()

  private suspend fun discoverConnection() = connectionProvider.discover().let { discoveryStop ->
    when (discoveryStop) {
      is DiscoveryStop.Complete -> {
        val host = discoveryStop.first
        Timber.v("Discovery detected ${host.address} will attempt to connect to it")
        host
      }

      else -> {
        Timber.v("Discovery did not complete, will not connect to any servers")
        null
      }
    }
  }

  private fun setupConnection(socket: Socket) {
    // Every coroutine below belongs to this socket. Without tearing the previous ones down first,
    // a reconnect that skips stop() (the ping timeout path) leaves the old outgoing collector
    // subscribed to the shared message queue, so each command is also handed to a dead connection.
    teardownConnection()

    val scope = CoroutineScope(coroutineContext + SupervisorJob(coroutineContext[Job]))
    connectionScope = scope

    val connection =
      Connection(socket, moshi, dispatchers, connectionConfig).also {
        this.connection = it
      }

    setupMessageFlows(scope, connection)
    startConnectionWorker(scope, connection)
    setupActivityChecker(connection)
    startHealthChecker(scope, connection)
  }

  private fun teardownConnection() {
    connectionScope?.let { scope ->
      Timber.v("Cancelling coroutines of the previous connection")
      scope.cancel()
    }
    connectionScope = null
    connection?.cleanup()
    connection = null
  }

  private fun setupMessageFlows(scope: CoroutineScope, connection: Connection) {
    scope.launch {
      connection.messages.collect { message ->
        messageHandler.processIncoming(message)
      }
    }
  }

  /**
   * Drains the outgoing queue for as long as the manager exists, rather than for the lifetime of a
   * single connection.
   *
   * The queue is a shared flow without replay, so a command emitted while nothing is collecting is
   * gone: a tap during a reconnect would never reach a socket and would never be buffered either.
   * Keeping a single collector alive across connections means every command is either written or
   * handed to [pendingCommands].
   */
  private fun startOutgoingPump() {
    pumpScope.launch {
      messageHandler.processOutgoing { message ->
        dispatchOutgoing(message)
      }
    }
  }

  private fun dispatchOutgoing(message: SocketMessage) {
    val current = connection
    if (current == null) {
      bufferForReplay(message)
      return
    }

    current.send(message).onFailure { exception ->
      Timber.e(exception, "Send failed")
      // Only a message that never reached the wire is safe to replay. A write that failed
      // mid-flight may have put part of the frame on the socket already, and replaying it would
      // hand the player a second copy of a command that is not idempotent.
      if (exception is SocketNotConnectedException) {
        bufferForReplay(message)
      }
      // A failed write means the socket is broken. Closing it lets the connection worker publish
      // Offline, which is what drives reconnection. Calling stop() here instead would also cancel
      // a reconnect that is already in flight.
      current.cleanup()
    }
  }

  /**
   * Holds [message] until a connection comes back, unless the manager is stopped: after an explicit
   * disconnect there is no session to replay into, and a command buffered then would fire against
   * whichever session the user opens next.
   */
  private fun bufferForReplay(message: SocketMessage) {
    if (isStopping) {
      Timber.d("Manager is stopped, discarding $message instead of buffering it")
      return
    }
    if (pendingCommands.stash(message)) {
      Timber.d("No connection available, buffering $message for replay")
    }
  }

  private fun startConnectionWorker(scope: CoroutineScope, connection: Connection) {
    scope.launch(dispatchers.io) {
      Timber.v("Socket connection is running")
      handleConnectionStatus(scope, connection.isConnected)

      try {
        connection.listen()
      } catch (e: IOException) {
        Timber.e(e, "Connection worker failed due to IO error")
      } finally {
        handleConnectionStatus(scope, connection.isConnected)
      }
    }
  }

  private fun setupActivityChecker(connection: Connection) {
    activityChecker.start()
    activityChecker.setPingTimeoutListener {
      Timber.v("Ping timeout received - resetting socket")
      connection.cleanup()
      activityChecker.stop()
      launch {
        connectionState.updateConnection(ConnectionStatus.Offline)
        delay(RECONNECT_DELAY_MS) // Brief delay before attempting reconnection
        attemptConnection()
      }
    }
  }

  private fun startHealthChecker(scope: CoroutineScope, connection: Connection) {
    scope.launch {
      while (connection.isConnected) {
        delay(connectionConfig.healthCheckIntervalMs)

        // Perform health check
        if (!connection.isConnected) {
          Timber.v("Health check failed - connection no longer healthy")
          connection.cleanup()
          break
        }

        // Additional check: try to get socket info to verify connection
        val healthCheck = runCatching {
          val (remoteAddress, isInputShutdown, isOutputShutdown) = connection.getSocketInfo()
          remoteAddress != null && !isInputShutdown && !isOutputShutdown
        }

        if (healthCheck.isFailure || healthCheck.getOrNull() == false) {
          Timber.v("Socket health check failed")
          connection.cleanup()
          break
        }
      }
    }
  }

  private fun handleConnectionStatus(scope: CoroutineScope, connected: Boolean) {
    scope.launch {
      if (!connected) {
        activityChecker.stop()
        connectionState.updateConnection(ConnectionStatus.Offline)
      } else {
        connectionState.updateConnection(ConnectionStatus.Authenticating)
        messageHandler.startHandshake()
      }
    }
  }

  override fun stop() {
    Timber.v("Stopping connection manager")
    isStopping = true
    currentCycleInfo = null

    // Cancel all coroutines (including connection attempts)
    onStop()

    // Close any pending socket that's in the middle of connecting
    pendingSocket?.let { socket ->
      Timber.v("Closing pending socket during stop")
      runCatching { socket.close() }
      pendingSocket = null
    }

    teardownConnection()
    activityChecker.stop()

    // Set state to Offline immediately
    connectionState.updateConnection(ConnectionStatus.Offline)
  }

  companion object {
    private const val DELAY_MS = 2000L
    private const val RECONNECT_DELAY_MS = 1000L
    private const val DEFAULT_MAX_CYCLES = 3
  }
}

class Connection(
  private val socket: Socket,
  moshi: Moshi,
  dispatchers: AppCoroutineDispatchers,
  private val config: ConnectionConfig = ConnectionConfig()
) {
  private val sink = socket.sink().buffer()
  private val source = socket.source().buffer()
  private val adapter = moshi.adapter(SocketMessage::class.java)
  private val job = SupervisorJob()
  private val scope = CoroutineScope(job + dispatchers.io)
  private val _messages = MutableSharedFlow<SocketMessage>(
    extraBufferCapacity = MESSAGE_BUFFER_CAPACITY,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
  )
  val messages: Flow<SocketMessage> get() = _messages

  @Volatile
  private var isCleanedUp = false

  val isConnected get() = !isCleanedUp &&
    socket.isConnected &&
    !socket.isClosed &&
    isSocketHealthy()

  fun isSocketHealthy(): Boolean = runCatching {
    // More robust socket health check
    !socket.isInputShutdown && !socket.isOutputShutdown && socket.remoteSocketAddress != null
  }.getOrElse { false }

  // Expose socket for health checks
  internal fun getSocketInfo() = Triple(
    socket.remoteSocketAddress,
    socket.isInputShutdown,
    socket.isOutputShutdown
  )

  fun cleanup() {
    if (isCleanedUp) return
    isCleanedUp = true

    job.cancel()

    runCatching {
      if (sink.isOpen) {
        sink.flush()
        sink.close()
      }
    }.onFailure { Timber.w(it, "Failed to close sink") }

    runCatching {
      if (source.isOpen) {
        source.close()
      }
    }.onFailure { Timber.w(it, "Failed to close source") }

    runCatching {
      if (!socket.isClosed) {
        socket.close()
      }
    }.onFailure { Timber.w(it, "Failed to close socket") }
  }

  /**
   * Writes [message] to the socket. Failure to write is reported as [Result.failure] so the caller
   * can tear the connection down and replay the command: an unwritten message must never look like
   * a delivered one.
   */
  fun send(message: SocketMessage): Result<Unit> = runCatching {
    if (!isConnected) {
      throw SocketNotConnectedException(message)
    }
    val address = socket.remoteSocketAddress
    Timber.v("Sending to mbrc:/$address (connected: $isConnected)::$message")
    adapter.toJson(sink, message)
    sink.writeUtf8(NEWLINE)
    sink.flush()
  }

  private fun emitMessages(rawMessage: String) {
    val replies =
      rawMessage
        .split("\r\n".toRegex())
        .dropLastWhile(String::isEmpty)

    for (reply in replies) {
      val result =
        runCatching {
          if (reply.isBlank()) {
            Timber.v("Skipping blank message")
            return@runCatching
          }

          val message = adapter.fromJson(reply)
          if (message == null) {
            Timber.w("Received null message from: $reply")
            return@runCatching
          }

          _messages.tryEmit(message)
        }

      if (result.isFailure) {
        val throwable = result.exceptionOrNull()
        Timber.e(throwable, "Failed processing message: $reply")
        // If we consistently fail to parse messages, the connection might be corrupted
        messageParseFailureCount++
        if (messageParseFailureCount >= MAX_PARSE_FAILURES) {
          Timber.w(
            "Too many message parse failures ($messageParseFailureCount), treating as connection failure"
          )
          throw IOException("Message parsing consistently failing - connection corrupted")
        }
      } else {
        // Reset failure count on successful parse
        messageParseFailureCount = 0
      }
    }
  }

  @Volatile
  private var messageParseFailureCount = 0

  fun listen() {
    try {
      while (isConnected) {
        val rawMessage = readWithTimeout()
        if (rawMessage == null) {
          Timber.d("Connection closed by remote")
          break
        }

        if (rawMessage.isNotEmpty()) {
          emitMessages(rawMessage)
        }
      }
    } catch (e: IOException) {
      if (!isCleanedUp) {
        Timber.e(e, "Listener terminated due to IO error")
      }
    } finally {
      cleanup()
    }
  }

  private fun readWithTimeout(): String? {
    // Set read timeout to detect unresponsive connections
    val originalTimeout = socket.soTimeout
    return try {
      socket.soTimeout = config.readTimeoutMs.toInt()
      readBoundedLine()
    } catch (e: SocketTimeoutException) {
      Timber.w("Read timeout after ${config.readTimeoutMs}ms - connection may be unresponsive")
      throw IOException("Connection read timeout", e)
    } finally {
      socket.soTimeout = originalTimeout
    }
  }

  /**
   * Reads a single `\n`-terminated line, refusing to buffer more than [ConnectionConfig.maxMessageBytes]
   * bytes. A peer that never sends a terminator would otherwise grow the okio buffer until the heap
   * is exhausted (the OOM we are guarding against). Returns null on a clean remote close.
   */
  private fun readBoundedLine(): String? {
    val limit = config.maxMessageBytes
    val newlineIndex = source.indexOf(LINE_FEED, 0, limit)
    if (newlineIndex != -1L) {
      val line = source.readUtf8(newlineIndex)
      source.skip(1) // consume the '\n'
      return line.removeSuffix("\r")
    }
    // No terminator within the cap: either the stream ended, or the peer is sending an
    // oversized/garbage frame that would grow the buffer without bound.
    val buffered = source.buffer.size
    if (buffered >= limit) {
      throw IOException(
        "Inbound message exceeded $limit bytes without a line terminator; connection corrupted"
      )
    }
    return if (buffered == 0L) null else source.readUtf8().removeSuffix("\r")
  }

  companion object {
    internal const val SO_TIMEOUT = 30_000
    internal const val CONNECT_TIMEOUT = 15_000
    private const val NEWLINE = "\r\n"
    private const val LINE_FEED = '\n'.code.toByte()
    private const val MAX_PARSE_FAILURES = 5
    private const val MESSAGE_BUFFER_CAPACITY = 128

    fun connect(address: SocketAddress): Socket {
      val socket = Socket()
      socket.soTimeout = SO_TIMEOUT
      socket.tcpNoDelay = true // Reduce latency for ping/pong
      socket.keepAlive = true // Enable TCP keep-alive
      socket.connect(address, CONNECT_TIMEOUT)
      return socket
    }
  }
}
