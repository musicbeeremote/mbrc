package com.kelsos.mbrc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.ConnectionSettings
import com.kelsos.mbrc.data.SocketMessage
import com.kelsos.mbrc.repository.ConnectionRepository
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
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

class ConnectionVerifierImplTest {
  private lateinit var server: ServerSocket

  @Mock lateinit var connectionRepository: ConnectionRepository
  @Rule @JvmField val toothpickRule: ToothPickRule = ToothPickRule(this, "verifier")
      .setRootRegistryPackage("com.kelsos.mbrc")
  private val mapper = ObjectMapper()
  private val port: Int = 36000

  lateinit var verifier: ConnectionVerifier

  private lateinit var executor: ExecutorService

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)
    toothpickRule.scope.installModules(TestModule())
    verifier = toothpickRule.getInstance(ConnectionVerifier::class.java)
    executor = Executors.newSingleThreadExecutor()
  }

  fun startMockServer(
      prematureDisconnect: Boolean = false,
      responseContext: String = Protocol.VerifyConnection,
      json: Boolean = true
  ) {
    val random = Random()
    val mockSocket = Runnable {

      server = ServerSocket(port + random.nextInt(1000))
      server.soTimeout = 5000

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

        val out = OutputStreamWriter(connection.outputStream, Const.UTF_8)
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
  }

  @After
  fun tearDown() {
    executor.shutdownNow()
  }

  @Test fun testSuccessfulVerification() {
    startMockServer()

    `when`(connectionRepository.default).thenAnswer {
      val settings = ConnectionSettings()
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
    startMockServer(true)
    `when`(connectionRepository.default).thenAnswer {
      val settings = ConnectionSettings()
      settings.address = server.inetAddress.hostAddress
      settings.port = server.localPort
      return@thenAnswer settings
    }

    val subscriber = verifier.verify().test()
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(RuntimeException::class.java)
  }

  @Test fun testInvalidPluginResponseVerification() {
    startMockServer(false, Protocol.ClientNotAllowed)
    `when`(connectionRepository.default).thenAnswer {
      val settings = ConnectionSettings()
      settings.address = server.inetAddress.hostAddress
      settings.port = server.localPort
      return@thenAnswer settings
    }

    val subscriber = verifier.verify().test()
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(ConnectionVerifierImpl.NoValidPluginConnection::class.java)
  }

  @Test fun testVerificationNoConnection() {
    startMockServer(true)

    `when`(connectionRepository.default).thenAnswer {
      return@thenAnswer null
    }

    val subscriber = verifier.verify().test()
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(RuntimeException::class.java)
  }

  @Test fun testVerificationNoJsonPayload() {
    startMockServer(false, "payload", false)

    `when`(connectionRepository.default).thenAnswer {
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
      bind(ConnectionVerifier::class.java).to(ConnectionVerifierImpl::class.java)
    }
  }
}
