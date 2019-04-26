package com.kelsos.mbrc.utils

import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.espresso.matcher.BoundedMatcher
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