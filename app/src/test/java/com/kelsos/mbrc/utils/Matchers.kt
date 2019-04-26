package com.kelsos.mbrc.utils

import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
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
