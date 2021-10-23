package com.kelsos.mbrc.features.radio.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.features.radio.RadioRepository
import com.kelsos.mbrc.features.radio.RadioRepositoryImpl
import com.kelsos.mbrc.features.radio.RadioStation
import com.kelsos.mbrc.features.radio.RadioStationDao
import com.kelsos.mbrc.features.radio.RadioStationDto
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utils.TestData
import com.kelsos.mbrc.utils.TestData.mockApi
import com.kelsos.mbrc.utils.collectDataForTest
import com.kelsos.mbrc.utils.testDispatcher
import com.kelsos.mbrc.utils.testDispatcherModule
import com.kelsos.mbrc.utils.testScope
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.single
import org.koin.test.KoinTest
import org.koin.test.inject
import java.net.SocketTimeoutException
import kotlin.time.Duration

@RunWith(AndroidJUnit4::class)
class RadioRepositoryTest : KoinTest {

  private val repository: RadioRepository by inject()

  private lateinit var db: Database
  private lateinit var dao: RadioStationDao
  private lateinit var apiBase: ApiBase

  @get:Rule
  val rule = InstantTaskExecutorRule()

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = TestData.createDB(context)
    dao = db.radioStationDao()
    apiBase = mockk()

    startKoin {
      modules(
        listOf(
          module {
            single { dao }
            single<RadioRepositoryImpl>() bind RadioRepository::class
            single { apiBase }
          },
          testDispatcherModule
        )
      )
    }
  }

  @After
  fun tearDown() {
    db.close()
    stopKoin()
  }

  @Test
  fun `sync is failure if there is an exception`() = runBlockingTest(testDispatcher) {
    coEvery {
      apiBase.getAllPages(
        Protocol.RadioStations,
        RadioStationDto::class,
        any()
      )
    } throws SocketTimeoutException()

    assertThat(repository.getRemote().isLeft()).isTrue()
  }

  @Test
  fun `sync remote data and update the database`() = testScope.runBlockingTest {
    assertThat(repository.cacheIsEmpty()).isTrue()

    coEvery { apiBase.getAllPages(Protocol.RadioStations, RadioStationDto::class, any()) } answers {
      mockApi(2) {
        RadioStationDto(name = "Radio $it", url = "http://radio.statio/$it")
      }
    }

    assertThat(repository.getRemote().isRight()).isTrue()
    assertThat(repository.count()).isEqualTo(2)

    repository.getAll().test(timeout = Duration.seconds(10)) {
      val data = awaitItem().collectDataForTest()
      assertThat(data).hasSize(2)
    }
    advanceUntilIdle()
  }

  @Test
  fun `it should filter the stations when searching`() = runBlockingTest(testDispatcher) {
    coEvery { apiBase.getAllPages(Protocol.RadioStations, RadioStationDto::class, any()) } answers {
      mockApi(5, listOf(RadioStationDto(name = "Heavy Metal", url = "http://heavy.metal.ru"))) {
        RadioStationDto(name = "Radio $it", url = "http://radio.statio/$it")
      }
    }

    assertThat(repository.getRemote().isRight()).isTrue()

    repository.search("Metal").test(timeout = Duration.seconds(10)) {
      val data = awaitItem().collectDataForTest()
      assertThat(data).hasSize(1)
      assertThat(data).containsExactly(
        RadioStation(
          name = "Heavy Metal",
          url = "http://heavy.metal.ru",
          id = 6
        )
      )
    }
    advanceUntilIdle()
  }
}
