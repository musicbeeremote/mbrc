package com.kelsos.mbrc.services

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.networking.client.ConnectivityVerifier
import com.kelsos.mbrc.networking.client.ConnectivityVerifierImpl
import com.kelsos.mbrc.networking.client.SocketMessage
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.networking.protocol.Protocol
import com.squareup.moshi.Moshi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.experimental.builder.create
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declareMock
import org.mockito.BDDMockito.given
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.util.Random
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ConnectivityVerifierImplTest : KoinTest {

  private val port: Int = 46000

  private val verifier: ConnectivityVerifier by inject()
  private val connectionRepository: ConnectionRepository by inject()
  private val moshi: Moshi by inject()

  @Before
  fun setUp() {
    startKoin(listOf(testModule))
    declareMock<ConnectionRepository>()
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  private fun startMockServer(
    prematureDisconnect: Boolean = false,
    responseContext: String = Protocol.VerifyConnection,
    json: Boolean = true
  ): ServerSocket {
    val random = Random()
    val executor: ExecutorService = Executors.newSingleThreadExecutor()
    val server = ServerSocket(port + random.nextInt(1000))
    val mockSocket = Runnable {
      server.soTimeout = 3000
      val messageAdapter = moshi.adapter<SocketMessage>(SocketMessage::class.java)

      while (true) {
        val connection = server.accept()
        val input = InputStreamReader(connection!!.inputStream)
        val inputReader = BufferedReader(input)
        val line = inputReader.readLine()

        val value = messageAdapter.fromJson(line)

        if (value?.context != Protocol.VerifyConnection) {
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
          val response = SocketMessage(context = responseContext)
          output.write(messageAdapter.toJson(response) + newLine + newLine)
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

  @Test
  fun testSuccessfulVerification() {
    val server = startMockServer()

    given(connectionRepository.default).willAnswer {
      val settings = ConnectionSettingsEntity()
      settings.address = server.inetAddress.hostAddress
      settings.port = server.localPort
      return@willAnswer settings
    }

    val subscriber = verifier.verify().test()
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertComplete()
    subscriber.assertNoErrors()
    subscriber.assertValueCount(1)
    subscriber.assertValue(true)
  }

  @Test
  fun testPrematureDisconnectDuringVerification() {
    val server = startMockServer(true)
    given(connectionRepository.default).willAnswer {
      val settings = ConnectionSettingsEntity()
      settings.address = server.inetAddress.hostAddress
      settings.port = server.localPort
      return@willAnswer settings
    }

    val subscriber = verifier.verify().test()
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(RuntimeException::class.java)
  }

  @Test
  fun testInvalidPluginResponseVerification() {
    val server = startMockServer(false, Protocol.ClientNotAllowed)
    given(connectionRepository.default).willAnswer {
      val settings = ConnectionSettingsEntity()
      settings.address = server.inetAddress.hostAddress
      settings.port = server.localPort
      return@willAnswer settings
    }

    val subscriber = verifier.verify().test()
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(ConnectivityVerifierImpl.NoValidPluginConnection::class.java)
  }

  @Test
  fun testVerificationNoConnection() {
    startMockServer(true)

    given(connectionRepository.default).willAnswer {
      return@willAnswer null
    }

    val subscriber = verifier.verify().test()
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(RuntimeException::class.java)
  }

  @Test
  fun testVerificationNoJsonPayload() {
    startMockServer(false, "payload", false)

    given(connectionRepository.default).willAnswer {
      return@willAnswer null
    }

    val subscriber = verifier.verify().test()
    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)
    subscriber.assertError(RuntimeException::class.java)
  }

  private val testModule = module {
    single { Moshi.Builder().build() }
    single<ConnectivityVerifier> { create<ConnectivityVerifierImpl>() }
  }
}