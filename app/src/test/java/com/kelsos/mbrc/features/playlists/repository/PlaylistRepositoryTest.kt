package com.kelsos.mbrc.features.playlists.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.TestApplication
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.features.playlists.data.PlaylistDao
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.utilities.paged
import com.kelsos.mbrc.utils.TestData
import com.kelsos.mbrc.utils.observeOnce
import com.kelsos.mbrc.utils.testDispatcherModule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.experimental.builder.create
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
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

    startKoin(listOf(module {
      single { dao }
      single<PlaylistRepository> { create<PlaylistRepositoryImpl>() }
      single { apiBase }
    }, testDispatcherModule))
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `it should be initially empty`() = runBlockingTest {
    assertThat(repository.cacheIsEmpty())
    assertThat(repository.count()).isEqualTo(0)
    repository.getAll().paged().observeOnce { list ->
      assertThat(list).isEmpty()
    }
  }
}
