package com.kelsos.mbrc.ui

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

/**
 * True while [this] entry is the one the user is actually interacting with.
 *
 * `NavHost` installs a `PredictiveBackHandler` that captures the entry to pop when the back
 * gesture *starts* and pops it when the gesture *completes*. If anything changes the top of the
 * back stack in between, `NavControllerImpl.popEntryFromBackStack` fails its
 * `check(entry == popUpTo)` and throws `Attempted to pop ..., which is not the top of the back
 * stack`.
 *
 * Starting the gesture calls `prepareForTransition`, which drops the departing entry to
 * [Lifecycle.State.STARTED], so gating navigation on [Lifecycle.State.RESUMED] turns a late or
 * duplicate navigation into a no-op instead of a crash. The same gate covers the other half of
 * the class: two taps on a back arrow before the first one recomposes.
 */
internal fun NavBackStackEntry.lifecycleIsResumed(): Boolean =
  lifecycle.currentState == Lifecycle.State.RESUMED

/**
 * Navigates to [route] only if [from] is still resumed. See [lifecycleIsResumed].
 */
internal fun NavHostController.navigateFrom(from: NavBackStackEntry, route: String) {
  if (from.lifecycleIsResumed()) {
    navigate(route)
  }
}

/**
 * Pops [from] only if it is still resumed, and therefore still the top of the back stack.
 * See [lifecycleIsResumed].
 */
internal fun NavHostController.popFrom(from: NavBackStackEntry) {
  if (from.lifecycleIsResumed()) {
    popBackStack()
  }
}
