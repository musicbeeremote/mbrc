package com.kelsos.mbrc.features.radio.presentation

import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario.launchInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.R
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.features.minicontrol.MiniControlFactory
import com.kelsos.mbrc.features.radio.domain.RadioStation
import com.kelsos.mbrc.utilities.paged
import com.kelsos.mbrc.utils.Matchers
import com.kelsos.mbrc.utils.MockFactory
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
import org.robolectric.annotation.LooperMode.Mode.PAUSED
import org.robolectric.annotation.TextLayoutMode
import org.robolectric.annotation.TextLayoutMode.Mode.REALISTIC

@RunWith(AndroidJUnit4::class)
@TextLayoutMode(REALISTIC)
@LooperMode(PAUSED)
class RadioFragmentTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var viewModel: RadioViewModel

  @Before
  fun setUp() {
    viewModel = mockk()
    val miniControlFactory: MiniControlFactory = mockk()
    every { miniControlFactory.attach(any()) } just Runs
    startKoin {
      modules(listOf(module {
        single<RadioAdapter>()
        single { viewModel }
        single { miniControlFactory }
      }))
    }
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun `when no data shows empty view with message`() {
    val liveData = MockFactory<RadioStation>(emptyList()).paged()
    every { viewModel.radios } answers { liveData }
    every { viewModel.emitter } answers { MutableLiveData() }
    val scenario = launchInContainer(RadioFragment::class.java)

    RadioRobot()
      .done()
      .emptyGroupVisible()
      .loadingGone()
      .emptyText(R.string.radio__no_radio_stations)

    scenario.moveToState(Lifecycle.State.DESTROYED)
  }

  @Test
  fun `initially shows loading`() {
    every { viewModel.radios } answers { MutableLiveData() }
    every { viewModel.emitter } answers { MutableLiveData() }
    launchInContainer(RadioFragment::class.java)

    RadioRobot()
      .done()
      .emptyGroupGone()
      .loadingVisible()
  }

  @Test
  fun `after loading displays stations`() {
    val station = RadioStation(
      name = "Test",
      url = "http://test.radio",
      id = 1
    )
    val liveData = MockFactory(
      listOf(
        station
      )
    ).paged()
    every { viewModel.radios } answers { liveData }
    every { viewModel.emitter } answers { MutableLiveData() }
    launchInContainer(RadioFragment::class.java)

    RadioRobot()
      .done()
      .listVisible()
      .textVisible("Test")
  }

  @Test
  fun `click on station should play the station`() {
    val station = RadioStation(
      name = "Test",
      url = "http://test.radio",
      id = 1
    )
    val liveData = MockFactory(
      listOf(
        station
      )
    ).paged()
    every { viewModel.radios } answers { liveData }
    every { viewModel.emitter } answers { MutableLiveData() }
    every { viewModel.play(any()) } just Runs
    launchInContainer(RadioFragment::class.java)

    RadioRobot()
      .clickText("Test")
      .done()
      .listVisible()
      .textVisible("Test")

    verify(exactly = 1) { viewModel.play("http://test.radio") }
  }

  @Test
  fun `on swipe down enter refreshing mode`() {
    val station = RadioStation(
      name = "Test",
      url = "http://test.radio",
      id = 1
    )
    val liveData = MockFactory(
      listOf(
        station
      )
    ).paged()
    every { viewModel.radios } answers { liveData }
    every { viewModel.emitter } answers { MutableLiveData() }
    every { viewModel.play(any()) } just Runs
    launchInContainer(RadioFragment::class.java)

    RadioRobot()
      .swipe()
      .done()
      .listVisible()
      .textVisible("Test")
      .isRefreshing()
  }

  @Test
  fun `show a network error message when queue fails`() {
    val events = MutableLiveData<Event<RadioUiMessages>>()
    every { viewModel.radios } answers { MutableLiveData() }
    every { viewModel.emitter } answers { events }
    events.postValue(Event(RadioUiMessages.NetworkError))
    launchInContainer(RadioFragment::class.java)
    RadioRobot()
      .done()
      .messageDisplayed(R.string.radio__queue_network_error)
  }

  @Test
  fun `show a queue error message when queue fails`() {
    val events = MutableLiveData<Event<RadioUiMessages>>()
    every { viewModel.radios } answers { MutableLiveData() }
    every { viewModel.emitter } answers { events }
    events.postValue(Event(RadioUiMessages.QueueFailed))
    launchInContainer(RadioFragment::class.java)
    RadioRobot()
      .done()
      .messageDisplayed(R.string.radio__queue_failed)
  }

  @Test
  fun `show a queue success message when queue succeeds`() {
    val events = MutableLiveData<Event<RadioUiMessages>>()
    every { viewModel.radios } answers { MutableLiveData() }
    every { viewModel.emitter } answers { events }
    events.postValue(Event(RadioUiMessages.QueueSuccess))
    launchInContainer(RadioFragment::class.java)
    RadioRobot()
      .done()
      .messageDisplayed(R.string.radio__queue_success)
  }

  @Test
  fun `show a refresh failed message when refresh fails`() {
    val events = MutableLiveData<Event<RadioUiMessages>>()
    every { viewModel.radios } answers { MutableLiveData() }
    every { viewModel.emitter } answers { events }
    events.postValue(Event(RadioUiMessages.RefreshFailed))
    launchInContainer(RadioFragment::class.java)
    RadioRobot()
      .done()
      .messageDisplayed(R.string.radio__refresh_failed)
  }

  @Test
  fun `show a refresh success message when refresh success`() {
    val events = MutableLiveData<Event<RadioUiMessages>>()
    every { viewModel.radios } answers { MutableLiveData() }
    every { viewModel.emitter } answers { events }
    events.postValue(Event(RadioUiMessages.RefreshSuccess))
    launchInContainer(RadioFragment::class.java)
    RadioRobot()
      .done()
      .messageDisplayed(R.string.radio__refresh_success)
  }
}

