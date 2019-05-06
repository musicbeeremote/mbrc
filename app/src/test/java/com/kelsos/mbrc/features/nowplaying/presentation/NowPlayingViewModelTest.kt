package com.kelsos.mbrc.features.nowplaying.presentation

import androidx.test.ext.junit.runners.AndroidJUnit4
import arrow.core.Try
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.TestApplication
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.features.nowplaying.domain.MoveManagerImpl
import com.kelsos.mbrc.features.nowplaying.repository.NowPlayingRepository
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.utils.MockFactory
import com.kelsos.mbrc.utils.TestDispatchers
import com.kelsos.mbrc.utils.observeOnce
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.net.SocketTimeoutException

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
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

  @After
  fun tearDown() {
  }

  @Test
  fun `should notify the observer that refresh failed`() {
    coEvery { repository.getRemote() } coAnswers { Try.raiseError(SocketTimeoutException()) }
    viewModel.emitter.observeOnce(observer)
    viewModel.reload()
    verify(exactly = 1) { observer(any()) }
    assertThat(slot.captured.peekContent()).isEqualTo(NowPlayingUiMessages.RefreshFailed)
  }

  @Test
  fun `should notify the observer that refresh succeeded`() {
    coEvery { repository.getRemote() } coAnswers { Try.invoke { } }
    viewModel.emitter.observeOnce(observer)
    viewModel.reload()
    verify(exactly = 1) { observer(any()) }
    assertThat(slot.captured.peekContent()).isEqualTo(NowPlayingUiMessages.RefreshSuccess)
  }
}