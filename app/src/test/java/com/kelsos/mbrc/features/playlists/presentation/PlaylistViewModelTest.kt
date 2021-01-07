package com.kelsos.mbrc.features.playlists.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.features.playlists.domain.Playlist
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepository
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.SocketTimeoutException

@RunWith(AndroidJUnit4::class)
class PlaylistViewModelTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var userActionUseCase: UserActionUseCase
  private lateinit var repository: PlaylistRepository
  private lateinit var observer: (Event<PlaylistUiMessages>) -> Unit
  private lateinit var slot: CapturingSlot<Event<PlaylistUiMessages>>
  private lateinit var viewModel: PlaylistViewModel

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    userActionUseCase = mockk()
    repository = mockk()
    observer = mockk()
    slot = slot()
    every { observer(capture(slot)) } just Runs
    every { repository.getAll() } answers { MockFactory<Playlist>().flow() }
    viewModel = PlaylistViewModel(
      dispatchers = appCoroutineDispatchers,
      repository = repository,
      userActionUseCase = userActionUseCase
    )
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    testDispatcher.cleanupTestCoroutines()
  }

  @Test
  fun `should notify the observer that refresh failed`() = runBlockingTest(testDispatcher) {
    coEvery { repository.getRemote(any()) } coAnswers { SocketTimeoutException().left() }
    viewModel.emitter.test {
      viewModel.reload()
      advanceUntilIdle()
      assertThat(expectItem()).isEqualTo(PlaylistUiMessages.RefreshFailed)
    }
  }

  @Test
  fun `should notify the observer that refresh succeeded`() = runBlockingTest(testDispatcher) {
    coEvery { repository.getRemote(any()) } coAnswers { Unit.right() }
    viewModel.emitter.test {
      viewModel.reload()
      advanceUntilIdle()
      assertThat(expectItem()).isEqualTo(PlaylistUiMessages.RefreshSuccess)
    }
  }

  @Test
  fun `should send a play action`() = runBlockingTest(testDispatcher) {
    val userAction = slot<UserAction>()
    every { userActionUseCase.perform(capture(userAction)) } just Runs
    viewModel.play("""C:\playlists\metal.m3u""")
    advanceUntilIdle()
    assertThat(userAction.captured.protocol).isEqualTo(Protocol.PlaylistPlay)
    assertThat(userAction.captured.data).isEqualTo("""C:\playlists\metal.m3u""")
  }
}
