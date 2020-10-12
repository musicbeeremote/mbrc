package com.kelsos.mbrc.ui.widgets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView


class RecyclerViewFastScroller : LinearLayout {
  private val BUBBLE_ANIMATION_DURATION = 100
  private val TRACK_SNAP_RANGE = 5

  private var bubble: TextView? = null
  private var handle: View? = null
  private var recyclerView: RecyclerView? = null
  private var inHeight: Int = 0
  private var isInitialized = false
  private var currentAnimator: ObjectAnimator? = null

  private val onScrollListener = object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
      updateBubbleAndHandlePosition()
    }
  }

  interface BubbleTextGetter {
    fun getTextToShowInBubble(pos: Int): String
  }


  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init(context)
  }

  constructor(context: Context) : super(context) {
    init(context)
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    init(context)
  }


  protected fun init(context: Context) {
    if (isInitialized)
      return
    isInitialized = true
    orientation = LinearLayout.HORIZONTAL
    clipChildren = false
  }

  fun setViewsToUse(@LayoutRes layoutResId: Int, @IdRes bubbleResId: Int, @IdRes handleResId: Int) {
    val inflater = LayoutInflater.from(context)
    inflater.inflate(layoutResId, this, true)
    bubble = findViewById(bubbleResId) as TextView
    if (bubble != null)
      bubble!!.visibility = View.INVISIBLE
    handle = findViewById(handleResId)
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    inHeight = h
    updateBubbleAndHandlePosition()
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    val action = event.action
    val handle = this.handle;
    when (action) {
      MotionEvent.ACTION_DOWN -> {
        if (event.x < handle!!.getX() - ViewCompat.getPaddingStart(handle))
          return false
        if (currentAnimator != null)
          currentAnimator!!.cancel()
        if (bubble != null && bubble!!.visibility == View.INVISIBLE)
          showBubble()
        handle!!.setSelected(true)
        val y = event.y
        setBubbleAndHandlePosition(y)
        setRecyclerViewPosition(y)
        return true
      }
      MotionEvent.ACTION_MOVE -> {
        val y = event.y
        setBubbleAndHandlePosition(y)
        setRecyclerViewPosition(y)
        return true
      }
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        handle!!.setSelected(false)
        hideBubble()
        return true
      }
    }
    return super.onTouchEvent(event)
  }

  fun setRecyclerView(recyclerView: RecyclerView) {
    if (this.recyclerView !== recyclerView) {
      if (this.recyclerView != null)
        this.recyclerView!!.removeOnScrollListener(onScrollListener)
      this.recyclerView = recyclerView
      if (this.recyclerView == null)
        return
      recyclerView.addOnScrollListener(onScrollListener)
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    if (recyclerView != null) {
      recyclerView!!.removeOnScrollListener(onScrollListener)
      recyclerView = null
    }
  }

  private fun setRecyclerViewPosition(y: Float) {
    if (recyclerView != null) {
      val itemCount = recyclerView!!.adapter?.itemCount ?: 0
      val proportion: Float = when {
        handle!!.y == 0F -> 0f
        handle!!.y + handle!!.height >= inHeight - TRACK_SNAP_RANGE -> 1f
        else -> y / inHeight.toFloat()
      }
      val targetPos = getValueInRange(0, itemCount - 1, (proportion * itemCount.toFloat()).toInt())
      (recyclerView!!.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(targetPos, 0)
      val bubbleText = (recyclerView!!.adapter as BubbleTextGetter).getTextToShowInBubble(targetPos)
      if (bubble != null)
        bubble!!.text = bubbleText
    }
  }

  private fun getValueInRange(min: Int, max: Int, value: Int): Int {
    val minimum = Math.max(min, value)
    return Math.min(minimum, max)
  }

  private fun updateBubbleAndHandlePosition() {
    if (bubble == null || handle!!.isSelected())
      return

    val verticalScrollOffset = recyclerView!!.computeVerticalScrollOffset()
    val verticalScrollRange = recyclerView!!.computeVerticalScrollRange()
    val proportion = verticalScrollOffset.toFloat() / (verticalScrollRange.toFloat() - inHeight)
    setBubbleAndHandlePosition(inHeight * proportion)
  }

  private fun setBubbleAndHandlePosition(y: Float) {
    val handleHeight = handle!!.getHeight()
    handle!!.y = getValueInRange(0, inHeight - handleHeight, (y - handleHeight / 2).toInt()).toFloat()
    if (bubble != null) {
      val bubbleHeight = bubble!!.height
      bubble!!.y = getValueInRange(0, inHeight - bubbleHeight - handleHeight / 2, (y - bubbleHeight).toInt()).toFloat()
    }
  }

  private fun showBubble() {
    if (bubble == null)
      return
    bubble!!.visibility = View.VISIBLE
    if (currentAnimator != null)
      currentAnimator!!.cancel()
    currentAnimator = ObjectAnimator.ofFloat(bubble, "alpha", 0f, 1f).setDuration(BUBBLE_ANIMATION_DURATION.toLong())
    currentAnimator!!.start()
  }

  private fun hideBubble() {
    if (bubble == null)
      return
    if (currentAnimator != null)
      currentAnimator!!.cancel()
    currentAnimator = ObjectAnimator.ofFloat(bubble, "alpha", 1f, 0f).setDuration(BUBBLE_ANIMATION_DURATION.toLong())
    currentAnimator!!.addListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator) {
        super.onAnimationEnd(animation)
        bubble!!.visibility = View.INVISIBLE
        currentAnimator = null
      }

      override fun onAnimationCancel(animation: Animator) {
        super.onAnimationCancel(animation)
        bubble!!.visibility = View.INVISIBLE
        currentAnimator = null
      }
    })
    currentAnimator!!.start()
  }

}
