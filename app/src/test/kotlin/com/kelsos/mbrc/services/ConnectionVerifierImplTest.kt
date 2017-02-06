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
import rx.observers.TestSubscriber
import toothpick.config.Module
import toothpick.testing.ToothPickRule
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ConnectionVerifierImplTest {
  private var server: ServerSocket? = null

  @Mock lateinit var connectionRepository: ConnectionRepository
  @Rule @JvmField val toothpickRule: ToothPickRule = ToothPickRule(this, "verifier")
      .setRootRegistryPackage("com.kelsos.mbrc")
  private val mapper = ObjectMapper()


  private val executor: ExecutorService = Executors.newSingleThreadExecutor()

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)
    toothpickRule.scope.installModules(TestModule())
  }

  fun startMockServer(prematureDisconnect: Boolean = false,
                      responseContext: String = Protocol.VerifyConnection,
                      json: Boolean = true) {
    val mockSocket = Runnable {

      try {
        server = ServerSocket(39192)

        while (true) {
          val connection = server?.accept()
          val input = InputStreamReader(connection!!.inputStream)
          val inputReader = BufferedReader(input)
          val line = inputReader.readLine()
          val value = mapper.readValue(line, SocketMessage::class.java)

          if (value.context != Protocol.VerifyConnection) {
            connection.close()
            return@Runnable
          }

          if (prematureDisconnect) {
            connection.close()
            return@Runnable
          }

          val out = OutputStreamWriter(connection.outputStream, Const.UTF_8)
          val output = PrintWriter(BufferedWriter(out), true)
          if (json) {
            val response = SocketMessage()
            response.context = responseContext
            output.write(mapper.writeValueAsString(response) + "\n\r")
          } else {
            output.write(responseContext + "\n\r")
          }
          output.flush()
          input.close()
          inputReader.close()
          out.close()
          output.close()
          connection.close()
        }
      } catch(e: Exception) {
      }
    }

    executor.execute(mockSocket)
  }

  @After
  fun tearDown() {
    server?.close()
    executor.shutdownNow()
  }

  @Test fun testSuccessfulVerification() {
    startMockServer()

    `when`(connectionRepository.default).thenAnswer {
      val settings = ConnectionSettings()
      settings.address = server!!.inetAddress.hostAddress
      settings.port = server!!.localPort
      return@thenAnswer settings
    }

    val verifier = toothpickRule.getInstance(ConnectionVerifier::class.java)
    val subscriber = TestSubscriber<Boolean>()
    verifier.verify().subscribe(subscriber)
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertCompleted()
    subscriber.assertNoErrors()
    subscriber.assertValueCount(1)
    subscriber.assertValue(true)
  }


  @Test fun testPrematureDisconnectDuringVerification() {
    startMockServer(true)
    `when`(connectionRepository.default).thenAnswer {
      val settings = ConnectionSettings()
      settings.address = server!!.inetAddress.hostAddress
      settings.port = server!!.localPort
      return@thenAnswer settings
    }

    val verifier = toothpickRule.getInstance(ConnectionVerifier::class.java)
    val subscriber = TestSubscriber<Boolean>()
    verifier.verify().subscribe(subscriber)
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(RuntimeException::class.java)
  }

  @Test fun testInvalidPluginResponseVerification() {
    startMockServer(false, Protocol.ClientNotAllowed)
    `when`(connectionRepository.default).thenAnswer {
      val settings = ConnectionSettings()
      settings.address = server!!.inetAddress.hostAddress
      settings.port = server!!.localPort
      return@thenAnswer settings
    }

    val verifier = toothpickRule.getInstance(ConnectionVerifier::class.java)
    val subscriber = TestSubscriber<Boolean>()
    verifier.verify().subscribe(subscriber)
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(ConnectionVerifierImpl.NoValidPluginConnection::class.java)
  }

  @Test fun testVerificationNoConnection() {
    startMockServer(true)

    `when`(connectionRepository.default).thenAnswer {
      return@thenAnswer null
    }

    val verifier = toothpickRule.getInstance(ConnectionVerifier::class.java)
    val subscriber = TestSubscriber<Boolean>()
    verifier.verify().subscribe(subscriber)
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(RuntimeException::class.java)
  }

  @Test fun testVerificationNoJsonPayload() {
    startMockServer(false, "payload", false)

    `when`(connectionRepository.default).thenAnswer {
      return@thenAnswer null
    }

    val verifier = toothpickRule.getInstance(ConnectionVerifier::class.java)
    val subscriber = TestSubscriber<Boolean>()
    verifier.verify().subscribe(subscriber)
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
