package com.kelsos.mbrc.features.radio.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.common.data.cacheIsEmpty
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.features.radio.RadioRepository
import com.kelsos.mbrc.features.radio.RadioRepositoryImpl
import com.kelsos.mbrc.features.radio.RadioStation
import com.kelsos.mbrc.features.radio.RadioStationDao
import com.kelsos.mbrc.features.radio.RadioStationDto
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.rules.CoroutineTestRule
import com.kelsos.mbrc.utils.TestData
import com.kelsos.mbrc.utils.TestData.mockApi
import com.kelsos.mbrc.utils.testDispatcherModule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.net.SocketTimeoutException

@RunWith(AndroidJUnit4::class)
class RadioRepositoryTest : KoinTest {

  private val repository: RadioRepository by inject()

  private lateinit var db: Database
  private lateinit var dao: RadioStationDao
  private lateinit var apiBase: ApiBase

  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  var coroutineTestRule = CoroutineTestRule()

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
            singleOf(::RadioRepositoryImpl) { bind<RadioRepository>() }
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
  fun `sync is failure if there is an exception`() = runTest {
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
  fun `sync remote data and update the database`() = runTest {
    assertThat(repository.cacheIsEmpty()).isTrue()

    coEvery { apiBase.getAllPages(Protocol.RadioStations, RadioStationDto::class, any()) } answers {
      mockApi(2) {
        RadioStationDto(name = "Radio $it", url = "http://radio.statio/$it")
      }
    }

    assertThat(repository.getRemote().isRight()).isTrue()
    assertThat(repository.count()).isEqualTo(2)
    assertThat(repository.test.getAll()).hasSize(2)
  }

  @Test
  fun `it should filter the stations when searching`() = runTest {
    coEvery { apiBase.getAllPages(Protocol.RadioStations, RadioStationDto::class, any()) } answers {
      mockApi(5, listOf(RadioStationDto(name = "Heavy Metal", url = "http://heavy.metal.ru"))) {
        RadioStationDto(name = "Radio $it", url = "http://radio.statio/$it")
      }
    }

    assertThat(repository.getRemote().isRight()).isTrue()

    val data = repository.test.search("Metal")
    assertThat(data).hasSize(1)
    assertThat(data).containsExactly(
      RadioStation(
        name = "Heavy Metal",
        url = "http://heavy.metal.ru",
        id = 6
      )
    )
  }
}
