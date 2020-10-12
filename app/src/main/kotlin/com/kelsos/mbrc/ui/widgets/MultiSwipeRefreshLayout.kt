package com.kelsos.mbrc.ui.widgets

/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import androidx.core.view.ViewCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.View
import android.widget.AbsListView

/**
 * A descendant of [android.support.v4.widget.SwipeRefreshLayout] which supports multiple
 * child views triggering a refresh gesture. You set the views which can trigger the gesture via
 * [.setSwipeableChildren], providing it the child ids.
 */
class MultiSwipeRefreshLayout :
  SwipeRefreshLayout {

  private lateinit var swipeableChildren: Array<View?>

  constructor(context: Context) : super(context) {
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
  }

  /**
   * Set the children which can trigger a refresh by swiping down when they are visible. These
   * views need to be a descendant of this view.
   */
  fun setSwipeableChildren(vararg ids: Int) {
    // Iterate through the ids and find the Views
    swipeableChildren = arrayOfNulls<View>(ids.size)
    for (i in ids.indices) {
      swipeableChildren[i] = findViewById(ids[i])
    }
  }

  fun clearSwipeableChildren() {
    swipeableChildren = emptyArray<View?>()
  }

  /**
   * This method controls when the swipe-to-refresh gesture is triggered. By returning false here
   * we are signifying that the view is in a state where a refresh gesture can start.

   *
   * As [android.support.v4.widget.SwipeRefreshLayout] only supports one direct child by
   * default, we need to manually iterate through our swipeable children to see if any are in a
   * state to trigger the gesture. If so we return false to start the gesture.
   */
  override fun canChildScrollUp(): Boolean {
    if (swipeableChildren.isNotEmpty()) {
      // Iterate through the scrollable children and check if any of them can not scroll up
      for (view in swipeableChildren) {
        if (view != null && view.isShown && !canViewScrollUp(view)) {
          // If the view is shown, and can not scroll upwards, return false and start the
          // gesture.
          return false
        }
      }
    }
    return true
  }

  /**
   * Utility method to check whether a [View] can scroll up from it's current position.
   * Handles platform version differences, providing backwards compatible functionality where
   * needed.
   */
  private fun canViewScrollUp(view: View): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= 14) {
      // For ICS and above we can call canScrollVertically() to determine this
      return ViewCompat.canScrollVertically(view, -1)
    } else {
      if (view is AbsListView) {
        // Pre-ICS we need to manually check the first visible item and the child view's top
        // value
        val listView = view
        return listView.childCount > 0 && (listView.firstVisiblePosition > 0 || listView.getChildAt(0).top < listView.getPaddingTop())
      } else {
        // For all other view types we just check the getScrollY() value
        return view.scrollY > 0
      }
    }
  }
}
