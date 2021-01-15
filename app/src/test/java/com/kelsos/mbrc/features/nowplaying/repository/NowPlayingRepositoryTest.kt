package com.kelsos.mbrc.features.nowplaying.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.features.nowplaying.NowPlayingDto
import com.kelsos.mbrc.features.nowplaying.data.NowPlayingDao
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utils.TestData
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

@ExperimentalCoroutinesApi
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

    startKoin {
      modules(
        listOf(
          module {
            single { dao }
            singleBy<NowPlayingRepository, NowPlayingRepositoryImpl>()
            single { apiBase }
          },
          testDispatcherModule
        )
      )
    }
  }

  @After
  fun tearDown() {
    stopKoin()
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
  fun `user moves track from position 1 to position 5`() = runBlockingTest {
    every { apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, any()) } answers {
      TestData.mockApi(20) {
        TestDataFactories.nowPlayingList(it)
      }
    }

    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.count()).isEqualTo(20)

    repository.move(1, 5)
    repository.getAll().paged().observeOnce { list ->
      assertThat(list[0]!!.title).isEqualTo("Song 2")
      assertThat(list[1]!!.title).isEqualTo("Song 3")
      assertThat(list[2]!!.title).isEqualTo("Song 4")
      assertThat(list[3]!!.title).isEqualTo("Song 5")
      assertThat(list[4]!!.title).isEqualTo("Song 1")
      assertThat(list[5]!!.title).isEqualTo("Song 6")
    }
  }

  @Test
  fun `user moves track from position 6 to position 1`() = runBlockingTest {
    every { apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, any()) } answers {
      TestData.mockApi(20) {
        TestDataFactories.nowPlayingList(it)
      }
    }

    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.count()).isEqualTo(20)

    repository.move(6, 1)
    repository.getAll().paged().observeOnce { list ->
      assertThat(list[0]!!.title).isEqualTo("Song 6")
      assertThat(list[1]!!.title).isEqualTo("Song 1")
      assertThat(list[2]!!.title).isEqualTo("Song 2")
      assertThat(list[3]!!.title).isEqualTo("Song 3")
      assertThat(list[4]!!.title).isEqualTo("Song 4")
      assertThat(list[5]!!.title).isEqualTo("Song 5")
      assertThat(list[6]!!.title).isEqualTo("Song 7")
    }
  }

  @Test
  fun `user removes a track`() = runBlockingTest {
    every { apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, any()) } answers {
      TestData.mockApi(20) {
        TestDataFactories.nowPlayingList(it)
      }
    }

    assertThat(repository.getRemote().isRight()).isTrue()
    assertThat(repository.count()).isEqualTo(20)

    repository.remove(2)
    repository.getAll().paged().observeOnce { list ->
      assertThat(list[0]!!.title).isEqualTo("Song 1")
      assertThat(list[1]!!.title).isEqualTo("Song 3")
      assertThat(list[2]!!.title).isEqualTo("Song 4")
      assertThat(list[3]!!.title).isEqualTo("Song 5")
    }
    assertThat(repository.count()).isEqualTo(19)
  }

  @Test
  fun `user search should return filtered results`() = runBlockingTest {
    every { apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, any()) } answers {
      TestData.mockApi(20) {
        TestDataFactories.nowPlayingList(it)
      }
    }

    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.count()).isEqualTo(20)

    repository.search("Song 6").paged().observeOnce { list ->
      assertThat(list).hasSize(1)
      assertThat(list.first()).isEqualTo(
        NowPlaying(
          title = "Song 6",
          artist = "Artist",
          position = 6,
          path = "C:\\library\\album\\6.mp3",
          id = 6
        )
      )
    }
  }

  @Test
  fun `updated items should keep the same ids`() = runBlockingTest {
    every { apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, any()) } answers {
      TestData.mockApi(5) {
        TestDataFactories.nowPlayingList(it)
      }
    }

    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.count()).isEqualTo(5)
    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)

    repository.getAll().paged().observeOnce { list ->
      assertThat(list).hasSize(5)
      assertThat(list[0]!!.id).isEqualTo(1)
      assertThat(list[1]!!.id).isEqualTo(2)
      assertThat(list[2]!!.id).isEqualTo(3)
      assertThat(list[3]!!.id).isEqualTo(4)
      assertThat(list[4]!!.id).isEqualTo(5)
    }
  }

  @Test
  fun `search should return -1 if item is not found`() = runBlockingTest {
    every { apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, any()) } answers {
      TestData.mockApi(5) {
        TestDataFactories.nowPlayingList(it)
      }
    }

    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.findPosition("Song 15")).isEqualTo(-1)
  }

  @Test
  fun `search should return the position if item is found`() = runBlockingTest {
    every { apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, any()) } answers {
      TestData.mockApi(5) {
        TestDataFactories.nowPlayingList(it)
      }
    }

    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    assertThat(repository.findPosition("Song 5")).isEqualTo(5)
  }
}
