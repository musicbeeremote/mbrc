package com.kelsos.mbrc.repository

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.BuildConfig
import com.kelsos.mbrc.TestApplication
import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.data.library.Genre
import com.kelsos.mbrc.rules.DBFlowTestRule
import com.kelsos.mbrc.services.LibraryService
import com.raizlabs.android.dbflow.list.FlowCursorList
import org.junit.After
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
import rx.Observable
import rx.observers.TestSubscriber
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import toothpick.testing.ToothPickRule
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
    application = TestApplication::class,
    sdk = intArrayOf(Build.VERSION_CODES.N_MR1))
class GenreRepositoryImplTest {
  private val toothPickRule = ToothPickRule(this)
  private lateinit var scope: Scope

  @Rule
  fun chain(): TestRule = RuleChain.outerRule(toothPickRule).around(DBFlowTestRule.create())

  @Before
  fun setUp() {
    scope = Toothpick.openScope(TEST_SCOPE)
    scope.installModules(TestModule())
  }

  @After
  @Throws(Exception::class)
  fun tearDown() {
    Toothpick.closeScope(scope)
    Toothpick.reset()
  }

  @Test
  fun getAndSaveRemote() {
    val repository = scope.getInstance(GenreRepository::class.java)
    val testSubscriber = TestSubscriber<FlowCursorList<Genre>>()
    repository.getAndSaveRemote().subscribe(testSubscriber)

    testSubscriber.awaitTerminalEvent(2, TimeUnit.SECONDS)
    testSubscriber.assertCompleted()
    testSubscriber.assertNoErrors()
    testSubscriber.assertValueCount(1)
    val cursorList = testSubscriber.onNextEvents[0]
    assertThat(cursorList.count).isEqualTo(1200)
    assertThat(cursorList.getItem(0).genre).isEqualTo("Metal0")
  }

  private inner class TestModule : Module() {
    init {
      val mockService = mock(LibraryService::class.java)
      `when`(mockService.getGenres(anyInt(), anyInt()))
          .thenAnswer {
            val offset = it.arguments[0] as Int
            var limit = it.arguments[1] as Int

            val totalElements = 1200
            if (offset > totalElements) {
              val page = Page<Genre>()
              page.data = emptyList()
              page.total = totalElements
              page.offset = offset
              page.limit = limit
              return@thenAnswer Observable.just(page)
            } else {
              if (offset + limit > totalElements) {
                limit = totalElements - offset
              }
              return@thenAnswer Observable.range(offset, limit)
                  .map { Genre("Metal$it", it) }
                  .toList()
                  .map {
                    val page = Page<Genre>()
                    page.data = it
                    page.total = totalElements
                    page.offset = offset
                    page.limit = limit
                    return@map page
                  }
            }
          }

      bind(LibraryService::class.java).toInstance(mockService)
      bind(GenreRepository::class.java).to(GenreRepositoryImpl::class.java)
    }
  }

  companion object {
    val TEST_SCOPE: Class<*> = TestCase::class.java
  }

  @javax.inject.Scope
  @Target(AnnotationTarget.TYPE)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class TestCase

}
