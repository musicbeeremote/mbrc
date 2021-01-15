package com.kelsos.mbrc.utils

import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import org.hamcrest.Description
import org.hamcrest.Matcher

object Matchers {

  fun isRefreshing(): Matcher<View> {
    return object : BoundedMatcher<View, SwipeRefreshLayout>(SwipeRefreshLayout::class.java) {

      override fun describeTo(description: Description) {
        description.appendText("is refreshing")
      }

      override fun matchesSafely(item: SwipeRefreshLayout): Boolean = item.isRefreshing
    }
  }
}

fun ViewInteraction.isVisible() {
  check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
}

fun ViewInteraction.isGone() {
  check(matches(withEffectiveVisibility(Visibility.GONE)))
}

fun ViewInteraction.doesNotExist() {
  check(ViewAssertions.doesNotExist())
}

fun swipeToRemove(): GeneralSwipeAction {
  return GeneralSwipeAction(
    Swipe.SLOW, GeneralLocation.CENTER_LEFT,
    GeneralLocation.CENTER_RIGHT, Press.FINGER
  )
}

/**
 * Click action that ignores visibility of item to avoid robolectric limitations
 */
object Click : ViewAction {
  override fun getConstraints(): Matcher<View> = ViewMatchers.isEnabled()

  override fun getDescription(): String = "Click"

  override fun perform(uiController: UiController?, view: View?) {
    view?.performClick()
  }
}
