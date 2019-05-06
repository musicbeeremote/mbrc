package com.kelsos.mbrc.features.nowplaying.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import arrow.core.Try
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.features.nowplaying.domain.MoveManagerImpl
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.features.nowplaying.repository.NowPlayingRepository
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.utils.MockFactory
import com.kelsos.mbrc.utils.appCoroutineDispatchers
import com.kelsos.mbrc.utils.testDispatcher
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.SocketTimeoutException

@RunWith(AndroidJUnit4::class)
class NowPlayingViewModelTest {

  private lateinit var userActionUseCase: UserActionUseCase
  private lateinit var observer: (Event<NowPlayingUiMessages>) -> Unit
  private lateinit var viewModel: NowPlayingViewModel
  private lateinit var repository: NowPlayingRepository
  private lateinit var slot: CapturingSlot<Event<NowPlayingUiMessages>>
  private lateinit var state: PlayingTrackState

  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  @Before
  fun setUp() {
    userActionUseCase = mockk()
    repository = mockk()
    observer = mockk()
    state = mockk()
    slot = slot()
    every { observer(capture(slot)) } just Runs
    every { repository.getAll() } answers { MockFactory<NowPlaying>().flow() }
    viewModel = NowPlayingViewModel(
      dispatchers = appCoroutineDispatchers,
      repository = repository,
      userActionUseCase = userActionUseCase,
      moveManager = MoveManagerImpl(),
      trackState = state
    )
  }

  @After
  fun tearDown() {
  }

  @Test
  fun `should notify the observer that refresh failed`() = runBlockingTest(testDispatcher) {
    coEvery { repository.getRemote() } coAnswers { Try.raiseError(SocketTimeoutException()) }
    viewModel.emitter.test {
      viewModel.reload()
      advanceUntilIdle()
      assertThat(expectItem()).isEqualTo(NowPlayingUiMessages.RefreshFailed)
    }
  }

  @Test
  fun `should notify the observer that refresh succeeded`() = runBlockingTest(testDispatcher) {
    coEvery { repository.getRemote() } coAnswers { Try.invoke { } }
    viewModel.emitter.test {
      viewModel.reload()
      advanceUntilIdle()
      assertThat(expectItem()).isEqualTo(NowPlayingUiMessages.RefreshSuccess)
    }
  }
}
