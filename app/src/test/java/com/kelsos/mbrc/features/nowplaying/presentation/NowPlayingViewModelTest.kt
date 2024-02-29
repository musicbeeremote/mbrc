package com.kelsos.mbrc.features.nowplaying.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.features.nowplaying.domain.MoveManagerImpl
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.features.nowplaying.repository.NowPlayingRepository
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.NowPlayingMoveRequest
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.rules.CoroutineTestRule
import com.kelsos.mbrc.utils.MockFactory
import com.kelsos.mbrc.utils.appCoroutineDispatchers
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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
  private lateinit var appState: AppState

  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  var coroutineTestRule = CoroutineTestRule()

  @Before
  fun setUp() {
    userActionUseCase = mockk()
    repository = mockk()
    observer = mockk()
    appState = AppState()
    slot = slot()
    every { observer(capture(slot)) } just Runs
    every { repository.getAll() } answers { MockFactory<NowPlaying>().flow() }
    viewModel = NowPlayingViewModel(
      dispatchers = appCoroutineDispatchers,
      repository = repository,
      userActionUseCase = userActionUseCase,
      moveManager = MoveManagerImpl(),
      appState = appState
    )
  }

  @Test
  fun `should notify the observer that refresh failed`() = runTest {
    coEvery { repository.getRemote(any()) } coAnswers { SocketTimeoutException().left() }
    viewModel.emitter.test {
      viewModel.reload()
      advanceUntilIdle()
      assertThat(awaitItem()).isEqualTo(NowPlayingUiMessages.RefreshFailed)
    }
  }

  @Test
  fun `should notify the observer that refresh succeeded`() = runTest {
    coEvery { repository.getRemote(any()) } coAnswers { Unit.right() }
    viewModel.emitter.test {
      viewModel.reload()
      advanceUntilIdle()
      assertThat(awaitItem()).isEqualTo(NowPlayingUiMessages.RefreshSuccess)
    }
  }

  @Test
  fun `move should commit the cached move instructions`() = runTest {
    val actionSlot = slot<UserAction>()
    coEvery { userActionUseCase.perform(capture(actionSlot)) } just Runs
    viewModel.moveTrack(0, 1)
    viewModel.moveTrack(1, 2)
    viewModel.moveTrack(2, 3)
    viewModel.move()
    advanceUntilIdle()

    assertThat(actionSlot.captured.protocol).isEqualTo(Protocol.NowPlayingListMove)
    assertThat(actionSlot.captured.data).isEqualTo(NowPlayingMoveRequest(0, 3))
  }

  @Test
  fun `search should play perform user action`() = runTest {
    val actionSlot = slot<UserAction>()
    coEvery { userActionUseCase.perform(capture(actionSlot)) } just Runs
    coEvery { repository.findPosition(any()) } answers { 5 }
    viewModel.search("search")
    viewModel.move()
    advanceUntilIdle()
    assertThat(actionSlot.captured.protocol).isEqualTo(Protocol.NowPlayingListPlay)
    assertThat(actionSlot.captured.data).isEqualTo(6)
  }

  @Test
  fun `remove should perform user action`() = runTest {
    val actionSlot = slot<UserAction>()
    coEvery { userActionUseCase.perform(capture(actionSlot)) } just Runs
    viewModel.removeTrack(1)
    advanceUntilIdle()
    assertThat(actionSlot.captured.protocol).isEqualTo(Protocol.NowPlayingListRemove)
    assertThat(actionSlot.captured.data).isEqualTo(1)
  }

  @Test
  fun `play should perform user action`() = runTest {
    val actionSlot = slot<UserAction>()
    coEvery { userActionUseCase.perform(capture(actionSlot)) } just Runs
    viewModel.play(2)
    advanceUntilIdle()

    assertThat(actionSlot.captured.protocol).isEqualTo(Protocol.NowPlayingListPlay)
    assertThat(actionSlot.captured.data).isEqualTo(3)
  }
}
