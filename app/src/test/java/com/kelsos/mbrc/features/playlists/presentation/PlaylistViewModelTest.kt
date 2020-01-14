package com.kelsos.mbrc.features.playlists.presentation

import androidx.test.ext.junit.runners.AndroidJUnit4
import arrow.core.Try
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepository
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
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
import java.net.SocketTimeoutException

@RunWith(AndroidJUnit4::class)
class PlaylistViewModelTest {

  private lateinit var userActionUseCase: UserActionUseCase
  private lateinit var repository: PlaylistRepository
  private lateinit var observer: (Event<PlaylistUiMessages>) -> Unit
  private lateinit var slot: CapturingSlot<Event<PlaylistUiMessages>>
  private lateinit var viewModel: PlaylistViewModel

  @Before
  fun setUp() {
    userActionUseCase = mockk()
    repository = mockk()
    observer = mockk()
    slot = slot()
    every { observer(capture(slot)) } just Runs
    every { repository.getAll() } answers { MockFactory(emptyList()) }
    viewModel = PlaylistViewModel(
      dispatchers = TestDispatchers.dispatchers,
      repository = repository,
      userActionUseCase = userActionUseCase
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
    Truth.assertThat(slot.captured.peekContent()).isEqualTo(PlaylistUiMessages.RefreshFailed)
  }

  @Test
  fun `should notify the observer that refresh succeeded`() {
    coEvery { repository.getRemote() } coAnswers { Try.invoke { } }
    viewModel.emitter.observeOnce(observer)
    viewModel.reload()
    verify(exactly = 1) { observer(any()) }
    Truth.assertThat(slot.captured.peekContent()).isEqualTo(PlaylistUiMessages.RefreshSuccess)
  }

  @Test
  fun `should send a play action`() {
    val userAction = slot<UserAction>()
    every { userActionUseCase.perform(capture(userAction)) } just Runs
    viewModel.play("""C:\playlists\metal.m3u""")
    assertThat(userAction.captured.context).isEqualTo(Protocol.PlaylistPlay)
    assertThat(userAction.captured.data).isEqualTo("""C:\playlists\metal.m3u""")
  }
}