package com.kelsos.mbrc.content.library.genres

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.TestApplication
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.paged
import com.kelsos.mbrc.utils.observeOnce
import com.kelsos.mbrc.utils.testDispatcherModule
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.experimental.builder.create
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class GenreRepositoryImplTest : KoinTest {

  private val repository: GenreRepository by inject()

  private lateinit var db: Database
  private lateinit var genreDao: GenreDao

  @get:Rule
  val rule = InstantTaskExecutorRule()

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, Database::class.java)
      .allowMainThreadQueries()
      .build()
    genreDao = db.genreDao()
    startKoin(listOf(testModule, testDispatcherModule))
  }

  @After
  fun tearDown() {
    db.close()
    stopKoin()
  }

  @Test
  fun getAndSaveRemote() {
    runBlocking {
      assertThat(repository.cacheIsEmpty()).isTrue()
      repository.getRemote()
      repository.allGenres().factory.paged().observeOnce { list ->
        assertThat(list).hasSize(1200)
        assertThat(list.first().genre).isEqualTo("Metal0")
      }
    }
  }

  val testModule = module {
    single<GenreRepository> { create<GenreRepositoryImpl>() }

    val mockApi = mockk<ApiBase>()

    every { mockApi.getAllPages(Protocol.LibraryBrowseGenres, GenreDto::class) } answers {
      Observable.range(0, 1200)
        .map { GenreDto("Metal$it") }
        .toList()
        .toObservable()
    }
    single { mockApi }
    single { genreDao }
  }
}