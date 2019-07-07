package com.kelsos.mbrc.features.minicontrol

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.state.BaseState
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.activestatus.PlayingPosition
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionState
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.utils.Click
import com.kelsos.mbrc.utils.isVisible
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
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

  private val positionState: TrackPositionState =
    object : BaseState<PlayingPosition>(), TrackPositionState {
      override fun setPlaying(playing: Boolean) {
        error("not implemented")
      }
    }
  private val trackState: PlayingTrackState =
    object : BaseState<PlayingTrack>(), PlayingTrackState {}
  private val playerStatus: PlayerStatusState =
    object : BaseState<PlayerStatusModel>(), PlayerStatusState {}

  @Before
  fun setUp() {
    viewModel = mockk()
    trackState.set(PlayingTrack())

    every { viewModel.trackPosition } answers { positionState }
    every { viewModel.playingTrack } answers { trackState }
    every { viewModel.playerStatus } answers { playerStatus }

    startKoin {
      modules(
        listOf(
          module {
            single { viewModel }
          }
        )
      )
    }
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun `pressing next should play the next track`() {
    launchFragmentInContainer<MiniControlFragment>()
    every { viewModel.next() } just Runs
    onView(withId(R.id.mini_control__play_next)).perform(Click)
    verify(exactly = 1) { viewModel.next() }
  }

  @Test
  fun `pressing previous should play the previous track`() {
    launchFragmentInContainer<MiniControlFragment>()
    every { viewModel.previous() } just Runs
    onView(withId(R.id.mini_control__play_previous)).perform(Click)
    verify(exactly = 1) { viewModel.previous() }
  }

  @Test
  fun `pressing play pause should play or pause the track playback`() {
    launchFragmentInContainer<MiniControlFragment>()
    every { viewModel.playPause() } just Runs
    onView(withId(R.id.mini_control__play_pause)).perform(Click)
    verify(exactly = 1) { viewModel.playPause() }
  }

  @Test
  fun `track state changes are reflected to view`() {
    trackState.set(
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
