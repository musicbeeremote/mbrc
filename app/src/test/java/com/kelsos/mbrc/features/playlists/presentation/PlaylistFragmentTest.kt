package com.kelsos.mbrc.features.playlists.presentation

import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario.launchInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.features.minicontrol.MiniControlFactory
import com.kelsos.mbrc.features.playlists.domain.Playlist
import com.kelsos.mbrc.utils.Matchers
import com.kelsos.mbrc.utils.MockFactory
import com.kelsos.mbrc.utils.isGone
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
import org.koin.experimental.builder.single
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.TextLayoutMode

@RunWith(AndroidJUnit4::class)
@TextLayoutMode(TextLayoutMode.Mode.REALISTIC)
@LooperMode(LooperMode.Mode.PAUSED)
class PlaylistFragmentTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var viewModel: PlaylistViewModel

  private val playlist = Playlist(
    name = "Test",
    url = """c:\playlists\playlist.m3u""",
    id = 1
  )

  @Before
  fun setUp() {
    viewModel = mockk()
    val miniControlFactory: MiniControlFactory = mockk()
    every { miniControlFactory.attach(any()) } just Runs
    startKoin {
      modules(
        listOf(
          module {
            single<PlaylistAdapter>()
            single { viewModel }
            single { miniControlFactory }
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
  fun `when no data shows empty view with message`() {
    val liveData = MockFactory<Playlist>(emptyList()).paged()
    every { viewModel.playlists } answers { liveData }
    every { viewModel.emitter } answers { MutableLiveData() }
    val scenario = launchInContainer(PlaylistFragment::class.java)

    PlaylistRobot()
      .done()
      .emptyGroupVisible()
      .loadingGone()
      .emptyText(R.string.playlists_list_empty)

    scenario.moveToState(Lifecycle.State.DESTROYED)
  }

  @Test
  fun `initially shows loading`() {
    every { viewModel.playlists } answers { MutableLiveData() }
    every { viewModel.emitter } answers { MutableLiveData() }
    launchInContainer(PlaylistFragment::class.java)

    PlaylistRobot()
      .done()
      .emptyGroupGone()
      .loadingVisible()
  }

  @Test
  fun `after loading displays playlists`() {
    val liveData = MockFactory(listOf(playlist)).paged()
    every { viewModel.playlists } answers { liveData }
    every { viewModel.emitter } answers { MutableLiveData() }
    launchInContainer(PlaylistFragment::class.java)

    PlaylistRobot()
      .done()
      .listVisible()
      .textVisible("Test")
  }

  @Test
  fun `click on playlist should play the playlist`() {
    val liveData = MockFactory(listOf(playlist)).paged()
    every { viewModel.playlists } answers { liveData }
    every { viewModel.emitter } answers { MutableLiveData() }
    every { viewModel.play(any()) } just Runs
    launchInContainer(PlaylistFragment::class.java)

    PlaylistRobot()
      .clickText("Test")
      .done()
      .listVisible()
      .textVisible("Test")

    verify(exactly = 1) { viewModel.play("""c:\playlists\playlist.m3u""") }
  }

  @Test
  fun `on swipe down enter refreshing mode`() {
    val liveData = MockFactory(listOf(playlist)).paged()
    every { viewModel.playlists } answers { liveData }
    every { viewModel.emitter } answers { MutableLiveData() }
    every { viewModel.play(any()) } just Runs
    launchInContainer(PlaylistFragment::class.java)

    PlaylistRobot()
      .swipe()
      .done()
      .listVisible()
      .textVisible("Test")
      .isRefreshing()
  }

  @Test
  fun `show a refresh failed message when refresh fails`() {
    val events = MutableLiveData<Event<PlaylistUiMessages>>()
    every { viewModel.playlists } answers { MutableLiveData() }
    every { viewModel.emitter } answers { events }
    events.postValue(Event(PlaylistUiMessages.RefreshFailed))
    launchInContainer(PlaylistFragment::class.java)
    PlaylistRobot()
      .done()
      .messageDisplayed(R.string.playlists__refresh_failed)
  }

  @Test
  fun `show a refresh success message when refresh success`() {
    val events = MutableLiveData<Event<PlaylistUiMessages>>()
    every { viewModel.playlists } answers { MutableLiveData() }
    every { viewModel.emitter } answers { events }
    events.postValue(Event(PlaylistUiMessages.RefreshSuccess))
    launchInContainer(PlaylistFragment::class.java)
    PlaylistRobot()
      .done()
      .messageDisplayed(R.string.playlists__refresh_success)
  }
}

class PlaylistRobot {
  fun done(): PlaylistRobotoResult {
    return PlaylistRobotoResult()
  }

  fun clickText(text: String): PlaylistRobot {
    onView(withText(text)).perform(click())
    return this
  }

  fun swipe(): PlaylistRobot {
    onView(withId(R.id.playlists__playlist_list)).perform(ViewActions.swipeDown())
    return this
  }
}

class PlaylistRobotoResult {
  private val emptyGroup = onView(withId(R.id.playlists__empty_group))
  private val list = onView(withId(R.id.playlists__playlist_list))
  private val loading = onView(withId(R.id.playlists__loading_bar))

  fun emptyGroupVisible(): PlaylistRobotoResult {
    emptyGroup.isVisible()
    return this
  }

  fun loadingGone(): PlaylistRobotoResult {
    loading.isGone()
    return this
  }

  fun emptyText(@StringRes resId: Int): PlaylistRobotoResult {
    onView(withId(R.id.playlists__text_title)).check(matches(withText(resId)))
    return this
  }

  fun emptyGroupGone(): PlaylistRobotoResult {
    emptyGroup.isGone()
    return this
  }

  fun loadingVisible(): PlaylistRobotoResult {
    loading.isVisible()
    return this
  }

  fun listVisible(): PlaylistRobotoResult {
    list.isVisible()
    return this
  }

  fun textVisible(text: String): PlaylistRobotoResult {
    onView(withText(text)).isVisible()
    return this
  }

  fun isRefreshing(): PlaylistRobotoResult {
    onView(withId(R.id.playlists__refresh_layout))
      .check(matches(Matchers.isRefreshing()))
    return this
  }

  fun messageDisplayed(@StringRes resId: Int): PlaylistRobotoResult {
    onView(withId(com.google.android.material.R.id.snackbar_text))
      .check(matches(withText(resId)))
    return this
  }
}