package com.kelsos.mbrc.repository

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.BuildConfig
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
import org.mockito.Mockito
import org.mockito.Mockito.anyInt
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import rx.Observable
import rx.observers.TestSubscriber
import toothpick.Toothpick
import toothpick.config.Module
import toothpick.testing.ToothPickRule

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class GenreRepositoryImplTest {
  private val toothPickRule = ToothPickRule(this)
  @Rule
  fun chain(): TestRule = RuleChain.outerRule(toothPickRule).around(DBFlowTestRule.create())

  @Before
  fun setUp() {

  }

  @After
  @Throws(Exception::class)
  fun tearDown() {
    Toothpick.reset()
  }

  @Test
  fun getAllCursor() {

  }

  @Test
  fun getAndSaveRemote() {
    val scope = Toothpick.openScope(RuntimeEnvironment.application)
    scope.installModules(TestModule())
    val repository = scope.getInstance(GenreRepository::class.java)
    val testSubscriber = TestSubscriber<FlowCursorList<Genre>>()
    repository.getAndSaveRemote().subscribe(testSubscriber)
    testSubscriber.awaitTerminalEvent()
    testSubscriber.assertCompleted()
    testSubscriber.assertNoErrors()
    testSubscriber.assertValueCount(1)
    val cursorList = testSubscriber.onNextEvents[0]
    assertThat(cursorList.count).isEqualTo(1200)
    assertThat(cursorList.getItem(0).genre).isEqualTo("Metal0")
  }



  private inner class TestModule : Module() {
    init {
      bind(LibraryService::class.java).toProviderInstance {
        val mockService = Mockito.mock(LibraryService::class.java)
        Mockito.`when`(mockService.getGenres(anyInt(), anyInt()))
            .thenAnswer {
              val offset = it.arguments[0] as Int
              val limit = it.arguments[1] as Int

              if (offset > 1200) {
                return@thenAnswer Observable.empty<Page<Genre>>()
              } else {
                return@thenAnswer Observable.range(offset, limit)
                    .map { Genre("Metal$it", it) }
                    .toList()
                    .map {
                      val page = Page<Genre>()
                      page.data = it
                      page.total = 1200
                      page.offset = offset
                      page.limit = limit
                      return@map page
                    }
              }
            }

        return@toProviderInstance mockService
      }
      bind(GenreRepository::class.java).to(GenreRepositoryImpl::class.java)
    }
  }

}
