package com.kelsos.mbrc.ui.navigation.radio

import android.app.Application
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.kelsos.mbrc.R
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.services.ServiceChecker
import com.kelsos.mbrc.ui.mini_control.MiniControlFragment
import com.kelsos.mbrc.ui.mini_control.MiniControlPresenter
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import toothpick.Toothpick
import toothpick.config.Module
import toothpick.testing.ToothPickTestModule


@RunWith(AndroidJUnit4::class)
@LargeTest
class RadioActivityTest {

  @Rule @JvmField val activityRule: IntentsTestRule<RadioActivity> = IntentsTestRule<RadioActivity>(RadioActivity::class.java, true, false)


  @Mock lateinit var presenter: RadioPresenter
  @Mock lateinit var miniControlPresenter: MiniControlPresenter
  @Mock lateinit var bus: RxBus
  @Mock lateinit var serviceChecker: ServiceChecker

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)
    val radioActivityScope = Toothpick.openScope(RadioActivity.Presenter::class.java)
    radioActivityScope.installTestModules(TestModule())
    val miniControlFragmentScope = Toothpick.openScope(MiniControlFragment.Presenter::class.java)
    miniControlFragmentScope.installTestModules(TestModule())
    val application = InstrumentationRegistry.getTargetContext().applicationContext as Application
    val scope = Toothpick.openScope(application)
    scope.installModules(ToothPickTestModule(this))

    FlowManager.init(FlowConfig.Builder(application).build())

  }

  @After
  fun tearDown() {
    Toothpick.reset()
  }

  @Test
  fun radioView_loading() {
    activityRule.launchActivity(Intent())
    activityRule.activity.showLoading()
    onView(withId(R.id.empty_view_progress_bar)).check(matches(isDisplayed()))
    verify(presenter, times(1)).load()
  }

  @Test
  fun radioView_noTrackFound() {

//    activityRule.launchActivity(Intent())
//    activityRule.activity.showLoading()
//    onView(withId(R.id.empty_view_progress_bar)).check(matches(isDisplayed()))
//    verify(presenter, times(1)).load()
//    activityRule.activity.update(FlowCursorList.Builder(RadioStation::class.java).build())
//    onView(withId(R.id.list_empty_title)).check(matches(isDisplayed()))
  }

  inner class TestModule : Module() {
    init {
      bind(RadioPresenter::class.java).toInstance(presenter)
      bind(MiniControlPresenter::class.java).toInstance(miniControlPresenter)
    }
  }

}
