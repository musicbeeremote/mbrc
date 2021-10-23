package com.kelsos.mbrc.features.nowplaying.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.features.nowplaying.NowPlayingDto
import com.kelsos.mbrc.features.nowplaying.data.NowPlayingDao
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utils.TestData
import com.kelsos.mbrc.utils.TestData.mockApi
import com.kelsos.mbrc.utils.TestDataFactories.nowPlayingList
import com.kelsos.mbrc.utils.collectDataForTest
import com.kelsos.mbrc.utils.result
import com.kelsos.mbrc.utils.testDispatcher
import com.kelsos.mbrc.utils.testDispatcherModule
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
import kotlin.time.Duration

@RunWith(AndroidJUnit4::class)
class NowPlayingRepositoryTest : KoinTest {
  private lateinit var apiBase: ApiBase
  private lateinit var database: Database
  private lateinit var dao: NowPlayingDao

  private val repository: NowPlayingRepository by inject()

  @get:Rule
  val rule = InstantTaskExecutorRule()

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = TestData.createDB(context)
    dao = database.nowPlayingDao()
    apiBase = mockk()

    val modules = listOf(
      module {
        single { dao }
        single<NowPlayingRepositoryImpl>() bind NowPlayingRepository::class
        single { apiBase }
      },
      testDispatcherModule
    )
    startKoin {
      modules(modules)
    }
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun `it should be initially empty`() = runBlockingTest(testDispatcher) {
    assertThat(repository.cacheIsEmpty()).isTrue()
    assertThat(repository.count()).isEqualTo(0)

    repository.getAll().test(timeout = Duration.seconds(10)) {
      val data = awaitItem().collectDataForTest()
      assertThat(data).isEmpty()
    }
    advanceUntilIdle()
  }

  @Test
  fun `user moves track from position 1 to position 5`() = runBlockingTest(testDispatcher) {
    coEvery { apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, any()) } answers {
      mockApi(20) {
        nowPlayingList(it)
      }
    }

    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.count()).isEqualTo(20)

    repository.move(1, 5)
    repository.getAll().test(timeout = Duration.seconds(10)) {
      val data = awaitItem().collectDataForTest()
      assertThat(data.map { it.title }.take(6)).containsExactlyElementsIn(
        listOf(
          "Song 2",
          "Song 3",
          "Song 4",
          "Song 5",
          "Song 1",
          "Song 6"
        )
      )
    }
    advanceUntilIdle()
  }

  @Test
  fun `user moves track from position 6 to position 1`() = runBlockingTest(testDispatcher) {
    coEvery { apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, any()) } answers {
      mockApi(20) {
        nowPlayingList(it)
      }
    }

    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.count()).isEqualTo(20)

    repository.move(6, 1)
    repository.getAll().test(timeout = Duration.seconds(10)) {
      val data = awaitItem().collectDataForTest()
      assertThat(data.map { it.title }.take(7)).containsExactlyElementsIn(
        listOf(
          "Song 6",
          "Song 1",
          "Song 2",
          "Song 3",
          "Song 4",
          "Song 5",
          "Song 7"
        )
      )
    }
    advanceUntilIdle()
  }

  @Test
  fun `user removes a track`() = runBlockingTest(testDispatcher) {
    coEvery { apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, any()) } answers {
      mockApi(20) {
        nowPlayingList(it)
      }
    }

    assertThat(repository.getRemote().isRight()).isTrue()
    assertThat(repository.count()).isEqualTo(20)

    repository.remove(2)
    repository.getAll().test(timeout = Duration.seconds(10)) {
      val data = awaitItem().collectDataForTest()
      assertThat(data.map { it.title }.take(4)).containsExactlyElementsIn(
        listOf(
          "Song 1",
          "Song 3",
          "Song 4",
          "Song 5"
        )
      )
    }
    advanceUntilIdle()
    assertThat(repository.count()).isEqualTo(19)
  }

  @Test
  fun `user search should return filtered results`() = runBlockingTest(testDispatcher) {
    coEvery { apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, any()) } answers {
      mockApi(20) {
        nowPlayingList(it)
      }
    }

    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.count()).isEqualTo(20)
    repository.search("Song 6").test(timeout = Duration.seconds(10)) {
      val data = awaitItem().collectDataForTest()
      assertThat(data).hasSize(1)
      assertThat(data.first()).isEqualTo(
        NowPlaying(
          title = "Song 6",
          artist = "Artist",
          position = 6,
          path = "C:\\library\\album\\6.mp3",
          id = 6
        )
      )
    }
    advanceUntilIdle()
  }

  @Test
  fun `updated items should keep the same ids`() = runBlockingTest(testDispatcher) {
    coEvery { apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, any()) } answers {
      mockApi(5) {
        nowPlayingList(it)
      }
    }

    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.count()).isEqualTo(5)
    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)

    repository.getAll().test(timeout = Duration.seconds(10)) {
      val data = awaitItem().collectDataForTest()
      assertThat(data).hasSize(5)
      assertThat(data.map { it.id }.take(5)).containsExactlyElementsIn(
        listOf(
          1L,
          2L,
          3L,
          4L,
          5L,
        )
      )
    }
    advanceUntilIdle()
  }

  @Test
  fun `search should return -1 if item is not found`() = runBlockingTest(testDispatcher) {
    coEvery { apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, any()) } answers {
      mockApi(5) {
        nowPlayingList(it)
      }
    }

    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.findPosition("Song 15")).isEqualTo(-1)
  }

  @Test
  fun `search should return the position if item is found`() = runBlockingTest(testDispatcher) {
    coEvery { apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, any()) } answers {
      mockApi(5) {
        nowPlayingList(it)
      }
    }

    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.findPosition("Song 5")).isEqualTo(5)
  }
}
