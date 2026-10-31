package com.kelsos.mbrc.core.networking.connectlink

import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.networking.ActiveConnection
import com.kelsos.mbrc.core.networking.client.SocketMessage
import com.kelsos.mbrc.core.networking.data.DeserializationAdapter
import com.kelsos.mbrc.core.networking.data.SerializationAdapter
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/** Answers whether a MusicBee Remote plugin is listening at one address. */
fun interface PluginProbe {
  suspend fun answers(address: String, port: Int): Boolean
}

/**
 * Probes with the V4 `verifyconnection` request, the same one [ConnectivityVerifier] uses: it needs
 * no handshake, so a probe never registers a client with the plugin. The reply is read through
 * [ActiveConnection], so a device that never ends its line cannot grow the buffer without bound.
 *
 * Any failure, not only an [java.io.IOException], counts as no answer: a link can point anywhere
 * on the LAN, and one odd address must not take the app down.
 *
 * Each attempt gives up after [TIMEOUT_MS] for connecting and again for the answer. Probes block in
 * socket calls, so they run on the IO pool rather than the small network pool the live connection
 * uses. Cancelling a probe closes its socket at once, since the plugin caps connections per IP.
 *
 * [ConnectivityVerifier]: com.kelsos.mbrc.core.networking.client.ConnectivityVerifier
 */
class SocketPluginProbe(
  private val serializationAdapter: SerializationAdapter,
  private val deserializationAdapter: DeserializationAdapter,
  private val dispatchers: AppCoroutineDispatchers
) : PluginProbe {
  private val request: ByteArray by lazy {
    (serializationAdapter.stringify(SocketMessage.create(Protocol.VerifyConnection)) + "\r\n")
      .toByteArray()
  }

  override suspend fun answers(address: String, port: Int): Boolean = withContext(dispatchers.io) {
    Socket().use { socket ->
      val closer = closeWhenCancelled(socket)
      try {
        verify(socket, InetSocketAddress(address, port))
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Timber.v(e, "No plugin answered at %s:%d", address, port)
        false
      } finally {
        closer.cancel()
      }
    }
  }

  /** Blocking socket calls ignore coroutine cancellation; closing the socket unblocks them. */
  private fun CoroutineScope.closeWhenCancelled(socket: Socket) = launch {
    try {
      awaitCancellation()
    } finally {
      socket.close()
    }
  }

  private fun verify(socket: Socket, address: InetSocketAddress): Boolean {
    socket.connect(address, TIMEOUT_MS)
    socket.soTimeout = TIMEOUT_MS
    val connection = ActiveConnection(socket, socket.getInputStream().bufferedReader())
    connection.send(request)
    val message = runCatching {
      deserializationAdapter.objectify(connection.readLine(), SocketMessage::class)
    }.getOrNull()
    return message?.context == Protocol.VERIFY_CONNECTION
  }

  companion object {
    const val TIMEOUT_MS = 3_000
  }
}

/**
 * Picks the address to save from a link's candidates. All of them are probed at once, and the
 * first one in link order that answers wins: the plugin orders the list by how likely each address
 * is to be the LAN one, so that order decides rather than whichever adapter replies first. The
 * other probes are cancelled as soon as the winner is known.
 */
class ReachableHostFinder(private val probe: PluginProbe) {
  suspend fun firstReachable(hosts: List<String>, port: Int): String? = coroutineScope {
    val attempts = hosts.map { host -> host to async { probe.answers(host, port) } }
    val winner = attempts.firstOrNull { (_, answered) -> answered.await() }?.first
    attempts.forEach { (_, attempt) -> attempt.cancel() }
    winner
  }
}
