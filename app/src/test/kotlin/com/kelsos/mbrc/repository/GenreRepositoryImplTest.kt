package com.kelsos.mbrc.repository

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.BuildConfig
import com.kelsos.mbrc.TestApplication
import com.kelsos.mbrc.content.library.LibraryService
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.genres.GenreRepositoryImpl
import com.kelsos.mbrc.extensions.fail
import com.kelsos.mbrc.networking.protocol.Page
import com.kelsos.mbrc.utilities.paged
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import toothpick.config.Module
import toothpick.testing.ToothPickRule
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
    application = TestApplication::class,
    sdk = [(Build.VERSION_CODES.N_MR1)]
)
class GenreRepositoryImplTest {
  private val toothPickRule = ToothPickRule(this, "test")

  @Rule
  fun chain(): TestRule = RuleChain.outerRule(toothPickRule)

  private lateinit var repository: GenreRepository

  @Before
  fun setUp() {
    toothPickRule.scope.installModules(TestModule())
    repository = toothPickRule.getInstance(GenreRepository::class.java)
  }

  @Test
  fun getAndSaveRemote() {

    val isEmptySubscriber = repository.cacheIsEmpty().test()
    isEmptySubscriber.awaitTerminalEvent()
    isEmptySubscriber.assertComplete()
    isEmptySubscriber.assertNoErrors()
    isEmptySubscriber.assertValueCount(1)
    isEmptySubscriber.assertValue(true)

    val testSubscriber = repository.getAndSaveRemote().test()

    testSubscriber.awaitTerminalEvent(2, TimeUnit.SECONDS)
    testSubscriber.assertComplete()
    testSubscriber.assertNoErrors()
    testSubscriber.assertValueCount(1)
    val factory = testSubscriber.values()[0]
    val data = factory.paged()
    val list = data.value ?: fail("data was null")
    assertThat(list.size).isEqualTo(1200)
    assertThat(list.first()).isEqualTo("Metal0")
  }

  private inner class TestModule : Module() {
    init {
      bind(GenreRepository::class.java).to(GenreRepositoryImpl::class.java)
      val mockService = mock(LibraryService::class.java)
      `when`(mockService.getGenres(anyInt(), anyInt()))
          .thenAnswer {
            val offset = it.arguments[0] as Int
            var limit = it.arguments[1] as Int

            val totalElements = 1200
            if (offset > totalElements) {
              return@thenAnswer Observable.just(createPage(totalElements, offset, limit))
            } else {
              if (offset + limit > totalElements) {
                limit = totalElements - offset
              }
              return@thenAnswer Observable.range(offset, limit)
                  .map { GenreEntity("Metal$it") }
                  .toList()
                  .map {
                    return@map createPage(totalElements, offset, limit, it)
                  }.toObservable()
            }
          }

      bind(LibraryService::class.java).toInstance(mockService)
    }
  }

  private fun createPage(
      totalElements: Int,
      offset: Int,
      limit: Int,
      data: List<GenreEntity> = emptyList()
  ): Page<GenreEntity> {
    val page = Page<GenreEntity>()
    page.data = data
    page.total = totalElements
    page.offset = offset
    page.limit = limit
    return page
  }

}
