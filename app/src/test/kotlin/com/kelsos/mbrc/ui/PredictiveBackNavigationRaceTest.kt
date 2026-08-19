package com.kelsos.mbrc.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigationevent.DirectNavigationEventInput
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventDispatcherOwner
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineExceptionHandler
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Drives a real predictive back gesture against a real [NavHost] to pin down the #348 crash.
 *
 * `NavHost` captures the entry to pop when the gesture *starts* and pops it when the gesture
 * *completes*:
 *
 * ```
 * val currentBackStackEntry = currentBackStack.last()            // start
 * composeNavigator.prepareForTransition(currentBackStackEntry)   // drops it to STARTED
 * backEvent.collect { ... }
 * composeNavigator.popBackStack(currentBackStackEntry, false)    // completion
 * ```
 *
 * The reported crash comes from exactly this path (`NavHost.kt:544` -> `ComposeNavigator.kt:66` ->
 * `popBackStackFromNavigator` -> `popEntryFromBackStack`), so the harness drives the right code.
 *
 * **What these tests establish is a negative result:** a navigation landing mid-gesture does *not*
 * by itself reproduce
 * `Attempted to pop route=now_playing_list, which is not the top of the back stack (route=home)`.
 * Ruled out so far, on navigation-compose 2.9.7 (what 1.6.1 shipped) and 2.9.8 (current), both:
 *
 *  - a plain `navigate()` pushing a destination on top mid-gesture, and
 *  - the drawer's own `popUpTo(start) { saveState } / launchSingleTop / restoreState` call.
 *
 * `NavControllerImpl.popBackStackFromNavigator` pops every entry stacked above the captured entry
 * before popping it, and skips entirely when the captured entry is already gone, so neither shape
 * collides. Whatever interleaving produces the crash is still unidentified, and the guards
 * exercised here are hardening rather than a demonstrated fix for #348.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class PredictiveBackNavigationRaceTest {
  /**
   * The pop runs inside the `PredictiveBackHandler` coroutine, so a failure there never reaches the
   * test thread. This handler is installed into the composition's effect context to catch it.
   */
  private val gestureFailures = mutableListOf<Throwable>()

  @get:Rule
  val composeTestRule = createComposeRule(
    effectContext = CoroutineExceptionHandler { _, throwable -> gestureFailures += throwable }
  )

  private val backInput = DirectNavigationEventInput()
  private lateinit var navController: NavHostController
  private lateinit var nowPlayingEntry: NavBackStackEntry
  private lateinit var racingNavigate: (NavHostController, NavBackStackEntry, String) -> Unit
  private lateinit var stackAfterRacingNavigate: List<String?>

  @Test
  fun `an unguarded navigation racing a back gesture is absorbed, not a crash`() {
    setContent(racingNavigate = ::unguardedNavigate)

    raceNavigationAgainstBackGesture()

    // album_tracks did land on top of the captured entry mid-gesture...
    assertThat(stackAfterRacingNavigate)
      .containsExactly(null, HOME, NOW_PLAYING, ALBUM_TRACKS)
      .inOrder()
    // ...and popBackStackFromNavigator popped it along with the captured entry, without throwing.
    assertThat(gestureFailures).isEmpty()
    assertThat(currentRoute()).isEqualTo(HOME)
  }

  @Test
  fun `a drawer navigation racing a back gesture is absorbed too`() {
    setContent(racingNavigate = ::drawerNavigate)

    raceNavigationAgainstBackGesture()

    // popUpTo(start) { saveState } removed the captured entry outright, so the gesture's pop found
    // nothing to pop: popBackStackFromNavigator logged "Ignoring pop" instead of throwing, and the
    // drawer's destination stayed put.
    assertThat(gestureFailures).isEmpty()
    assertThat(currentRoute()).isEqualTo(ALBUM_TRACKS)
  }

  @Test
  fun `the guard turns the racing navigation into a no-op`() {
    setContent(racingNavigate = NavHostController::navigateFrom)

    raceNavigationAgainstBackGesture()

    // The racing navigation never pushed album_tracks, so the gesture pops a settled stack.
    assertThat(stackAfterRacingNavigate).containsExactly(null, HOME, NOW_PLAYING).inOrder()
    assertThat(gestureFailures).isEmpty()
    assertThat(currentRoute()).isEqualTo(HOME)
  }

  /** Starts a back gesture, fires the racing navigation mid-gesture, then completes the gesture. */
  private fun raceNavigationAgainstBackGesture() {
    composeTestRule.runOnIdle { navController.navigate(NOW_PLAYING) }
    composeTestRule.waitForIdle()
    check(currentRoute() == NOW_PLAYING) { "expected to be on $NOW_PLAYING" }

    // --- gesture start: NavHost captures now_playing_list and drops it to STARTED ---
    composeTestRule.runOnIdle { backInput.backStarted(backEvent(progress = 0f)) }
    composeTestRule.waitForIdle()
    check(entryState() == Lifecycle.State.STARTED) {
      "prepareForTransition should have dropped the entry to STARTED, was ${entryState()}"
    }
    composeTestRule.runOnIdle { backInput.backProgressed(backEvent(progress = 0.3f)) }
    composeTestRule.waitForIdle()

    // --- the racing navigation: a drawer tap, or a slow repository lookup resolving ---
    composeTestRule.runOnIdle { racingNavigate(navController, nowPlayingEntry, ALBUM_TRACKS) }
    composeTestRule.waitForIdle()
    stackAfterRacingNavigate = stack()

    // --- gesture completion: NavHost pops the entry it captured at the start ---
    composeTestRule.runOnIdle { backInput.backCompleted() }
    composeTestRule.waitForIdle()
  }

  /** The pre-fix call site: navigates regardless of whether the source is on its way out. */
  private fun unguardedNavigate(
    navController: NavHostController,
    @Suppress("UNUSED_PARAMETER") from: NavBackStackEntry,
    route: String
  ) {
    navController.navigate(route)
  }

  /** The drawer's real call: `AppDrawer.kt`'s `onNavigate`, options and all. */
  private fun drawerNavigate(
    navController: NavHostController,
    @Suppress("UNUSED_PARAMETER") from: NavBackStackEntry,
    route: String
  ) {
    navController.navigate(route) {
      popUpTo(navController.graph.startDestinationId) { saveState = true }
      launchSingleTop = true
      restoreState = true
    }
  }

  private fun stack(): List<String?> = composeTestRule.runOnIdle {
    navController.currentBackStack.value.map { it.destination.route }
  }

  private fun currentRoute(): String? =
    composeTestRule.runOnIdle { navController.currentBackStackEntry?.destination?.route }

  private fun entryState(): Lifecycle.State =
    composeTestRule.runOnIdle { nowPlayingEntry.lifecycle.currentState }

  private fun backEvent(progress: Float) =
    NavigationEvent(swipeEdge = NavigationEvent.EDGE_LEFT, progress = progress)

  private fun setContent(racingNavigate: (NavHostController, NavBackStackEntry, String) -> Unit) {
    this.racingNavigate = racingNavigate
    val owner = TestOwner()
    owner.navigationEventDispatcher.addInput(backInput)

    composeTestRule.setContent {
      navController = rememberNavController()
      CompositionLocalProvider(
        LocalNavigationEventDispatcherOwner provides owner,
        LocalLifecycleOwner provides owner,
        LocalViewModelStoreOwner provides owner
      ) {
        NavHost(navController = navController, startDestination = HOME) {
          composable(HOME) { Text(HOME) }
          composable(NOW_PLAYING) { entry ->
            nowPlayingEntry = entry
            Text(NOW_PLAYING)
          }
          composable(ALBUM_TRACKS) { Text(ALBUM_TRACKS) }
        }
      }
    }
  }

  private class TestOwner :
    LifecycleOwner,
    ViewModelStoreOwner,
    NavigationEventDispatcherOwner {
    private val registry = LifecycleRegistry.createUnsafe(this).apply {
      currentState = Lifecycle.State.RESUMED
    }
    override val lifecycle: Lifecycle get() = registry
    override val viewModelStore: ViewModelStore = ViewModelStore()
    override val navigationEventDispatcher: NavigationEventDispatcher = NavigationEventDispatcher()
  }

  private companion object {
    const val HOME = "home"
    const val NOW_PLAYING = "now_playing_list"
    const val ALBUM_TRACKS = "album_tracks"
  }
}
