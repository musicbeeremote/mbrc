package com.kelsos.mbrc.features.playlists.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.features.playlists.Playlist
import com.kelsos.mbrc.features.playlists.PlaylistDao
import com.kelsos.mbrc.features.playlists.PlaylistDto
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utils.TestData
import com.kelsos.mbrc.utils.TestData.mockApi
import com.kelsos.mbrc.utils.TestDataFactories
import com.kelsos.mbrc.utils.collectDataForTest
import com.kelsos.mbrc.utils.result
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
import java.net.SocketTimeoutException
import kotlin.time.Duration

@RunWith(AndroidJUnit4::class)
class PlaylistRepositoryTest : KoinTest {

  private lateinit var apiBase: ApiBase
  private lateinit var database: Database
  private lateinit var dao: PlaylistDao

  private val repository: PlaylistRepository by inject()

  @get:Rule
  val rule = InstantTaskExecutorRule()

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = TestData.createDB(context)
    dao = database.playlistDao()
    apiBase = mockk()

    startKoin {
      modules(
        listOf(
          module {
            single { dao }
            single<PlaylistRepositoryImpl>() bind PlaylistRepository::class
            single { apiBase }
          },
          testDispatcherModule
        )
      )
    }
  }

  @After
  fun tearDown() {
    database.close()
    stopKoin()
  }

  @Test
  fun `sync is failure if there is an exception`() = runBlockingTest {
    coEvery {
      apiBase.getAllPages(
        Protocol.PlaylistList,
        PlaylistDto::class,
        any()
      )
    } throws SocketTimeoutException()
    assertThat(repository.getRemote().result()).isInstanceOf(SocketTimeoutException::class.java)
  }

  @Test
  fun `it should be initially empty`() = runBlockingTest {
    assertThat(repository.cacheIsEmpty()).isTrue()
    assertThat(repository.count()).isEqualTo(0)
    repository.getAll().test(timeout = Duration.seconds(10)) {
      val data = awaitItem().collectDataForTest()
      assertThat(data).isEmpty()
    }
    advanceUntilIdle()
  }

  @Test
  fun `sync remote playlists and update database`() = runBlockingTest {
    coEvery { apiBase.getAllPages(Protocol.PlaylistList, PlaylistDto::class, any()) } answers {
      mockApi(20) {
        TestDataFactories.playlist(it)
      }
    }
    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.count()).isEqualTo(20)
    repository.getAll().test(timeout = Duration.seconds(10)) {
      val data = awaitItem().collectDataForTest()
      assertThat(data).hasSize(20)
    }
    advanceUntilIdle()
  }

  @Test
  fun `it should filter the playlists when searching`() = runBlockingTest {
    val extra = listOf(PlaylistDto(name = "Heavy Metal", url = """C:\library\metal.m3u"""))
    coEvery { apiBase.getAllPages(Protocol.PlaylistList, PlaylistDto::class, any()) } answers {
      mockApi(5, extra) {
        TestDataFactories.playlist(it)
      }
    }

    assertThat(repository.getRemote().isRight()).isTrue()
    repository.search("Metal").test(timeout = Duration.seconds(10)) {
      val data = awaitItem().collectDataForTest()
      assertThat(data).hasSize(1)
      assertThat(data).containsExactly(
        Playlist(
          name = "Heavy Metal",
          url = """C:\library\metal.m3u""",
          id = 6
        )
      )
    }
    advanceUntilIdle()
  }
}
