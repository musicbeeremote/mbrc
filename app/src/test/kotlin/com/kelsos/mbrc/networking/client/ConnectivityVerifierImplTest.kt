package com.kelsos.mbrc.networking.client

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.data.ConnectionSettings
import com.kelsos.mbrc.core.networking.ClientIdProvider
import com.kelsos.mbrc.core.networking.DefaultConnectionProvider
import com.kelsos.mbrc.core.networking.RequestManager
import com.kelsos.mbrc.core.networking.RequestManagerImpl
import com.kelsos.mbrc.core.networking.client.ConnectivityVerifier
import com.kelsos.mbrc.core.networking.client.ConnectivityVerifierImpl
import com.kelsos.mbrc.core.networking.client.SocketMessage
import com.kelsos.mbrc.core.networking.data.DeserializationAdapter
import com.kelsos.mbrc.core.networking.data.DeserializationAdapterImpl
import com.kelsos.mbrc.core.networking.data.SerializationAdapter
import com.kelsos.mbrc.core.networking.data.SerializationAdapterImpl
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.feature.settings.domain.ConnectionRepository
import com.kelsos.mbrc.utils.parserModule
import com.kelsos.mbrc.utils.testDispatcher
import com.kelsos.mbrc.utils.testDispatcherModule
import com.squareup.moshi.Moshi
import io.mockk.coEvery
import io.mockk.mockk
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import timber.log.Timber

private const val EXCEPTION_MESSAGE = "Test should throw"

@RunWith(AndroidJUnit4::class)
class ConnectivityVerifierImplTest : KoinTest {
  private var server: ServerSocket? = null
  private val connectionRepository: ConnectionRepository by inject()
  private val verifier: ConnectivityVerifier by inject()
  private val moshi: Moshi by inject()

  private val basePort: Int = 36000

  private lateinit var executor: ExecutorService

  private val testModule =
    module {
      includes(testDispatcherModule, parserModule)
      single { mockk<ConnectionRepository>() }
      singleOf(::RequestManagerImpl) { bind<RequestManager>() }
      singleOf(::ConnectivityVerifierImpl) { bind<ConnectivityVerifier>() }
      singleOf(::DeserializationAdapterImpl) { bind<DeserializationAdapter>() }
      singleOf(::SerializationAdapterImpl) { bind<SerializationAdapter>() }
      single<ClientIdProvider> { ClientIdProvider { "1234" } }
      single<DefaultConnectionProvider> {
        val repository = get<ConnectionRepository>()
        DefaultConnectionProvider { repository.getDefault() }
      }
    }

  @Before
  fun setUp() {
    startKoin {
      modules(listOf(testModule))
    }
    executor = Executors.newSingleThreadExecutor()
  }

  @After
  fun tearDown() {
    executor.shutdownNow()
    stopKoin()
  }

  private fun getServer(): ServerSocket {
    val maxPort = basePort + 1000
    for (port in basePort until maxPort) {
      val server = runCatching { ServerSocket(port) }

      if (server.isSuccess) {
        return server.getOrThrow()
      }
    }
    error("could not bind any port from $basePort to $maxPort")
  }

  private fun startMockServer(
    prematureDisconnect: Boolean = false,
    responseContext: Protocol = Protocol.VerifyConnection,
    json: Boolean = true
  ): ServerSocket {
    val executor: ExecutorService = Executors.newSingleThreadExecutor()
    val server = getServer()
    val adapter = moshi.adapter(SocketMessage::class.java)
    val mockSocket =
      Runnable {
        server.soTimeout = 3000

        while (true) {
          Timber.v("Listening on ${server.inetAddress.hostAddress}:${server.localPort}")
          val connection = server.accept()
          val input = InputStreamReader(connection!!.inputStream)
          val inputReader = BufferedReader(input)
          val line = inputReader.readLine()
          Timber.v("Received a message $line")

          val value = adapter.fromJson(line)

          if (Protocol.fromString(value?.context.orEmpty()) != Protocol.VerifyConnection) {
            connection.close()
            server.close()
            return@Runnable
          }

          if (prematureDisconnect) {
            connection.close()
            server.close()
            return@Runnable
          }

          val out = OutputStreamWriter(connection.outputStream, "UTF-8")
          val output = PrintWriter(BufferedWriter(out), true)
          val newLine = "\r\n"
          if (json) {
            val response = SocketMessage(context = responseContext.context)
            output.write(adapter.toJson(response) + newLine + newLine)
          } else {
            output.write(responseContext.context + newLine + newLine)
          }
          output.flush()
          input.close()
          inputReader.close()
          out.close()
          output.close()
          connection.close()
          server.close()
          return@Runnable
        }
      }

    executor.execute(mockSocket)
    return server
  }

  private fun ServerSocket.toConnection(): ConnectionSettings = ConnectionSettings(
    address = checkNotNull(this.inetAddress.hostAddress),
    port = this.localPort,
    name = "Test",
    isDefault = true,
    id = 1
  )

  @Test
  fun testSuccessfulVerification() {
    runTest(testDispatcher) {
      server = startMockServer()

      coEvery { connectionRepository.getDefault() } answers {
        checkNotNull(server).toConnection()
      }

      assertThat(verifier.verify()).isTrue()
    }
  }

  @Test
  fun testPrematureDisconnectDuringVerification() {
    runTest(testDispatcher) {
      server = startMockServer(true)
      coEvery { connectionRepository.getDefault() } answers {
        checkNotNull(server).toConnection()
      }

      try {
        verifier.verify()
        error(EXCEPTION_MESSAGE)
      } catch (e: Exception) {
        assertThat(e).isInstanceOf(RuntimeException::class.java)
      }
    }
  }

  @Test
  fun testInvalidPluginResponseVerification() {
    runTest(testDispatcher) {
      server = startMockServer(false, Protocol.ClientNotAllowed)
      coEvery { connectionRepository.getDefault() } answers {
        checkNotNull(server).toConnection()
      }

      // When plugin responds with wrong protocol, verify should return false
      val result = verifier.verify()
      assertThat(result).isFalse()
    }
  }

  @Test
  fun testVerificationNoConnection() {
    runTest(testDispatcher) {
      // No server needed - testing the case where no default connection exists
      coEvery { connectionRepository.getDefault() } answers {
        null
      }

      try {
        verifier.verify()
        error(EXCEPTION_MESSAGE)
      } catch (e: Exception) {
        assertThat(e).isInstanceOf(RuntimeException::class.java)
      }
    }
  }

  @Test
  fun testVerificationNoJsonPayload() {
    runTest(testDispatcher) {
      server = startMockServer(false, Protocol.fromString("a"), false)

      coEvery { connectionRepository.getDefault() } answers {
        checkNotNull(server).toConnection()
      }

      try {
        verifier.verify()
        error(EXCEPTION_MESSAGE)
      } catch (e: Exception) {
        assertThat(e).isInstanceOf(RuntimeException::class.java)
      }
    }
  }
}
