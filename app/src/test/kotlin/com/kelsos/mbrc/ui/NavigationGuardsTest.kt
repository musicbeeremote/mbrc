package com.kelsos.mbrc.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Regression coverage for #348.
 *
 * `NavHost`'s `PredictiveBackHandler` captures the entry to pop when the back gesture starts and
 * pops it when the gesture completes. A navigation landing in between moves the top of the back
 * stack, and the pop then fails `check(entry == popUpTo)` with
 * `Attempted to pop route=now_playing_list, which is not the top of the back stack (route=home)`.
 *
 * Starting the gesture drops the departing entry to [Lifecycle.State.STARTED] via
 * `prepareForTransition`, so a destination that is no longer `RESUMED` must not navigate or pop.
 */
@RunWith(AndroidJUnit4::class)
class NavigationGuardsTest {
  @Test
  fun `entry is resumed only in the RESUMED state`() {
    assertThat(entryIn(Lifecycle.State.RESUMED).lifecycleIsResumed()).isTrue()
    assertThat(entryIn(Lifecycle.State.STARTED).lifecycleIsResumed()).isFalse()
    assertThat(entryIn(Lifecycle.State.CREATED).lifecycleIsResumed()).isFalse()
  }

  @Test
  fun `navigateFrom navigates while the source destination is resumed`() {
    val navController = mockk<NavHostController>(relaxed = true)

    navController.navigateFrom(entryIn(Lifecycle.State.RESUMED), ROUTE)

    verify(exactly = 1) { navController.navigate(ROUTE) }
  }

  @Test
  fun `navigateFrom is a no-op once a back gesture has started`() {
    val navController = mockk<NavHostController>(relaxed = true)

    // prepareForTransition has dropped the departing entry to STARTED.
    navController.navigateFrom(entryIn(Lifecycle.State.STARTED), ROUTE)

    verify(exactly = 0) { navController.navigate(ROUTE) }
  }

  @Test
  fun `popFrom pops while the source destination is resumed`() {
    val navController = mockk<NavHostController>(relaxed = true)

    navController.popFrom(entryIn(Lifecycle.State.RESUMED))

    verify(exactly = 1) { navController.popBackStack() }
  }

  @Test
  fun `popFrom is a no-op when the destination is no longer the top of the back stack`() {
    val navController = mockk<NavHostController>(relaxed = true)

    // A second tap on the back arrow arrives after the first pop already moved the entry off top.
    navController.popFrom(entryIn(Lifecycle.State.STARTED))

    verify(exactly = 0) { navController.popBackStack() }
  }

  private fun entryIn(state: Lifecycle.State): NavBackStackEntry {
    val owner = mockk<LifecycleOwner>()
    val registry = LifecycleRegistry.createUnsafe(owner)
    registry.currentState = state
    return mockk<NavBackStackEntry> { every { lifecycle } returns registry }
  }

  private companion object {
    const val ROUTE = "album_tracks/1/album/artist"
  }
}
