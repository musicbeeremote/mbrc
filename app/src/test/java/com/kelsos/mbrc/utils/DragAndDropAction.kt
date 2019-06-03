package com.kelsos.mbrc.utils

import android.view.View
import android.view.ViewConfiguration
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.Press
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

class DragAndDropAction(
  private val sourceViewPosition: Int,
  private val targetViewPosition: Int,
  @IdRes private val dragHandle: Int = -1
) : ViewAction {

  override fun getConstraints(): Matcher<View> {
    return allOf(isDisplayed(), isAssignableFrom(RecyclerView::class.java))
  }

  override fun getDescription(): String {
    return "Drag and drop action"
  }

  override fun perform(uiController: UiController, view: View) {
    val recyclerView: RecyclerView = view as RecyclerView
    // Sending down
    recyclerView.scrollToPosition(sourceViewPosition)
    uiController.loopMainThreadUntilIdle()
    val sourceHolder = recyclerView.findViewHolderForAdapterPosition(sourceViewPosition)
    val sourceItemView = checkNotNull(sourceHolder).itemView
    val sourceView = if (dragHandle < 0) sourceItemView else sourceItemView.findViewById(dragHandle)

    val sourceViewCenter = GeneralLocation.VISIBLE_CENTER.calculateCoordinates(sourceView)
    val fingerPrecision = Press.FINGER.describePrecision()

    val downEvent = MotionEvents.sendDown(uiController, sourceViewCenter, fingerPrecision).down
    try {
      val longPressTimeout = (ViewConfiguration.getLongPressTimeout() * 2f).toLong()
      uiController.loopMainThreadForAtLeast(longPressTimeout)

      recyclerView.scrollToPosition(targetViewPosition)
      uiController.loopMainThreadUntilIdle()

      val targetHolder = recyclerView.findViewHolderForAdapterPosition(targetViewPosition)
      val targetItemView = checkNotNull(targetHolder).itemView
      val targetView = if (dragHandle < 0) {
        targetItemView
      } else {
        targetItemView.findViewById(dragHandle)
      }
      val targetViewLocation = GeneralLocation.CENTER.calculateCoordinates(targetView)
      targetViewLocation[1] = targetViewLocation[1] + 10

      val steps = interpolate(sourceViewCenter, targetViewLocation)

      for (i in 0 until steps.size) {
        val step = steps[i]
        if (!MotionEvents.sendMovement(uiController, downEvent, step)) {
          MotionEvents.sendCancel(uiController, downEvent)
        }
      }

      if (!MotionEvents.sendUp(uiController, downEvent, targetViewLocation)) {
        MotionEvents.sendCancel(uiController, downEvent)
      }
    } finally {
      downEvent.recycle()
    }
  }

  private fun interpolate(start: FloatArray, end: FloatArray): Array<FloatArray> {
    val res = Array(SWIPE_EVENT_COUNT) { FloatArray(2) }

    for (i in 1..SWIPE_EVENT_COUNT) {
      res[i - 1][0] = start[0] + (end[0] - start[0]) * i / SWIPE_EVENT_COUNT
      res[i - 1][1] = start[1] + (end[1] - start[1]) * i / SWIPE_EVENT_COUNT
    }

    return res
  }

  companion object {
    private const val SWIPE_EVENT_COUNT = 10
  }
}