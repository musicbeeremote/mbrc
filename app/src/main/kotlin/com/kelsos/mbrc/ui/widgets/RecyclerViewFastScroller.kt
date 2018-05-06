package com.kelsos.mbrc.ui.widgets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.IdRes
import android.support.annotation.IntDef
import android.support.annotation.LayoutRes
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

class RecyclerViewFastScroller : LinearLayout {

  private var bubble: TextView? = null
  private lateinit var handle: View
  private var recyclerView: RecyclerView? = null
  private var inHeight: Int = 0
  private var isInitialized = false
  private var currentAnimator: ObjectAnimator? = null
  private var scrollStateChangeListener: RecyclerViewFastScroller.ScrollStateChangeListener? = null

  private val onScrollListener = object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
      updateBubbleAndHandlePosition()
    }
  }

  interface BubbleTextGetter {
    fun getTextToShowInBubble(pos: Int): String
  }

  interface ScrollStateChangeListener {
    fun scrollStateChanged(@ScrollState state: Int)
  }

  fun setOnScrollStateChangeListener(scrollStateChangeListener: ScrollStateChangeListener) {
    this.scrollStateChangeListener = scrollStateChangeListener
  }

  constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr) {
    init()
  }

  constructor(context: Context) : super(context) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    init()
  }

  internal fun init() {
    if (isInitialized)
      return
    isInitialized = true
    orientation = LinearLayout.HORIZONTAL
    clipChildren = false
  }

  fun setViewsToUse(@LayoutRes layoutResId: Int, @IdRes bubbleResId: Int, @IdRes handleResId: Int) {
    val inflater = LayoutInflater.from(context)
    inflater.inflate(layoutResId, this, true)
    bubble = findViewById(bubbleResId)
    bubble?.isInvisible = true
    handle = findViewById(handleResId)
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    inHeight = h
    updateBubbleAndHandlePosition()
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    val action = event.action

    when (action) {
      MotionEvent.ACTION_DOWN -> {
        if (event.x < handle.x - ViewCompat.getPaddingStart(handle)) {
          return false
        }

        currentAnimator?.cancel()

        bubble?.let {
          if (it.isInvisible) {
            showBubble()
          }
        }

        handle.isSelected = true

        val y = event.y
        setBubbleAndHandlePosition(y)
        setRecyclerViewPosition(y)
        scrollStateChangeListener?.scrollStateChanged(SCROLL_STARTED)
        return true
      }
      MotionEvent.ACTION_MOVE -> {
        val y = event.y
        setBubbleAndHandlePosition(y)
        setRecyclerViewPosition(y)
        return true
      }
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        handle.isSelected = false
        hideBubble()
        scrollStateChangeListener?.scrollStateChanged(SCROLL_ENDED)
        return true
      }
    }
    return super.onTouchEvent(event)
  }

  fun setRecyclerView(recyclerView: RecyclerView) {
    if (this.recyclerView === recyclerView) {
      return
    }

    this.recyclerView?.removeOnScrollListener(onScrollListener)
    this.recyclerView = recyclerView
    if (this.recyclerView == null)
      return
    recyclerView.addOnScrollListener(onScrollListener)
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    recyclerView?.removeOnScrollListener(onScrollListener)
    recyclerView = null
  }

  private fun setRecyclerViewPosition(y: Float) {

    val recyclerView = this.recyclerView ?: return

    val itemCount = recyclerView.adapter.itemCount
    val proportion: Float = if (handle.y == 0F) {
      0f
    } else {
      if (handle.y + handle.height >= inHeight - TRACK_SNAP_RANGE)
        1f
      else y / inHeight.toFloat()
    }

    val targetPos = getValueInRange(0, itemCount - 1, (proportion * itemCount.toFloat()).toInt())
    (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(targetPos, 0)
    val bubbleText = (recyclerView.adapter as BubbleTextGetter).getTextToShowInBubble(targetPos)
    bubble?.text = bubbleText
  }

  private fun getValueInRange(min: Int, max: Int, value: Int): Int {
    val minimum = Math.max(min, value)
    return Math.min(minimum, max)
  }

  private fun updateBubbleAndHandlePosition() {
    if (bubble == null || handle.isSelected) {
      return
    }

    val recyclerView = recyclerView ?: return

    val verticalScrollOffset = recyclerView.computeVerticalScrollOffset()
    val verticalScrollRange = recyclerView.computeVerticalScrollRange()
    val proportion = verticalScrollOffset.toFloat() / (verticalScrollRange.toFloat() - inHeight)

    setBubbleAndHandlePosition(inHeight * proportion)
  }

  private fun setBubbleAndHandlePosition(y: Float) {
    val handleHeight = handle.height
    handle.y = getValueInRange(0, inHeight - handleHeight, (y - handleHeight / 2).toInt()).toFloat()
    bubble?.let {
      val bubbleHeight = it.height
      it.y = getValueInRange(
        0,
        inHeight - bubbleHeight - handleHeight / 2,
        (y - bubbleHeight).toInt()
      ).toFloat()
    }
  }

  @SuppressLint("ObjectAnimatorBinding")
  private fun showBubble() {
    val bubbleView = bubble ?: return
    bubbleView.isVisible = true

    currentAnimator?.cancel()
    currentAnimator = ObjectAnimator.ofFloat(bubbleView, "alpha", 0f, 1f)
      .setDuration(BUBBLE_ANIMATION_DURATION.toLong())
      .apply { start() }
  }

  @SuppressLint("ObjectAnimatorBinding")
  private fun hideBubble() {
    val bubbleView = bubble ?: return

    currentAnimator?.cancel()
    currentAnimator = ObjectAnimator.ofFloat(bubble, "alpha", 1f, 0f)
      .setDuration(BUBBLE_ANIMATION_DURATION.toLong())
      .apply {
        addListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            bubbleView.isInvisible = true
            currentAnimator = null
          }

          override fun onAnimationCancel(animation: Animator) {
            super.onAnimationCancel(animation)
            bubbleView.isInvisible = true
            currentAnimator = null
          }
        })
        start()
      }
  }

  @IntDef(SCROLL_STARTED, SCROLL_ENDED)
  @Retention(AnnotationRetention.SOURCE)
  annotation class ScrollState

  companion object {
    const val SCROLL_STARTED = 1
    const val SCROLL_ENDED = 2

    private const val BUBBLE_ANIMATION_DURATION = 100
    private const val TRACK_SNAP_RANGE = 5
  }
}