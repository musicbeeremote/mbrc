package com.kelsos.mbrc.features.radio.presentation

import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.radio.domain.RadioStation
import com.kelsos.mbrc.ui.minicontrol.MiniControlViewModel
import com.kelsos.mbrc.utils.MockFactory
import io.mockk.every
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
class RadioFragmentTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var viewModel: RadioViewModel

  @Before
  fun setUp() {
    viewModel = mockk()
    val testModules = listOf(
      module {
        single<RadioAdapter>()
        single { viewModel }
        single<MiniControlViewModel> { mockk(relaxed = true) }
      }
    )
    startKoin {
      modules(testModules)
    }
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun `when no data shows empty view with message`() {
    every { viewModel.radios } answers { MockFactory<RadioStation>().flow() }
    every { viewModel.emitter } answers { emptyFlow() }
    launchFragmentInContainer<RadioFragment>()

    RadioRobot()
      .done()
      .emptyGroupVisible()
      .loadingGone()
      .emptyText(R.string.radio__no_radio_stations)
  }

  @Test
  fun `initially shows loading`() {
    every { viewModel.radios } answers { emptyFlow() }
    every { viewModel.emitter } answers { emptyFlow() }
    launchFragmentInContainer<RadioFragment>()

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

    every { viewModel.radios } answers { MockFactory(listOf(station)).flow() }
    every { viewModel.emitter } answers { emptyFlow() }
    launchFragmentInContainer<RadioFragment>()

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

  private fun ViewInteraction.isVisible() {
    check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
  }

  fun emptyGroupVisible(): RadioResultRobot {
    onView(withId(R.id.radio_stations__empty_group)).isVisible()
    return this
  }

  fun emptyGroupGone(): RadioResultRobot {
    onView(withId(R.id.radio_stations__empty_group))
      .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    return this
  }

  fun listVisible(): RadioResultRobot {
    onView(withId(R.id.radio_stations__stations_list)).isVisible()
    return this
  }

  fun loadingVisible(): RadioResultRobot {
    onView(withId(R.id.radio_stations__loading_bar)).isVisible()
    return this
  }

  fun loadingGone(): RadioResultRobot {
    onView(withId(R.id.radio_stations__loading_bar))
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
