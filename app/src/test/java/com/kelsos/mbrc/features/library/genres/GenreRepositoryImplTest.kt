package com.kelsos.mbrc.features.library.genres

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.features.library.data.GenreDao
import com.kelsos.mbrc.features.library.dto.GenreDto
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import com.kelsos.mbrc.features.library.repositories.GenreRepositoryImpl
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utils.TestData
import com.kelsos.mbrc.utils.TestDataFactories
import com.kelsos.mbrc.utils.observeOnce
import com.kelsos.mbrc.utils.result
import com.kelsos.mbrc.utils.testDispatcherModule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
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

@RunWith(AndroidJUnit4::class)
class GenreRepositoryImplTest : KoinTest {

  private val repository: GenreRepository by inject()

  private lateinit var db: Database
  private lateinit var genreDao: GenreDao

  @get:Rule
  val rule = InstantTaskExecutorRule()

  @Before
  fun setUp() {
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
  }

  @Test
  fun getAndSaveRemote() = runBlocking {
    assertThat(repository.cacheIsEmpty()).isTrue()
    assertThat(repository.getRemote().result()).isInstanceOf(Unit::class.java)
    repository.allGenres().paged().observeOnce { list ->
      assertThat(list).hasSize(1200)
      assertThat(list.first().genre).isEqualTo("Metal 0")
    }
  }

  private val testModule = module {
    singleBy<GenreRepository, GenreRepositoryImpl>()

    val mockApi = mockk<ApiBase>()

    coEvery { mockApi.getAllPages(Protocol.LibraryBrowseGenres, GenreDto::class, any()) } answers {
      TestData.mockApi(1200) {
        TestDataFactories.genre(it)
      }
    }
    single { mockApi }
    single { genreDao }
  }
}