class RadioRobot {
  fun done(): RadioResultRobot = RadioResultRobot()
  fun clickText(text: String): RadioRobot {
    onView(withText(text)).perform(click())
    return this
  }

  fun swipe(): RadioRobot {
    onView(withId(R.id.radio_stations__stations_list)).perform(swipeDown())
    return this
  }
}

class RadioResultRobot {
  private val emptyGroup = onView(withId(R.id.radio_stations__empty_group))
  private val stationsList = onView(withId(R.id.radio_stations__stations_list))
  private val loading = onView(withId(R.id.radio_stations__loading_bar))

  private fun ViewInteraction.isVisible() {
    check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
  }

  fun emptyGroupVisible(): RadioResultRobot {
    emptyGroup.isVisible()
    return this
  }

  fun emptyGroupGone(): RadioResultRobot {
    emptyGroup
      .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    return this
  }

  fun listVisible(): RadioResultRobot {
    stationsList.isVisible()
    return this
  }

  fun loadingVisible(): RadioResultRobot {
    loading.isVisible()
    return this
  }

  fun loadingGone(): RadioResultRobot {
    loading
      .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    return this
  }

  fun emptyText(@StringRes resId: Int): RadioResultRobot {
    onView(withId(R.id.radio_stations__text_title)).check(matches(withText(resId)))
    return this
  }

  fun textVisible(text: String): RadioResultRobot {
    onView(withText(text)).isVisible()
    return this
  }

  fun isRefreshing(): RadioResultRobot {
    onView(withId(R.id.radio_stations__refresh_layout))
      .check(matches(Matchers.isRefreshing()))
    return this
  }

  fun messageDisplayed(@StringRes resId: Int): RadioResultRobot {
    onView(withId(com.google.android.material.R.id.snackbar_text))
      .check(matches(withText(resId)))
    return this
  }
}