package com.kelsos.mbrc.features.radio.presentation

import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario.launchInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.R
import com.kelsos.mbrc.TestApplication
import com.kelsos.mbrc.features.minicontrol.MiniControlFactory
import com.kelsos.mbrc.features.radio.domain.RadioStation
import com.kelsos.mbrc.utilities.paged
import com.kelsos.mbrc.utils.MockFactory
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.experimental.builder.create
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class RadioFragmentTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var viewModel: RadioViewModel

  @Before
  fun setUp() {
    viewModel = mockk()
    val miniControlFactory: MiniControlFactory = mockk()
    every { miniControlFactory.attach(any()) } just Runs
    startKoin(listOf(module {
      single { create<RadioAdapter>() }
      single { viewModel }
      single { miniControlFactory }
    }))
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
    launchInContainer(RadioFragment::class.java)

    RadioRobot()
      .done()
      .emptyGroupVisible()
      .loadingGone()
      .emptyText(R.string.radio__no_radio_stations)
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
}

class RadioRobot {
  fun done(): RadioResultRobot = RadioResultRobot()
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
}