package com.kelsos.mbrc.features.playlists.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.common.data.cacheIsEmpty
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.features.playlists.Playlist
import com.kelsos.mbrc.features.playlists.PlaylistDao
import com.kelsos.mbrc.features.playlists.PlaylistDto
import com.kelsos.mbrc.features.playlists.PlaylistRepository
import com.kelsos.mbrc.features.playlists.PlaylistRepositoryImpl
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.rules.CoroutineTestRule
import com.kelsos.mbrc.utils.TestData
import com.kelsos.mbrc.utils.TestData.mockApi
import com.kelsos.mbrc.utils.TestDataFactories
import com.kelsos.mbrc.utils.result
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
class PlaylistRepositoryTest : KoinTest {

  private lateinit var apiBase: ApiBase
  private lateinit var database: Database
  private lateinit var dao: PlaylistDao

  private val repository: PlaylistRepository by inject()

  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  var coroutineTestRule = CoroutineTestRule()

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
            singleOf(::PlaylistRepositoryImpl) { bind<PlaylistRepository>() }
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
  fun `sync is failure if there is an exception`() = runTest {
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
  fun `it should be initially empty`() = runTest {
    assertThat(repository.cacheIsEmpty()).isTrue()
    assertThat(repository.count()).isEqualTo(0)
    assertThat(repository.test.getAll()).isEmpty()
  }

  @Test
  fun `sync remote playlists and update database`() = runTest {
    coEvery { apiBase.getAllPages(Protocol.PlaylistList, PlaylistDto::class, any()) } answers {
      mockApi(20) {
        TestDataFactories.playlist(it)
      }
    }
    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.count()).isEqualTo(20)
    assertThat(repository.test.getAll()).hasSize(20)
  }

  @Test
  fun `it should filter the playlists when searching`() = runTest {
    val extra = listOf(PlaylistDto(name = "Heavy Metal", url = """C:\library\metal.m3u"""))
    coEvery { apiBase.getAllPages(Protocol.PlaylistList, PlaylistDto::class, any()) } answers {
      mockApi(5, extra) {
        TestDataFactories.playlist(it)
      }
    }

    assertThat(repository.getRemote().isRight()).isTrue()
    val data = repository.test.search("Metal")
    assertThat(data).hasSize(1)
    assertThat(data).containsExactly(
      Playlist(
        name = "Heavy Metal",
        url = """C:\library\metal.m3u""",
        id = 6
      )
    )
  }
}
