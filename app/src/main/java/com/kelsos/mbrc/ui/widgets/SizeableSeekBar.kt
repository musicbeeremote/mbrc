package com.kelsos.mbrc.ui.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

class SizeableSeekBar
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) :
  AppCompatSeekBar(context, attrs) {

  private var currentThumbSizeRatio = 1.0f
  private var seekListener: OnSeekBarChangeListener? = null
  private val pendingThumb: Drawable?
  private var thumb: Drawable? = null
  private var thumbGrowAnimator: ValueAnimator? = null
  private var thumbShrinkAnimator: ValueAnimator? = null
  private val interpolator = AccelerateDecelerateInterpolator()

  private val animatorListener =
    ValueAnimator.AnimatorUpdateListener { valueAnimator ->
      currentThumbSizeRatio = valueAnimator.animatedValue as Float
      thumb?.apply {
        level = (10000f * (currentThumbSizeRatio / maxThumbSizeRatio)).toInt()
      }

      this@SizeableSeekBar.invalidate()
    }

  private val internalListener = object : OnSeekBarChangeListener {

    override fun onStopTrackingTouch(seekBar: SeekBar) {
      startThumbShrinkAnimation()
      seekListener?.onStopTrackingTouch(this@SizeableSeekBar)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
      startThumbGrowAnimation()
      seekListener?.onStartTrackingTouch(this@SizeableSeekBar)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
      seekListener?.onProgressChanged(this@SizeableSeekBar, progress, fromUser)
    }
  }

  init {
    super.setOnSeekBarChangeListener(internalListener)
    setThumb(null)
    pendingThumb = null
  }

  internal fun startThumbGrowAnimation() {
    thumbShrinkAnimator?.cancel()
    thumbShrinkAnimator = null

    thumbGrowAnimator = ValueAnimator.ofFloat(currentThumbSizeRatio, maxThumbSizeRatio).apply {
      interpolator = this@SizeableSeekBar.interpolator
      addUpdateListener(animatorListener)
      duration = 300
      start()
    }
  }

  internal fun startThumbShrinkAnimation() {
    thumbGrowAnimator?.cancel()
    thumbGrowAnimator = null

    thumbShrinkAnimator = ValueAnimator.ofFloat(currentThumbSizeRatio, 1.0f).apply {
      interpolator = this@SizeableSeekBar.interpolator
      addUpdateListener(animatorListener)
      duration = 300
      start()
    }
  }

  override fun setOnSeekBarChangeListener(listener: OnSeekBarChangeListener) {
    seekListener = listener
  }

  override fun setThumb(thumb: Drawable?) {
    var thumbDrawable: Drawable = thumb ?: return

    if (thumbDrawable !is ScaleDrawable) {
      thumbDrawable = ScaleDrawable(thumbDrawable, Gravity.CENTER, 1.0f, 1.0f)
    }

    this.thumb = thumbDrawable.apply {
      level = (10000f * (1.0f / maxThumbSizeRatio)).toInt()
    }
    super.setThumb(this.thumb)
  }

  override fun getThumb(): Drawable? = thumb

  companion object {
    private const val maxThumbSizeRatio = 2.0f
  }
}
