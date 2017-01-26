package com.kelsos.mbrc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.ConnectionSettings
import com.kelsos.mbrc.data.SocketMessage
import com.kelsos.mbrc.repository.ConnectionRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import rx.observers.TestSubscriber
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import toothpick.testing.ToothPickTestModule
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ConnectionVerifierImplTest {
  lateinit var server: ServerSocket

  @Mock lateinit var connectionRepository: ConnectionRepository
  private val mapper = ObjectMapper()
  private var scope: Scope? = null

  private val executor: Executor = Executors.newSingleThreadExecutor()

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)

    scope = Toothpick.openScope(this)
    scope!!.installModules(ToothPickTestModule(this), TestModule())
  }

  fun startMockServer(prematureDisconnect: Boolean = false, responseContext: String = Protocol.VerifyConnection) {
    val mockSocket = Runnable {

      try {
        server = ServerSocket(39192)

        while (true) {
          val connection = server.accept()
          val input = InputStreamReader(connection.inputStream)
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
          val response = SocketMessage()
          response.context = responseContext
          output.write(mapper.writeValueAsString(response) + "\n\r")
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
    server.close()
  }

  @Test fun testSuccessfulVerification() {
    startMockServer()

    `when`(connectionRepository.default).thenAnswer {
      val settings = ConnectionSettings()
      settings.address = server.inetAddress.hostAddress
      settings.port = server.localPort
      return@thenAnswer settings
    }

    val verifier = scope!!.getInstance(ConnectionVerifier::class.java)
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
      settings.address = server.inetAddress.hostAddress
      settings.port = server.localPort
      return@thenAnswer settings
    }

    val verifier = scope!!.getInstance(ConnectionVerifier::class.java)
    val subscriber = TestSubscriber<Boolean>()
    verifier.verify().subscribe(subscriber)
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

    val verifier = scope!!.getInstance(ConnectionVerifier::class.java)
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

    val verifier = scope!!.getInstance(ConnectionVerifier::class.java)
    val subscriber = TestSubscriber<Boolean>()
    verifier.verify().subscribe(subscriber)
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(RuntimeException::class.java)
  }

  //todo no socket message payload test

  inner class TestModule: Module() {
    init {
      bind(ObjectMapper::class.java).toInstance(mapper)
      bind(ConnectionRepository::class.java).toInstance(connectionRepository)
      bind(ConnectionVerifier::class.java).to(ConnectionVerifierImpl::class.java)
    }
  }
}
