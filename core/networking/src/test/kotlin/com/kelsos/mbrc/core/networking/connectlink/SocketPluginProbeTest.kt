package com.kelsos.mbrc.core.networking.connectlink

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.networking.data.DeserializationAdapterImpl
import com.kelsos.mbrc.core.networking.data.SerializationAdapterImpl
import com.squareup.moshi.Moshi
import java.net.InetAddress
import java.net.ServerSocket
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.Test

/** Runs against a real loopback [ServerSocket], so it uses real dispatchers rather than a test one. */
class SocketPluginProbeTest {

  private val ioDispatchers = object : AppCoroutineDispatchers {
    override val main: CoroutineDispatcher = Dispatchers.IO
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val database: CoroutineDispatcher = Dispatchers.IO
    override val network: CoroutineDispatcher = Dispatchers.IO
  }

  private val moshi = Moshi.Builder().build()
  private val probe = SocketPluginProbe(
    SerializationAdapterImpl(moshi),
    DeserializationAdapterImpl(moshi),
    ioDispatchers
  )
  private val server = ServerSocket(0, 1, InetAddress.getLoopbackAddress())
  private val loopback = InetAddress.getLoopbackAddress().hostAddress!!

  @After
  fun tearDown() {
    server.close()
  }

  private fun replyOnce(reply: String): CompletableFuture<String?> {
    val received = CompletableFuture<String?>()
    thread {
      server.accept().use { client ->
        received.complete(client.getInputStream().bufferedReader().readLine())
        client.getOutputStream().write("$reply\r\n".toByteArray())
      }
    }
    return received
  }

  @Test
  fun `a plugin that answers verifyconnection is found`() = runBlocking {
    val request = replyOnce("""{"context":"verifyconnection","data":""}""")

    assertThat(probe.answers(loopback, server.localPort)).isTrue()
    assertWithMessage("the probe must not start a handshake, so no client is registered")
      .that(request.get(1, TimeUnit.SECONDS))
      .contains("\"context\":\"verifyconnection\"")
  }

  @Test
  fun `a server answering something else is not a plugin`() = runBlocking {
    replyOnce("""{"context":"player","data":"MusicBee"}""")

    assertThat(probe.answers(loopback, server.localPort)).isFalse()
  }

  @Test
  fun `a server answering garbage is not a plugin`() = runBlocking {
    replyOnce("HTTP/1.1 400 Bad Request")

    assertThat(probe.answers(loopback, server.localPort)).isFalse()
  }

  @Test
  fun `a closed port is not a plugin`() = runBlocking {
    val port = server.localPort
    server.close()

    assertThat(probe.answers(loopback, port)).isFalse()
  }

  @Test
  fun `cancelling a probe closes its socket at once`() = runBlocking {
    val closedByClient = CompletableFuture<Boolean>()
    thread {
      server.accept().use { client ->
        val reader = client.getInputStream().bufferedReader()
        reader.readLine()
        closedByClient.complete(reader.read() == -1)
      }
    }

    val result = withTimeoutOrNull(CANCEL_AFTER_MS) { probe.answers(loopback, server.localPort) }

    assertThat(result).isNull()
    assertWithMessage("the socket should close on cancel, well before the read timeout")
      .that(closedByClient.get(SocketPluginProbe.TIMEOUT_MS / 2L, TimeUnit.MILLISECONDS))
      .isTrue()
  }

  private companion object {
    const val CANCEL_AFTER_MS = 300L
  }
}
