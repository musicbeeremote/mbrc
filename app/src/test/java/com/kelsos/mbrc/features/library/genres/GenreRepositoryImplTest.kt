package com.kelsos.mbrc.features.library.genres

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.features.library.data.GenreDao
import com.kelsos.mbrc.features.library.dto.GenreDto
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import com.kelsos.mbrc.features.library.repositories.GenreRepositoryImpl
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utils.TestData
import com.kelsos.mbrc.utils.collectDataForTest
import com.kelsos.mbrc.utils.testDispatcher
import com.kelsos.mbrc.utils.testDispatcherModule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
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
class GenreRepositoryImplTest : KoinTest {
  private val repository: GenreRepository by inject()

  private lateinit var db: Database
  private lateinit var genreDao: GenreDao

  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = TestData.createDB(context)
    genreDao = db.genreDao()
    startKoin {
      modules(listOf(testModule, testDispatcherModule))
    }
  }

  @After
  fun tearDown() {
    db.close()
    stopKoin()
    Dispatchers.resetMain()
    testDispatcher.cleanupTestCoroutines()
  }

  @Test
  fun getAndSaveRemote() = runBlockingTest(testDispatcher) {
    assertThat(repository.cacheIsEmpty()).isTrue()
    repository.getRemote()
    repository.getAll().test(timeout = Duration.seconds(10)) {
      val data = awaitItem().collectDataForTest()
      assertThat(data).hasSize(10)
      assertThat(data[0].genre).isEqualTo("Metal0")
    }
    advanceUntilIdle()
  }

  private val testModule = module {
    single<GenreRepositoryImpl>() bind GenreRepository::class

    val mockApi = mockk<ApiBase>()

    coEvery { mockApi.getAllPages(Protocol.LibraryBrowseGenres, GenreDto::class, any()) } answers {
      flow {
        emit((0 until 10).map { GenreDto("Metal$it") })
      }
    }
    single { mockApi }
    single { genreDao }
  }
}
