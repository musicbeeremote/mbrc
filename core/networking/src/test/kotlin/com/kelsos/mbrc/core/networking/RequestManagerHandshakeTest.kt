package com.kelsos.mbrc.core.networking

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.kelsos.mbrc.core.common.data.ConnectionSettings
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.networking.data.DeserializationAdapterImpl
import com.kelsos.mbrc.core.networking.data.SerializationAdapterImpl
import com.squareup.moshi.Moshi
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test

/** Runs against a real loopback [ServerSocket], so it uses real dispatchers rather than a test one. */
class RequestManagerHandshakeTest {

  private val ioDispatchers = object : AppCoroutineDispatchers {
    override val main: CoroutineDispatcher = Dispatchers.IO
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val database: CoroutineDispatcher = Dispatchers.IO
    override val network: CoroutineDispatcher = Dispatchers.IO
  }

  private val server = ServerSocket(0, 1, InetAddress.getLoopbackAddress())
  private val moshi = Moshi.Builder().build()
  private val clientIdProvider = mockk<ClientIdProvider>().also {
    coEvery { it.getClientId() } returns "client"
  }
  private val manager = RequestManagerImpl(
    serializationAdapter = SerializationAdapterImpl(moshi),
    deserializationAdapter = DeserializationAdapterImpl(moshi),
    clientIdProvider = clientIdProvider,
    connectionProvider = {
      ConnectionSettings(
        address = InetAddress.getLoopbackAddress().hostAddress!!,
        port = server.localPort,
        name = "test",
        isDefault = true,
        id = 1
      )
    },
    dispatchers = ioDispatchers
  )

  @After
  fun tearDown() {
    server.close()
  }

  /** Answers the first line with [reply], then reports whether the client closed its socket. */
  private fun replyOnce(reply: String): CompletableFuture<Boolean> {
    val clientClosed = CompletableFuture<Boolean>()
    thread {
      server.accept().use { client ->
        val reader = client.getInputStream().bufferedReader()
        reader.readLine()
        client.getOutputStream().write("$reply\r\n".toByteArray())
        clientClosed.complete(reader.read() == -1)
      }
    }
    return clientClosed
  }

  @Test
  fun `a service answering a bare number fails the connection instead of crashing`() = runBlocking {
    val clientClosed = replyOnce("42")

    val thrown = runCatching { manager.openConnection() }.exceptionOrNull()

    assertWithMessage("callers only handle IOException, so the failure must be one")
      .that(thrown)
      .isInstanceOf(IOException::class.java)
    assertThat(thrown).isInstanceOf(UnexpectedHandshakeReplyException::class.java)
    assertWithMessage("the socket to the wrong service must be closed")
      .that(clientClosed.get(5, TimeUnit.SECONDS))
      .isTrue()
  }

  @Test
  fun `a service answering text that is not json fails the connection with an IOException`() =
    runBlocking {
      val clientClosed = replyOnce("HTTP/1.1 400 Bad Request")

      val thrown = runCatching { manager.openConnection() }.exceptionOrNull()

      assertThat(thrown).isInstanceOf(IOException::class.java)
      assertWithMessage("the socket to the wrong service must be closed")
        .that(clientClosed.get(5, TimeUnit.SECONDS))
        .isTrue()
    }
}
