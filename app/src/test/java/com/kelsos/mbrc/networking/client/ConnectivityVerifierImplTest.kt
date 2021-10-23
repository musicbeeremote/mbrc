package com.kelsos.mbrc.networking.client

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.data.DeserializationAdapter
import com.kelsos.mbrc.data.DeserializationAdapterImpl
import com.kelsos.mbrc.data.SerializationAdapter
import com.kelsos.mbrc.data.SerializationAdapterImpl
import com.kelsos.mbrc.features.settings.ClientInformationStore
import com.kelsos.mbrc.networking.RequestManager
import com.kelsos.mbrc.networking.RequestManagerImpl
import com.kelsos.mbrc.networking.connections.ConnectionDao
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettings
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utils.testDispatcher
import com.kelsos.mbrc.utils.testDispatcherModule
import com.squareup.moshi.Moshi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.single
import org.koin.test.KoinTest
import org.koin.test.inject
import timber.log.Timber
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
class ConnectivityVerifierImplTest : KoinTest {

  private val port: Int = 46000
  private val verifier: ConnectivityVerifier by inject()
  private lateinit var db: Database
  private lateinit var dao: ConnectionDao
  private val informationStore: ClientInformationStore = mockk()
  private val connectionRepository: ConnectionRepository = mockk()
  private val moshi: Moshi by inject()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, Database::class.java)
      .allowMainThreadQueries()
      .build()
    dao = db.connectionDao()
    coEvery { informationStore.getClientId() } returns "abc"
    startKoin {
      modules(listOf(testModule, testDispatcherModule))
    }
  }

  @After
  fun tearDown() {
    db.close()
    stopKoin()
    Dispatchers.resetMain()
  }

  private fun startMockServer(
    prematureDisconnect: Boolean = false,
    responseContext: Protocol = Protocol.VerifyConnection,
    json: Boolean = true
  ): ServerSocket {
    val random = Random()
    val executor: ExecutorService = Executors.newSingleThreadExecutor()
    val server = ServerSocket(port + random.nextInt(1000))
    val adapter = moshi.adapter(SocketMessage::class.java)
    val mockSocket = Runnable {
      server.soTimeout = 3000

      while (true) {
        Timber.v("Listening on ${server.inetAddress.hostAddress}:${server.localPort}")
        val connection = server.accept()
        val input = InputStreamReader(connection!!.inputStream)
        val inputReader = BufferedReader(input)
        val line = inputReader.readLine()
        Timber.v("Received a message $line")

        val value = adapter.fromJson(line)

        if (Protocol.fromString(value?.context ?: "") != Protocol.VerifyConnection) {
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

  @Test
  fun testSuccessfulVerification() = runBlockingTest(testDispatcher) {
    val server = startMockServer()

    coEvery { connectionRepository.getDefault() } answers {
      return@answers server.defaultConnection()
    }

    assertThat(verifier.verify().isRight()).isTrue()
  }

  @Test
  fun testPrematureDisconnectDuringVerification() = runBlockingTest(testDispatcher) {
    val server = startMockServer(true)
    coEvery { connectionRepository.getDefault() } answers {
      return@answers server.defaultConnection()
    }

    val result = verifier.verify()
    assertThat(result.isLeft()).isTrue()
    val value = (result as Either.Left).value
    assertThat(value).isInstanceOf(ConnectivityVerifierImpl.NoValidPluginConnection::class.java)
    assertThat(value).hasCauseThat().isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun testInvalidPluginResponseVerification() = runBlockingTest(testDispatcher) {
    val server = startMockServer(false, Protocol.ClientNotAllowed)
    coEvery { connectionRepository.getDefault() } answers {
      return@answers server.defaultConnection()
    }

    val result = verifier.verify()
    assertThat(result.isLeft()).isTrue()
    val value = (result as Either.Left).value
    assertThat(value).isInstanceOf(ConnectivityVerifierImpl.NoValidPluginConnection::class.java)
    assertThat(value).hasCauseThat().isNull()
  }

  private fun ServerSocket.defaultConnection(): ConnectionSettings =
    ConnectionSettings(
      address = inetAddress.hostAddress,
      port = localPort,
      name = "default",
      id = 1,
      isDefault = true
    )

  @Test
  fun testVerificationNoConnection() = runBlockingTest(testDispatcher) {
    startMockServer(true)

    coEvery { connectionRepository.getDefault() } answers {
      return@answers null
    }

    val result = verifier.verify()
    assertThat(result.isLeft()).isTrue()
    val value = (result as Either.Left).value
    assertThat(value).isInstanceOf(ConnectivityVerifierImpl.NoValidPluginConnection::class.java)
    assertThat(value).hasCauseThat().isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun testVerificationNoJsonPayload() = runBlockingTest(testDispatcher) {
    startMockServer(false, Protocol.fromString("a"), false)

    coEvery { connectionRepository.getDefault() } answers {
      return@answers null
    }

    val result = verifier.verify()
    assertThat(result.isLeft()).isTrue()
    val value = (result as Either.Left).value
    assertThat(value).isInstanceOf(ConnectivityVerifierImpl.NoValidPluginConnection::class.java)
    assertThat(value).hasCauseThat().isInstanceOf(RuntimeException::class.java)
  }

  private val testModule = module {
    single { Moshi.Builder().build() }
    single { connectionRepository }
    single { dao }
    single<RequestManagerImpl>() bind RequestManager::class
    single<ConnectivityVerifierImpl>() bind ConnectivityVerifier::class
    single<DeserializationAdapterImpl>() bind DeserializationAdapter::class
    single<SerializationAdapterImpl>() bind SerializationAdapter::class
    single { informationStore }
  }
}
