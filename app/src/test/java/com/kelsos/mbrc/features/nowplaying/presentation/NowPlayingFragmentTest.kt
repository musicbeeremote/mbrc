package com.kelsos.mbrc.features.nowplaying.presentation

import android.widget.EditText
import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.ui.minicontrol.MiniControlViewModel
import com.kelsos.mbrc.utils.MockFactory
import com.kelsos.mbrc.utils.SingleFragmentActivity
import com.kelsos.mbrc.utils.TestDataFactories
import com.kelsos.mbrc.utils.isGone
import com.kelsos.mbrc.utils.isVisible
import com.kelsos.mbrc.utils.swipeToRemove
import com.kelsos.mbrc.utils.testDispatcher
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
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
    val data = MockFactory<NowPlaying>().flow()
    every { viewModel.list } answers { data }
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

  @Test
  fun `after loading displays now playing`() {
    val data = MockFactory(
      TestDataFactories.nowPlayingListEntities(10)
    ).flow()
    every { viewModel.list } answers { data }
    every { viewModel.emitter } answers { emptyFlow() }

    launchFragmentInContainer<NowPlayingFragment>()

    NowPlayingRobot()
      .done()
      .listVisible()
      .textVisible("Test title 6")
  }

  @Test
  fun `clicking on a track should play it`() {
    val data = MockFactory(
      TestDataFactories.nowPlayingListEntities(10)
    ).flow()
    every { viewModel.list } answers { data }
    every { viewModel.emitter } answers { emptyFlow() }
    every { viewModel.play(any()) } just Runs

    launchFragmentInContainer<NowPlayingFragment>()

    NowPlayingRobot()
      .press(5)
      .done()
      .listVisible()

    verify(exactly = 1) { viewModel.play(5) }
  }

  @Test
  fun `swiping right on a track should remove it`() {
    val data = MockFactory(
      TestDataFactories.nowPlayingListEntities(10)
    ).flow()
    every { viewModel.list } answers { data }
    every { viewModel.emitter } answers { emptyFlow() }
    every { viewModel.removeTrack(any()) } just Runs

    launchFragmentInContainer<NowPlayingFragment>()

    NowPlayingRobot()
      .swipe(5)
      .done()
      .listVisible()

    testDispatcher.advanceUntilIdle()

    verify(exactly = 1) { viewModel.removeTrack(5) }
  }

  @Test
  fun `show a queue success message when queue succeeds`() {
    val events = flow { emit(NowPlayingUiMessages.RefreshSuccess) }

    every { viewModel.list } answers { emptyFlow() }
    every { viewModel.emitter } answers { events }

    launchFragmentInContainer<NowPlayingFragment>()

    NowPlayingRobot()
      .done()
      .messageDisplayed(R.string.now_playing__refresh_success)
  }

  @Test
  fun `show a fail message when refresh fails`() {
    val events = flow { emit(NowPlayingUiMessages.RefreshFailed) }

    every { viewModel.list } answers { emptyFlow() }
    every { viewModel.emitter } answers { events }

    launchFragmentInContainer<NowPlayingFragment>()

    NowPlayingRobot()
      .done()
      .messageDisplayed(R.string.now_playing__refresh_failed)
  }

  @Test
  fun `searching on the toolbar should pass to the viewmodel`() {
    val scenario = ActivityScenario.launch(SingleFragmentActivity::class.java)
    every { viewModel.search(any()) } just Runs
    every { viewModel.list } answers { emptyFlow() }
    every { viewModel.emitter } answers { emptyFlow() }

    scenario.onActivity {
      it.setFragment(NowPlayingFragment())
    }

    onView(withId(R.id.now_playing_search)).perform(click())
    onView(isAssignableFrom(EditText::class.java))
      .perform(typeText("track"), pressImeActionButton())

    verify(exactly = 1) { viewModel.search("track") }
  }
}

class NowPlayingRobot {
  fun done(): NowPlayingRobotResult {
    return NowPlayingRobotResult()
  }

  fun press(position: Int): NowPlayingRobot {
    onView(withId(R.id.now_playing__track_list))
      .perform(actionOnItemAtPosition<NowPlayingTrackViewHolder>(position, click()))
    return this
  }

  fun swipe(position: Int): NowPlayingRobot {
    onView(withId(R.id.now_playing__track_list))
      .perform(actionOnItemAtPosition<NowPlayingTrackViewHolder>(position, swipeToRemove()))
    return this
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

  fun listVisible(): NowPlayingRobotResult {
    list.isVisible()
    return this
  }

  fun textVisible(text: String): NowPlayingRobotResult {
    onView(withText(text)).isVisible()
    return this
  }

  fun messageDisplayed(@StringRes resId: Int) {
    onView(withId(com.google.android.material.R.id.snackbar_text))
      .check(matches(withText(resId)))
  }
}
