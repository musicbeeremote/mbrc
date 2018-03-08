package com.kelsos.mbrc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.networking.ConnectivityVerifier
import com.kelsos.mbrc.networking.ConnectivityVerifierImpl
import com.kelsos.mbrc.networking.SocketMessage
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.networking.protocol.Protocol
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.given
import org.mockito.MockitoAnnotations
import toothpick.config.Module
import toothpick.testing.ToothPickRule
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ConnectivityVerifierImplTest {

  @Mock lateinit var connectionRepository: ConnectionRepository
  @Rule @JvmField val toothpickRule: ToothPickRule = ToothPickRule(this, "verifier")
      .setRootRegistryPackage("com.kelsos.mbrc")
  private val mapper = ObjectMapper()
  private val port: Int = 46000

  lateinit var verifier: ConnectivityVerifier

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)
    toothpickRule.scope.installModules(TestModule())
    verifier = toothpickRule.getInstance(ConnectivityVerifier::class.java)
  }

  fun startMockServer(
    prematureDisconnect: Boolean = false,
    responseContext: String = Protocol.VerifyConnection,
    json: Boolean = true
  ): ServerSocket {
    val random = Random()
    val executor: ExecutorService = Executors.newSingleThreadExecutor()
    val server = ServerSocket(port + random.nextInt(1000))
    val mockSocket = Runnable {
      server.soTimeout = 3000

      while (true) {
        val connection = server.accept()
        val input = InputStreamReader(connection!!.inputStream)
        val inputReader = BufferedReader(input)
        val line = inputReader.readLine()
        val value = mapper.readValue(line, SocketMessage::class.java)

        if (value.context != Protocol.VerifyConnection) {
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
          val response = SocketMessage()
          response.context = responseContext
          output.write(mapper.writeValueAsString(response) + newLine + newLine)
        } else {
          output.write(responseContext + newLine + newLine)
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

  @After
  fun tearDown() {
  }

  @Test fun testSuccessfulVerification() {
    val server = startMockServer()

    given(connectionRepository.default).thenAnswer {
      val settings = ConnectionSettingsEntity()
      settings.address = server.inetAddress.hostAddress
      settings.port = server.localPort
      return@thenAnswer settings
    }

    val subscriber = verifier.verify().test()
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertComplete()
    subscriber.assertNoErrors()
    subscriber.assertValueCount(1)
    subscriber.assertValue(true)
  }

  @Test fun testPrematureDisconnectDuringVerification() {
    val server = startMockServer(true)
    given(connectionRepository.default).thenAnswer {
      val settings = ConnectionSettingsEntity()
      settings.address = server.inetAddress.hostAddress
      settings.port = server.localPort
      return@thenAnswer settings
    }

    val subscriber = verifier.verify().test()
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(RuntimeException::class.java)
  }

  @Test fun testInvalidPluginResponseVerification() {
    val server = startMockServer(false, Protocol.ClientNotAllowed)
    given(connectionRepository.default).thenAnswer {
      val settings = ConnectionSettingsEntity()
      settings.address = server.inetAddress.hostAddress
      settings.port = server.localPort
      return@thenAnswer settings
    }

    val subscriber = verifier.verify().test()
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(ConnectivityVerifierImpl.NoValidPluginConnection::class.java)
  }

  @Test fun testVerificationNoConnection() {
    startMockServer(true)

    given(connectionRepository.default).thenAnswer {
      return@thenAnswer null
    }

    val subscriber = verifier.verify().test()
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(RuntimeException::class.java)
  }

  @Test fun testVerificationNoJsonPayload() {
    startMockServer(false, "payload", false)

    given(connectionRepository.default).thenAnswer {
      return@thenAnswer null
    }

    val subscriber = verifier.verify().test()
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(RuntimeException::class.java)
  }

  inner class TestModule : Module() {
    init {
      bind(ObjectMapper::class.java).toInstance(mapper)
      bind(ConnectionRepository::class.java).toInstance(connectionRepository)
      bind(ConnectivityVerifier::class.java).to(ConnectivityVerifierImpl::class.java)
    }
  }
}