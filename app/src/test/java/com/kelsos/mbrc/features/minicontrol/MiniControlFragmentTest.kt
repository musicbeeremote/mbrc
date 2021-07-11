package com.kelsos.mbrc.features.minicontrol

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utils.Click
import com.kelsos.mbrc.utils.isVisible
import com.kelsos.mbrc.utils.testDispatcher
import io.mockk.Runs
import io.mockk.coVerify
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
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class MiniControlFragmentTest {
  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var viewModel: MiniControlViewModel
  private lateinit var appState: AppState
  private lateinit var userActionUseCase: UserActionUseCase

  @Before
  fun setUp() {
    userActionUseCase = mockk()
    appState = AppState()
    viewModel = MiniControlViewModel(appState, userActionUseCase)

    startKoin {
      modules(listOf(module { single { viewModel } }))
    }
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun `pressing next should play the next track`() {
    val capturingSlot = slot<UserAction>()
    launchFragmentInContainer<MiniControlFragment>()
    every { viewModel.next() } just Runs
    onView(withId(R.id.mini_control__play_next)).perform(Click)
    coVerify(exactly = 1) { userActionUseCase.perform(capture(capturingSlot)) }
    assertThat(capturingSlot.captured.protocol).isEqualTo(Protocol.PlayerNext)
  }

  @Test
  fun `pressing previous should play the previous track`() {
    val capturingSlot = slot<UserAction>()
    launchFragmentInContainer<MiniControlFragment>()
    every { viewModel.previous() } just Runs
    onView(withId(R.id.mini_control__play_previous)).perform(Click)
    coVerify(exactly = 1) { userActionUseCase.perform(capture(capturingSlot)) }
    assertThat(capturingSlot.captured.protocol).isEqualTo(Protocol.PlayerPrevious)
  }

  @Test
  fun `pressing play pause should play or pause the track playback`() {
    val capturingSlot = slot<UserAction>()
    launchFragmentInContainer<MiniControlFragment>()
    every { viewModel.playPause() } just Runs
    onView(withId(R.id.mini_control__play_pause)).perform(Click)
    coVerify(exactly = 1) { userActionUseCase.perform(capture(capturingSlot)) }
    assertThat(capturingSlot.captured.protocol).isEqualTo(Protocol.PlayerPlayPause)
  }

  @Test
  fun `track state changes are reflected to view`() = runBlockingTest(testDispatcher) {
    appState.playingTrack.emit(
      PlayingTrack(
        artist = "Test Artist",
        title = "Test Title",
        album = "Test Album"
      )
    )
    launchFragmentInContainer<MiniControlFragment>()

    onView(withText("Test Title")).isVisible()
    onView(withText("Test Artist - Test Album")).isVisible()
  }
}
