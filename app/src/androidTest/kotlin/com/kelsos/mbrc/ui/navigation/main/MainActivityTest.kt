package com.kelsos.mbrc.ui.navigation.main

import android.app.Application
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE
import android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.kelsos.mbrc.R
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.repository.ModelCache
import com.kelsos.mbrc.services.ServiceChecker
import com.kelsos.mbrc.utilities.SettingsManager
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import rx.Completable
import rx.Scheduler
import rx.Single
import rx.schedulers.TestScheduler
import toothpick.Toothpick
import toothpick.config.Module
import toothpick.testing.ToothPickRule


@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

  var toothPickRule = ToothPickRule(this)
  val activityRule = IntentsTestRule(MainActivity::class.java, true, false)
  @Rule fun chain(): TestRule = RuleChain.outerRule(toothPickRule).around(activityRule)

  private lateinit var model: MainDataModel
  private lateinit var mockBus: RxBus
  private lateinit var mockSettingsManager: SettingsManager
  private lateinit var mockCache: ModelCache
  private lateinit var application: Application
  private lateinit var mockServiceChecker: ServiceChecker

  @Before
  fun setUp() {
    mockSettingsManager = mock(SettingsManager::class.java)
    mockCache = mock(ModelCache::class.java)
    mockServiceChecker = mock(ServiceChecker::class.java)

    `when`(mockCache.restoreCover()).thenReturn(Single.just(""))
    `when`(mockCache.persistCover(anyString())).thenReturn(Completable.complete())
    `when`(mockCache.restoreInfo()).thenReturn(Single.just(TrackInfo()))
    `when`(mockSettingsManager.shouldShowPluginUpdate()).thenReturn(Single.just(false))
    mockBus = mock(RxBus::class.java)

    model = MainDataModel(mockBus, mockCache)

    application = InstrumentationRegistry.getTargetContext().applicationContext as Application
    val scope = Toothpick.openScope(application)
    scope.installModules(TestModule())
  }

  @After
  fun tearDown() {
    Toothpick.reset()
  }

  @Test
  fun testShowOutdatedPluginSnackBar() {
    activityRule.launchActivity(Intent())
    onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(R.string.plugin_protocol_out_of_date)))
        .check(doesNotExist())
    model.pluginProtocol = 1
    onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(R.string.plugin_protocol_out_of_date)))
        .check(matches(withEffectiveVisibility(VISIBLE)))
  }

  @Test
  fun testShouldNotShowOutdatedPluginSnackBar() {
    activityRule.launchActivity(Intent())
    onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(R.string.plugin_protocol_out_of_date)))
        .check(doesNotExist())
    model.pluginProtocol = 3
    onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(R.string.plugin_protocol_out_of_date)))
        .check(doesNotExist())
  }

  inner class TestModule : Module() {
    init {
      bind(MainDataModel::class.java).toProviderInstance { model }.providesSingletonInScope()
      bind(RxBus::class.java).toProviderInstance { mockBus }.providesSingletonInScope()
      bind(SettingsManager::class.java)
          .toProviderInstance { mockSettingsManager }
          .providesSingletonInScope()
      bind(Scheduler::class.java).withName("main").toProviderInstance { TestScheduler() }
      bind(Application::class.java).toInstance(application)
      bind(ServiceChecker::class.java).toInstance(mockServiceChecker)
    }
  }
}
