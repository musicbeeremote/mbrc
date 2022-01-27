package com.kelsos.mbrc.features.playlists.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.features.playlists.Playlist
import com.kelsos.mbrc.features.playlists.PlaylistRepository
import com.kelsos.mbrc.features.playlists.PlaylistUiMessages
import com.kelsos.mbrc.features.playlists.PlaylistViewModel
import com.kelsos.mbrc.networking.client.UserActionUseCase
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
class PlaylistViewModelTest {
  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  var coroutineTestRule = CoroutineTestRule()

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
    every { repository.getAll() } answers { MockFactory<Playlist>().flow() }
    viewModel =
      PlaylistViewModel(
        dispatchers = appCoroutineDispatchers,
        repository = repository,
        userActionUseCase = userActionUseCase,
      )
  }

  @Test
  fun `should notify the observer that refresh failed`() =
    runTest {
      coEvery { repository.getRemote(any()) } coAnswers { SocketTimeoutException().left() }
      viewModel.emitter.test {
        viewModel.actions.reload()
        advanceUntilIdle()
        assertThat(awaitItem()).isEqualTo(PlaylistUiMessages.RefreshFailed)
      }
    }

  @Test
  fun `should notify the observer that refresh succeeded`() =
    runTest {
      coEvery { repository.getRemote(any()) } coAnswers { Unit.right() }
      viewModel.emitter.test {
        viewModel.actions.reload()
        advanceUntilIdle()
        assertThat(awaitItem()).isEqualTo(PlaylistUiMessages.RefreshSuccess)
      }
    }

  @Test
  fun `should send a play action`() =
    runTest {
      val userAction = slot<UserAction>()
      coEvery { userActionUseCase.perform(capture(userAction)) } just Runs
      viewModel.actions.play("""C:\playlists\metal.m3u""")
      advanceUntilIdle()
      assertThat(userAction.captured.protocol).isEqualTo(Protocol.PlaylistPlay)
      assertThat(userAction.captured.data).isEqualTo("""C:\playlists\metal.m3u""")
    }
}
