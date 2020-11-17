package com.kelsos.mbrc.features.nowplaying.presentation

import androidx.test.ext.junit.runners.AndroidJUnit4
import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.features.nowplaying.domain.MoveManagerImpl
import com.kelsos.mbrc.features.nowplaying.repository.NowPlayingRepository
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.NowPlayingMoveRequest
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utils.MockFactory
import com.kelsos.mbrc.utils.TestDispatchers
import com.kelsos.mbrc.utils.idle
import com.kelsos.mbrc.utils.observeOnce
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.SocketTimeoutException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class NowPlayingViewModelTest {

  private lateinit var userActionUseCase: UserActionUseCase
  private lateinit var observer: (Event<NowPlayingUiMessages>) -> Unit
  private lateinit var viewModel: NowPlayingViewModel
  private lateinit var repository: NowPlayingRepository
  private lateinit var slot: CapturingSlot<Event<NowPlayingUiMessages>>
  private lateinit var state: PlayingTrackState

  @Before
  fun setUp() {
    userActionUseCase = mockk()
    repository = mockk()
    observer = mockk()
    state = mockk()
    slot = slot()
    every { observer(capture(slot)) } just Runs
    every { repository.getAll() } answers { MockFactory(emptyList()) }
    viewModel = NowPlayingViewModel(
      dispatchers = TestDispatchers.dispatchers,
      repository = repository,
      userActionUseCase = userActionUseCase,
      moveManager = MoveManagerImpl(),
      trackState = state
    )
  }

  @Test
  fun `should notify the observer that refresh failed`() {
    coEvery { repository.getRemote(any()) } coAnswers { Either.left(SocketTimeoutException()) }
    viewModel.emitter.observeOnce(observer)
    viewModel.reload()
    idle()
    verify(exactly = 1) { observer(any()) }
    assertThat(slot.captured.peekContent()).isEqualTo(NowPlayingUiMessages.RefreshFailed)
  }

  @Test
  fun `should notify the observer that refresh succeeded`() {
    coEvery { repository.getRemote(any()) } coAnswers { Either.right(Unit) }
    viewModel.emitter.observeOnce(observer)
    viewModel.reload()
    idle()
    verify(exactly = 1) { observer(any()) }
    assertThat(slot.captured.peekContent()).isEqualTo(NowPlayingUiMessages.RefreshSuccess)
  }

  @Test
  fun `move should commit the cached move instructions`() {
    val actionSlot = slot<UserAction>()
    every { userActionUseCase.perform(capture(actionSlot)) } just Runs
    viewModel.moveTrack(0, 1)
    viewModel.moveTrack(1, 2)
    viewModel.moveTrack(2, 3)
    viewModel.move()

    assertThat(actionSlot.captured.context).isEqualTo(Protocol.NowPlayingListMove)
    assertThat(actionSlot.captured.data).isEqualTo(NowPlayingMoveRequest(0, 3))
  }

  @Test
  fun `search should play perform user action`() {
    val actionSlot = slot<UserAction>()
    every { userActionUseCase.perform(capture(actionSlot)) } just Runs
    coEvery { repository.findPosition(any()) } answers { 5 }
    viewModel.search("search")

    assertThat(actionSlot.captured.context).isEqualTo(Protocol.NowPlayingListPlay)
    assertThat(actionSlot.captured.data).isEqualTo(6)
  }

  @Test
  fun `remove should perform user action`() = runBlockingTest {
    val actionSlot = slot<UserAction>()
    every { userActionUseCase.perform(capture(actionSlot)) } just Runs
    viewModel.removeTrack(1)
    assertThat(actionSlot.captured.context).isEqualTo(Protocol.NowPlayingListRemove)
    assertThat(actionSlot.captured.data).isEqualTo(1)
  }

  @Test
  fun `play should perform user action`() {
    val actionSlot = slot<UserAction>()
    every { userActionUseCase.perform(capture(actionSlot)) } just Runs
    viewModel.play(2)

    assertThat(actionSlot.captured.context).isEqualTo(Protocol.NowPlayingListPlay)
    assertThat(actionSlot.captured.data).isEqualTo(3)
  }
}