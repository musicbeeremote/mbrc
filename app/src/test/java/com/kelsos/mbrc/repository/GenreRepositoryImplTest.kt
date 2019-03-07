package com.kelsos.mbrc.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.LoadState
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.content.library.genres.GenreDao
import com.kelsos.mbrc.content.library.genres.GenreDto
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.genres.GenreRepositoryImpl
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.ui.navigation.library.genres.GenreAdapter
import com.kelsos.mbrc.utils.noopListUpdateCallback
import com.kelsos.mbrc.utils.testDispatcher
import com.kelsos.mbrc.utils.testDispatcherModule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
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
import org.koin.dsl.module
import org.koin.experimental.builder.create
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.concurrent.CountDownLatch

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
    db = Room.inMemoryDatabaseBuilder(context, Database::class.java)
      .allowMainThreadQueries()
      .build()
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

    val differ = AsyncPagingDataDiffer(
      diffCallback = GenreAdapter.DIFF_CALLBACK,
      updateCallback = noopListUpdateCallback,
      mainDispatcher = testDispatcher,
      workerDispatcher = testDispatcher
    )
    val latch = CountDownLatch(1)
    differ.addLoadStateListener {
      if (it.prepend == LoadState.NotLoading(endOfPaginationReached = true)) {
        latch.countDown()
      }
    }

    val job = launch {
      repository.getAll().collectLatest { data ->
        differ.submitData(data)
      }
    }

    advanceUntilIdle()
    @Suppress("BlockingMethodInNonBlockingContext")
    latch.await()

    assertThat(differ.itemCount).isEqualTo(1200)
    assertThat(differ.getItem(0)?.genre).isEqualTo("Metal0")

    job.cancel()
  }

  private val testModule = module {
    single<GenreRepository> { create<GenreRepositoryImpl>() }

    val mockApi = mockk<ApiBase>()

    coEvery { mockApi.getAllPages(Protocol.LibraryBrowseGenres, GenreDto::class) } answers {
      flow {
        emit((0 until 1200).map { GenreDto("Metal$it") })
      }
    }
    single { mockApi }
    single { genreDao }
  }
}
