package com.kelsos.mbrc.ui.navigation.radio

import android.app.Application
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.swipeDown
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.kelsos.mbrc.R
import com.kelsos.mbrc.data.RadioStation
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.services.ServiceChecker
import com.kelsos.mbrc.ui.mini_control.MiniControlFragment
import com.kelsos.mbrc.ui.mini_control.MiniControlPresenter
import com.raizlabs.android.dbflow.list.FlowCursorList
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import toothpick.Toothpick
import toothpick.config.Module
import toothpick.testing.ToothPickTestModule
import java.net.SocketTimeoutException


@RunWith(AndroidJUnit4::class)
@LargeTest
class RadioActivityTest {

  @Rule @JvmField val activityRule: IntentsTestRule<RadioActivity> = IntentsTestRule<RadioActivity>(RadioActivity::class.java, true, false)


  @Mock lateinit var presenter: RadioPresenter
  @Mock lateinit var miniControlPresenter: MiniControlPresenter
  @Mock lateinit var bus: RxBus
  @Mock lateinit var serviceChecker: ServiceChecker
  @Mock lateinit var cursor: FlowCursorList<RadioStation>

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
  fun radioView_noStationsFound() {
    `when`(cursor.count).thenReturn(0)
    activityRule.launchActivity(Intent())
    verify(presenter, times(1)).load()
    Single.fromCallable { cursor }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          val activity = activityRule.activity
          activity.hideLoading()
          activity.update(it)
        }

    onView(withId(R.id.list_empty_title)).check(matches(isDisplayed()))
  }

  @Test
  fun radioView_threeStationsAvailable_playSuccess() {
    `when`(cursor.count).thenReturn(3)

    val station_1 = RadioStation()
    station_1.name = "Radio 1"
    station_1.url = "http://station_1.url"

    val station_2 = RadioStation()
    station_2.name = "Radio 2"
    station_2.url = "http://station_2.url"

    val station_3 = RadioStation()
    station_3.name = "Radio 3"
    station_3.url = "http://station_3.url"

    `when`(cursor.getItem(eq(0L))).thenReturn(station_1)
    `when`(cursor.getItem(eq(1L))).thenReturn(station_2)
    `when`(cursor.getItem(eq(2L))).thenReturn(station_3)

    activityRule.launchActivity(Intent())
    verify(presenter, times(1)).load()
    Single.fromCallable { cursor }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          val activity = activityRule.activity
          activity.hideLoading()
          activity.update(it)
        }

    onView(withText(station_3.name)).check(matches(isDisplayed()))
    onView(withText(station_3.name)).perform(click())
    verify(presenter, times(1)).play(station_3.url)

    Single.just(true)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          val activity = activityRule.activity
          activity.radioPlaySuccessful()
        }

    onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(R.string.radio__play_successful)))
        .check(matches(withEffectiveVisibility(VISIBLE)))
  }

  @Test
  fun radioView_threeStationsAvailable_playFailure() {
    `when`(cursor.count).thenReturn(3)

    val station_1 = RadioStation()
    station_1.name = "Radio 1"
    station_1.url = "http://station_1.url"

    val station_2 = RadioStation()
    station_2.name = "Radio 2"
    station_2.url = "http://station_2.url"

    val station_3 = RadioStation()
    station_3.name = "Radio 3"
    station_3.url = "http://station_3.url"

    `when`(cursor.getItem(eq(0L))).thenReturn(station_1)
    `when`(cursor.getItem(eq(1L))).thenReturn(station_2)
    `when`(cursor.getItem(eq(2L))).thenReturn(station_3)

    activityRule.launchActivity(Intent())
    verify(presenter, times(1)).load()
    Single.fromCallable { cursor }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          val activity = activityRule.activity
          activity.hideLoading()
          activity.update(it)
        }

    onView(withText(station_3.name)).check(matches(isDisplayed()))
    onView(withText(station_3.name)).perform(click())
    verify(presenter, times(1)).play(station_3.url)

    Single.just(SocketTimeoutException())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          val activity = activityRule.activity
          activity.radioPlayFailed(it)
        }
    onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(R.string.radio__play_failed)))
        .check(matches(withEffectiveVisibility(VISIBLE)))
  }


  @Test
  fun radioView_swipeToRefresh() {
    `when`(cursor.count).thenReturn(0)
    activityRule.launchActivity(Intent())
    verify(presenter, times(1)).load()
    Single.just(cursor)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          val activity = activityRule.activity
          activity.update(it)
          activity.hideLoading()
        }

    onView(withId(R.id.empty_view)).perform(swipeDown())
    verify(presenter, times(1)).refresh()
  }

  @Test
  fun radioView_swipeToRefresh_loadError() {
    `when`(cursor.count).thenReturn(0)
    activityRule.launchActivity(Intent())
    verify(presenter, times(1)).load()
    Single.just(cursor)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          val activity = activityRule.activity
          activity.update(it)
          activity.hideLoading()
        }

    onView(withId(R.id.empty_view)).perform(swipeDown())
    verify(presenter, times(1)).refresh()

    Single.just(SocketTimeoutException())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          val activity = activityRule.activity
          activity.hideLoading()
          activity.error(it)
        }

    onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(R.string.radio__loading_failed)))
        .check(matches(withEffectiveVisibility(VISIBLE)))
  }

  inner class TestModule : Module() {
    init {
      bind(RadioPresenter::class.java).toInstance(presenter)
      bind(MiniControlPresenter::class.java).toInstance(miniControlPresenter)
    }
  }

}
