package com.kelsos.mbrc.features.nowplaying.presentation

import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.ui.minicontrol.MiniControlViewModel
import com.kelsos.mbrc.utils.MockFactory
import com.kelsos.mbrc.utils.isGone
import com.kelsos.mbrc.utils.isVisible
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.experimental.builder.single

@RunWith(AndroidJUnit4::class)
class NowPlayingFragmentTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var viewModel: NowPlayingViewModel
  private lateinit var state: PlayingTrackState

  @Before
  fun setUp() {
    viewModel = mockk()
    state = mockk()
    every { viewModel.trackState } answers { state }
    every { state.observe(any(), any()) } just Runs
    val testModule = module {
      single<NowPlayingAdapter>()
      single { viewModel }
      single<MiniControlViewModel> { mockk(relaxed = true) }
    }

    startKoin {
      modules(
        listOf(
          testModule
        )
      )
    }
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun `when no data shows empty view with message`() {
    val liveData = MockFactory<NowPlaying>().flow()
    every { viewModel.list } answers { liveData }
    every { viewModel.emitter } answers { emptyFlow() }
    launchFragmentInContainer<NowPlayingFragment>()

    NowPlayingRobot()
      .done()
      .emptyGroupVisible()
      .loadingGone()
      .emptyText(R.string.now_playing_list_empty)
  }

  @Test
  fun `initially shows loading`() {
    every { viewModel.list } answers { emptyFlow() }
    every { viewModel.emitter } answers { emptyFlow() }
    launchFragmentInContainer<NowPlayingFragment>()

    NowPlayingRobot()
      .done()
      .emptyGroupGone()
      .loadingVisible()
  }
}

class NowPlayingRobot {
  fun done(): NowPlayingRobotResult {
    return NowPlayingRobotResult()
  }
}

class NowPlayingRobotResult {
  private val emptyGroup = onView(withId(R.id.now_playing__empty_group))
  private val list = onView(withId(R.id.now_playing__track_list))
  private val loading = onView(withId(R.id.now_playing__loading_bar))
  fun emptyGroupVisible(): NowPlayingRobotResult {
    emptyGroup.isVisible()
    return this
  }

  fun loadingGone(): NowPlayingRobotResult {
    loading.isGone()
    return this
  }

  fun emptyText(@StringRes resId: Int): NowPlayingRobotResult {
    onView(withId(R.id.now_playing__text_title)).check(matches(withText(resId)))
    return this
  }

  fun emptyGroupGone(): NowPlayingRobotResult {
    emptyGroup.isGone()
    return this
  }

  fun loadingVisible(): NowPlayingRobotResult {
    loading.isVisible()
    return this
  }
}
