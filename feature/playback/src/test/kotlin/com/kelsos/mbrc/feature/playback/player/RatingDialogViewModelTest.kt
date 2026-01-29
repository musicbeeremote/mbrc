package com.kelsos.mbrc.feature.playback.player

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.state.AppStateFlow
import com.kelsos.mbrc.core.common.state.LfmRating
import com.kelsos.mbrc.core.common.state.TrackRating
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.usecases.UserActionUseCase
import com.kelsos.mbrc.feature.settings.domain.SettingsManager
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class RatingDialogViewModelTest : KoinTest {

  private val trackRatingFlow = MutableStateFlow(TrackRating())
  private val halfStarRatingFlow = MutableStateFlow(false)

  private val testModule = module {
    single<AppStateFlow> {
      mockk(relaxed = true) {
        every { playingTrackRating } returns trackRatingFlow
      }
    }
    single<UserActionUseCase> { mockk(relaxed = true) }
    single<SettingsManager> {
      mockk(relaxed = true) {
        every { halfStarRatingFlow } returns this@RatingDialogViewModelTest.halfStarRatingFlow
      }
    }
    singleOf(::RatingDialogViewModel)
  }

  private val viewModel: RatingDialogViewModel by inject()
  private val userActionUseCase: UserActionUseCase by inject()

  @Before
  fun setUp() {
    startKoin {
      modules(listOf(testModule, testDispatcherModule))
    }
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun `initial rating should be null`() = runTest(testDispatcher) {
    viewModel.rating.test {
      assertThat(awaitItem()).isNull()
    }
  }

  @Test
  fun `rating should update when app state emits new track rating`() = runTest(testDispatcher) {
    viewModel.rating.test {
      assertThat(awaitItem()).isNull()

      trackRatingFlow.emit(TrackRating(rating = 3.5f))
      assertThat(awaitItem()).isEqualTo(3.5f)

      trackRatingFlow.emit(TrackRating(rating = 5.0f))
      assertThat(awaitItem()).isEqualTo(5.0f)
    }
  }

  @Test
  fun `rating should reflect bomb rating from app state`() = runTest(testDispatcher) {
    viewModel.rating.test {
      assertThat(awaitItem()).isNull()

      trackRatingFlow.emit(TrackRating(rating = 0f))
      assertThat(awaitItem()).isEqualTo(0f)
    }
  }

  @Test
  fun `halfStarEnabled should reflect settings manager flow`() = runTest(testDispatcher) {
    viewModel.halfStarEnabled.test {
      assertThat(awaitItem()).isFalse()

      halfStarRatingFlow.emit(true)
      assertThat(awaitItem()).isTrue()

      halfStarRatingFlow.emit(false)
      assertThat(awaitItem()).isFalse()
    }
  }

  @Test
  fun `changeRating should update local rating state`() = runTest(testDispatcher) {
    viewModel.rating.test {
      assertThat(awaitItem()).isNull()

      viewModel.changeRating(4.0f)
      assertThat(awaitItem()).isEqualTo(4.0f)
    }
  }

  @Test
  fun `changeRating should send user action with numeric rating`() = runTest(testDispatcher) {
    viewModel.changeRating(3.5f)
    advanceUntilIdle()

    coVerify {
      userActionUseCase.perform(
        match {
          it.protocol == Protocol.NowPlayingRating && it.data == 3.5f
        }
      )
    }
  }

  @Test
  fun `changeRating with null should send user action with empty string`() =
    runTest(testDispatcher) {
      viewModel.changeRating(null)
      advanceUntilIdle()

      coVerify {
        userActionUseCase.perform(
          match {
            it.protocol == Protocol.NowPlayingRating && it.data == ""
          }
        )
      }
    }

  @Test
  fun `changeRating with zero (bomb) should send user action with zero`() =
    runTest(testDispatcher) {
      viewModel.changeRating(0f)
      advanceUntilIdle()

      coVerify {
        userActionUseCase.perform(
          match {
            it.protocol == Protocol.NowPlayingRating && it.data == 0f
          }
        )
      }
    }

  @Test
  fun `rating changes should be distinct until changed`() = runTest(testDispatcher) {
    viewModel.rating.test {
      assertThat(awaitItem()).isNull()

      // Emit same rating multiple times - should only receive once
      trackRatingFlow.emit(TrackRating(rating = 3.0f))
      assertThat(awaitItem()).isEqualTo(3.0f)

      // Same rating value should not emit again
      trackRatingFlow.emit(TrackRating(rating = 3.0f, lfmRating = LfmRating.Loved))
      expectNoEvents()

      // Different rating should emit
      trackRatingFlow.emit(TrackRating(rating = 4.0f))
      assertThat(awaitItem()).isEqualTo(4.0f)
    }
  }

  @Test
  fun `changeRating with half star values should work correctly`() = runTest(testDispatcher) {
    viewModel.rating.test {
      assertThat(awaitItem()).isNull()

      viewModel.changeRating(0.5f)
      assertThat(awaitItem()).isEqualTo(0.5f)

      viewModel.changeRating(2.5f)
      assertThat(awaitItem()).isEqualTo(2.5f)

      viewModel.changeRating(4.5f)
      assertThat(awaitItem()).isEqualTo(4.5f)
    }
  }

  @Test
  fun `viewModel should request current rating on init`() = runTest(testDispatcher) {
    // Create mocks directly to verify init behavior
    val mockUserActionUseCase: UserActionUseCase = mockk(relaxed = true)
    val mockAppState: AppStateFlow = mockk(relaxed = true) {
      every { playingTrackRating } returns MutableStateFlow(TrackRating())
    }
    val mockSettingsManager: SettingsManager = mockk(relaxed = true) {
      every { halfStarRatingFlow } returns MutableStateFlow(false)
    }

    // Create ViewModel - this triggers init block
    RatingDialogViewModel(mockUserActionUseCase, mockAppState, mockSettingsManager)
    advanceUntilIdle()

    // Verify that init requested the current rating
    coVerify {
      mockUserActionUseCase.perform(
        match {
          it.protocol == Protocol.NowPlayingRating && it.data == true
        }
      )
    }
  }
}
