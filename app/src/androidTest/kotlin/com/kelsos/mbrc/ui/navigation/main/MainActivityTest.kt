package com.kelsos.mbrc.ui.navigation.main

import android.app.Application
import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.R
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.repository.ModelCache
import com.kelsos.mbrc.services.ServiceChecker
import com.kelsos.mbrc.utilities.SettingsManager
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
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
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
    `when`(mockSettingsManager.shouldShowChangeLog()).thenReturn(Single.just(false))
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
  fun testShowOutdatedDialog() {
    activityRule.launchActivity(Intent())
    onView(withText(R.string.main__dialog_plugin_outdated_message)).check(doesNotExist())
    model.pluginProtocol = 3
    onView(withText(R.string.main__dialog_plugin_outdated_message)).check(matches(isDisplayed()))
  }

  @Test
  fun testShouldNotShowOutdatedPluginSnackBar() {
    activityRule.launchActivity(Intent())
    onView(withText(R.string.main__dialog_plugin_outdated_message)).check(doesNotExist())
    model.pluginProtocol = 4
    onView(withText(R.string.main__dialog_plugin_outdated_message)).check(doesNotExist())
  }

  @Test
  fun testShouldShowChangeLog() {
    `when`(mockSettingsManager.shouldShowChangeLog()).thenReturn(Single.just(true))
    activityRule.launchActivity(Intent())
    onView(withText(R.string.main__dialog_change_log)).check(matches(isDisplayed()))
    verify(mockSettingsManager, times(1)).shouldShowChangeLog()
  }

  @Test
  fun testShouldNoShowChangeLog() {
    activityRule.launchActivity(Intent())
    onView(withText(R.string.main__dialog_change_log)).check(doesNotExist())
    verify(mockSettingsManager, times(1)).shouldShowChangeLog()
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
