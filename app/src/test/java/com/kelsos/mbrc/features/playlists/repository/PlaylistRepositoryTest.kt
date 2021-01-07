package com.kelsos.mbrc.features.playlists.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.features.playlists.PlaylistDto
import com.kelsos.mbrc.features.playlists.data.PlaylistDao
import com.kelsos.mbrc.features.playlists.domain.Playlist
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utils.TestData
import com.kelsos.mbrc.utils.TestData.mockApi
import com.kelsos.mbrc.utils.TestDataFactories
import com.kelsos.mbrc.utils.observeOnce
import com.kelsos.mbrc.utils.result
import com.kelsos.mbrc.utils.testDispatcherModule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import org.koin.test.KoinTest
import org.koin.test.inject
import java.net.SocketTimeoutException

@ExperimentalCoroutinesApi
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
            singleBy<PlaylistRepository, PlaylistRepositoryImpl>()
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
    every {
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
    assertThat(repository.cacheIsEmpty())
    assertThat(repository.count()).isEqualTo(0)
    repository.getAll().paged().observeOnce { list ->
      assertThat(list).isEmpty()
    }
  }

  @Test
  fun `sync remote playlists and update database`() = runBlockingTest {
    every { apiBase.getAllPages(Protocol.PlaylistList, PlaylistDto::class, any()) } answers {
      mockApi(20) {
        TestDataFactories.playlist(it)
      }
    }
    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.count()).isEqualTo(20)
    repository.getAll().paged().observeOnce { result ->
      assertThat(result).hasSize(20)
    }
  }

  @Test
  fun `it should filter the playlists when searching`() = runBlockingTest {
    val extra = listOf(PlaylistDto(name = "Heavy Metal", url = """C:\library\metal.m3u"""))
    every { apiBase.getAllPages(Protocol.PlaylistList, PlaylistDto::class, any()) } answers {
      mockApi(5, extra) {
        TestDataFactories.playlist(it)
      }
    }

    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    repository.search("Metal").paged().observeOnce {
      assertThat(it).hasSize(1)
      assertThat(it).containsExactly(
        Playlist(
          name = "Heavy Metal",
          url = """C:\library\metal.m3u""",
          id = 6
        )
      )
    }
  }
}
