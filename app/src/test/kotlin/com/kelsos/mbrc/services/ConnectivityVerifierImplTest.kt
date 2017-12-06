package com.kelsos.mbrc.services

import android.app.Application
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.TestApplication
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.networking.ConnectivityVerifier
import com.kelsos.mbrc.networking.ConnectivityVerifierImpl
import com.kelsos.mbrc.networking.RequestManager
import com.kelsos.mbrc.networking.RequestManagerImpl
import com.kelsos.mbrc.networking.SocketMessage
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettings
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.ClientInformationStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import toothpick.config.Module
import toothpick.testing.ToothPickRule
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.util.Random
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
class ConnectivityVerifierImplTest {
  private val connectionRepository: ConnectionRepository = mockk()
  private val testDispatcher = TestCoroutineDispatcher()

  @Rule
  @JvmField
  val toothpickRule: ToothPickRule = ToothPickRule(this, "verifier")
    .setRootRegistryPackage("com.kelsos.mbrc")
  private val mapper = ObjectMapper()
  private val port: Int = 46000

  lateinit var verifier: ConnectivityVerifier
  private val informationStore: ClientInformationStore = mockk()

  @Before
  fun setUp() {
    toothpickRule.scope.installModules(TestModule())
    verifier = toothpickRule.getInstance(ConnectivityVerifier::class.java)
    every { informationStore.getClientId() } returns "abc"
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

  @Test
  fun testSuccessfulVerification() = testDispatcher.runBlockingTest {
    val server = startMockServer()

    coEvery { connectionRepository.getDefault() } answers {
      val settings = ConnectionSettings()
      settings.address = server.inetAddress.hostAddress
      settings.port = server.localPort
      settings
    }

    val verifier = toothpickRule.getInstance(ConnectivityVerifier::class.java)
    assertThat(verifier.verify()).isTrue()
  }

  @Test
  fun testPrematureDisconnectDuringVerification() = testDispatcher.runBlockingTest {
    val server = startMockServer(true)
    coEvery { connectionRepository.getDefault() } answers {
      val settings = ConnectionSettings()
      settings.address = server.inetAddress.hostAddress
      settings.port = server.localPort
      settings
    }

    val verifier = toothpickRule.getInstance(ConnectivityVerifier::class.java)
    try {
      verifier.verify()
      error("Test should throw")
    } catch (e: Exception) {
      assertThat(e).isInstanceOf(RuntimeException::class.java)
    }
  }

  @Test
  fun testInvalidPluginResponseVerification() = testDispatcher.runBlockingTest {
    val server = startMockServer(false, Protocol.ClientNotAllowed)
    coEvery { connectionRepository.getDefault() } answers {
      val settings = ConnectionSettings()
      settings.address = server.inetAddress.hostAddress
      settings.port = server.localPort
      settings
    }

    val verifier = toothpickRule.getInstance(ConnectivityVerifier::class.java)
    try {
      println(verifier.verify())
      error("Test should throw")
    } catch (e: Exception) {
      assertThat(e).isInstanceOf(ConnectivityVerifierImpl.NoValidPluginConnection::class.java)
    }
  }

  @Test
  fun testVerificationNoConnection() = testDispatcher.runBlockingTest {
    startMockServer(true)

    coEvery { connectionRepository.getDefault() } answers {
      null
    }

    val verifier = toothpickRule.getInstance(ConnectivityVerifier::class.java)
    try {
      verifier.verify()
      error("Test should throw")
    } catch (e: Exception) {
      assertThat(e).isInstanceOf(RuntimeException::class.java)
    }
  }

  @Test
  fun testVerificationNoJsonPayload() = testDispatcher.runBlockingTest {
    startMockServer(false, "payload", false)

    coEvery { connectionRepository.getDefault() } answers {
      null
    }

    val verifier = toothpickRule.getInstance(ConnectivityVerifier::class.java)
    try {
      verifier.verify()
      error("Test should throw")
    } catch (e: Exception) {
      assertThat(e).isInstanceOf(RuntimeException::class.java)
    }
  }

  inner class TestModule : Module() {
    init {
      val applicationContext = getApplicationContext<TestApplication>()
      bind(Application::class.java).toInstance(applicationContext)
      bind(ObjectMapper::class.java).toInstance(mapper)
      bind(RequestManager::class.java).to(RequestManagerImpl::class.java)
      bind(ConnectionRepository::class.java).toInstance(connectionRepository)
      bind(AppDispatchers::class.java).toInstance(
        AppDispatchers(
          testDispatcher,
          testDispatcher,
          testDispatcher
        )
      )
      bind(ClientInformationStore::class.java).toInstance(informationStore)
    }
  }
}
