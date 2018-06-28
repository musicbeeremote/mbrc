package com.kelsos.mbrc.ui.navigation.radio

import android.app.Application
import android.content.Intent
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import com.kelsos.mbrc.DbTest
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.radios.RadioRepository
import com.kelsos.mbrc.content.radios.RadioRepositoryImpl
import com.kelsos.mbrc.content.radios.RadioStation
import com.kelsos.mbrc.content.radios.RadioStationDao
import com.kelsos.mbrc.content.radios.RadioStationDto
import com.kelsos.mbrc.ui.minicontrol.MiniControlFragment
import com.kelsos.mbrc.ui.minicontrol.MiniControlPresenter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations



import java.net.SocketTimeoutException

@RunWith(AndroidJUnit4::class)
@LargeTest
class RadioFragmentTest : DbTest() {

  @Rule
  @JvmField
  val activityRule: IntentsTestRule<RadioFragment> = IntentsTestRule<RadioFragment>(
    RadioFragment::class.java,
    true,
    false
  )

  private val station1 = RadioStationDto(
    name = "Radio 1",
    url = "http://station_1.url"
  )
  private val station2 = RadioStationDto(
    name = "Radio 2",
    url = "http://station_2.url"
  )
  private val station3 = RadioStationDto(
    name = "Radio 3",
    url = "http://station_3.url"
  )

  private val list = listOf(
    station1,
    station2,
    station3
  )

  @Mock
  lateinit var presenter: RadioPresenter
  @Mock
  lateinit var miniControlPresenter: MiniControlPresenter

  private lateinit var resource: CountingIdlingResource

  @Before
  fun setUp() {

    MockitoAnnotations.initMocks(this)
    val radioActivityScope = Toothpick.openScope(RadioFragment.Presenter::class.java)
    radioActivityScope.installTestModules(TestModule(db.radioStationDao()))
    val miniControlFragmentScope = Toothpick.openScope(MiniControlFragment.Presenter::class.java)
    miniControlFragmentScope.installTestModules(TestModule(db.radioStationDao()))
    val application = InstrumentationRegistry.getTargetContext().applicationContext as Application
    val scope = Toothpick.openScope(application)
    scope.installModules(ToothPickTestModule(this))
    resource = CountingIdlingResource("idling resource")
    IdlingRegistry.getInstance().register(resource)
  }

  @After
  fun tearDown() {
    Toothpick.reset()
  }

  @Test
  fun radioScreenIsLoading() {
    activityRule.launchActivity(Intent())
    activityRule.activity.showLoading()
    onView(withId(R.id.radio_stations__loading_bar)).check(matches(isDisplayed()))
    verify(presenter, times(1)).load()
  }

  @Test
  fun radioScreenIsEmpty() {
    activityRule.launchActivity(Intent())
    mockDataLoading(activityRule.activity, resource)
    onView(withId(R.id.radio_stations__empty_icon)).check(matches(isDisplayed()))
    verify(presenter, times(1)).load()
  }

  @Test
  fun radioView_threeStationsAvailable_playSuccess() {
    activityRule.launchActivity(Intent())
    verify(presenter, times(1)).load()
    mockDataLoading(activityRule.activity, resource)
    onView(withText(station3.name)).check(matches(isDisplayed()))
    onView(withText(station3.name)).perform(click())
    verify(presenter, times(1)).play(station3.url)

    Single.just(true)
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        val activity = activityRule.activity
        activity.radioPlaySuccessful()
      }, {
        fail()
      })

    onView(allOf(
      withId(android.support.design.R.id.snackbar_text),
      withText(R.string.radio__play_successful)
    )).check(matches(withEffectiveVisibility(VISIBLE)))
  }

  @Test
  fun radioView_threeStationsAvailable_playFailure() {
    activityRule.launchActivity(Intent())
    mockDataLoading(activityRule.activity, resource)

    onView(withText(station3.name)).check(matches(isDisplayed()))
    onView(withText(station3.name)).perform(click())

    Single.just(SocketTimeoutException())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        val activity = activityRule.activity
        activity.radioPlayFailed(it)
      }, {
        fail()
      })

    onView(allOf(
      withId(android.support.design.R.id.snackbar_text),
      withText(R.string.radio__play_failed)
    ))
      .check(matches(withEffectiveVisibility(VISIBLE)))

    verify(presenter, times(1)).play(station3.url)
    verify(presenter, times(1)).load()
  }

  @Test
  fun radioView_swipeToRefresh() {
    activityRule.launchActivity(Intent())
    val activity = activityRule.activity
    verify(presenter, times(1)).load()
    given(presenter.load()).will { mockDataLoading(activity, resource) }
    given(presenter.refresh()).will { mockDataLoading(activity, resource) }

    onView(withId(R.id.radio_stations__refresh_layout)).perform(swipeDown())

    verify(presenter, times(1)).refresh()
  }

  private fun mockDataLoading(
    view: RadioView,
    countingIdlingResource: CountingIdlingResource
  ): Disposable? {
    countingIdlingResource.increment()

    return Single.just(emptyList<RadioStation>())
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doFinally { countingIdlingResource.decrement() }
      .subscribe({
        view.update(it)
        view.hideLoading()
      }, {
        fail()
      })
  }

  private fun mockLoadingError(
    view: RadioFragment,
    countingIdlingResource: CountingIdlingResource
  ): Disposable? {
    countingIdlingResource.increment()
    return Single.just(SocketTimeoutException())
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doFinally { countingIdlingResource.decrement() }
      .subscribe({
        view.hideLoading()
        view.error(it)
      }, {
        fail()
      })
  }

  @Test
  fun radioView_swipeToRefresh_loadError() {
    activityRule.launchActivity(Intent())
    val view = activityRule.activity
    given(presenter.refresh()).will { mockLoadingError(view, resource) }

    onView(withId(R.id.radio_stations__refresh_layout)).perform(swipeDown())
    onView(allOf(
      withId(android.support.design.R.id.snackbar_text),
      withText(R.string.radio__loading_failed)
    )).check(matches(withEffectiveVisibility(VISIBLE)))

    verify(presenter, times(1)).refresh()
    verify(presenter, times(1)).load()
  }

  inner class TestModule(dao: RadioStationDao) : Module() {
    init {
      bind(RadioPresenter::class.java).toInstance(presenter)
      bind(MiniControlPresenter::class.java).toInstance(miniControlPresenter)
      bind(RadioRepository::class.java).to(RadioRepositoryImpl::class.java)
      bind(RadioStationDao::class.java).toInstance(dao)
    }
  }
}