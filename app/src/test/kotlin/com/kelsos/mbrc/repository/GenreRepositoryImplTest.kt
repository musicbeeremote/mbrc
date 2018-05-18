package com.kelsos.mbrc.repository

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.BuildConfig
import com.kelsos.mbrc.TestApplication
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.genres.GenreRepositoryImpl
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.paged
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import toothpick.config.Module
import toothpick.testing.ToothPickRule
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(
  constants = BuildConfig::class,
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
    val list = data.value ?: error("data was null")
    assertThat(list.size).isEqualTo(1200)
    assertThat(list.first()).isEqualTo("Metal0")
  }

  private inner class TestModule : Module() {
    init {
      bind(GenreRepository::class.java).to(GenreRepositoryImpl::class.java)
      val mockApi = mock(ApiBase::class.java)

      given(mockApi.getAllPages(Protocol.LibraryBrowseGenres, GenreEntity::class))
        .willAnswer {
          Observable.range(0, 1200)
            .map { GenreEntity("Metal$it") }
            .toList()
        }

      bind(ApiBase::class.java).toInstance(mockApi)
    }
  }
}