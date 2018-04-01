/*
 * Copyright 2014 - 2015 Henning Dodenhof
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kelsos.mbrc.ui.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.kelsos.mbrc.R

class CircleImageView : AppCompatImageView {

  private val drawableRect = RectF()
  private val borderRect = RectF()

  private val shaderMatrix = Matrix()
  private val bitmapPaint = Paint()
  private val borderPaint = Paint()
  private val fillPaint = Paint()

  private var mBorderColor = DEFAULT_BORDER_COLOR
  private var mBorderWidth = DEFAULT_BORDER_WIDTH
  private var mFillColor = DEFAULT_FILL_COLOR

  private var bitmap: Bitmap? = null
  private var bitmapShader: BitmapShader? = null
  private var bitmapWidth: Int = 0
  private var bitmapHeight: Int = 0

  private var drawableRadius: Float = 0.toFloat()
  private var borderRadius: Float = 0.toFloat()

  private var colorFilter: ColorFilter? = null

  private var ready: Boolean = false
  private var setupPending: Boolean = false
  private var borderOverlay: Boolean = false

  @Suppress("MemberVisibilityCanBePrivate")
  var isDisableCircularTransformation: Boolean = false
    set(disableCircularTransformation) {
      if (isDisableCircularTransformation == disableCircularTransformation) {
        return
      }

      field = disableCircularTransformation
      initializeBitmap()
    }

  constructor(context: Context) : super(context) {

    init()
  }

  @JvmOverloads
  constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : super(
    context,
    attrs,
    defStyle
  ) {

    val a = context.obtainStyledAttributes(
      attrs,
      R.styleable.CircleImageView,
      defStyle,
      0
    )

    mBorderWidth =
      a.getDimensionPixelSize(
        R.styleable.CircleImageView_civ_border_width,
        DEFAULT_BORDER_WIDTH
      )
    mBorderColor = a.getColor(R.styleable.CircleImageView_civ_border_color, DEFAULT_BORDER_COLOR)
    borderOverlay =
      a.getBoolean(
        R.styleable.CircleImageView_civ_border_overlay,
        DEFAULT_BORDER_OVERLAY
      )
    mFillColor = a.getColor(R.styleable.CircleImageView_civ_fill_color, DEFAULT_FILL_COLOR)

    a.recycle()

    init()
  }

  private fun init() {
    super.setScaleType(SCALE_TYPE)
    ready = true

    if (setupPending) {
      setup()
      setupPending = false
    }
  }

  override fun getScaleType(): ScaleType {
    return SCALE_TYPE
  }

  override fun setScaleType(scaleType: ScaleType) {
    if (scaleType != SCALE_TYPE) {
      throw IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType))
    }
  }

  override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
    if (adjustViewBounds) {
      throw IllegalArgumentException("adjustViewBounds not supported.")
    }
  }

  override fun onDraw(canvas: Canvas) {
    if (isDisableCircularTransformation) {
      super.onDraw(canvas)
      return
    }

    if (bitmap == null) {
      return
    }

    if (mFillColor != Color.TRANSPARENT) {
      canvas.drawCircle(
        drawableRect.centerX(),
        drawableRect.centerY(),
        drawableRadius,
        fillPaint
      )
    }
    canvas.drawCircle(
      drawableRect.centerX(),
      drawableRect.centerY(),
      drawableRadius,
      bitmapPaint
    )
    if (mBorderWidth != 0) {
      canvas.drawCircle(borderRect.centerX(), borderRect.centerY(), borderRadius, borderPaint)
    }
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    setup()
  }

  @Suppress("MemberVisibilityCanBePrivate")
  var borderColor: Int
    get() = mBorderColor
    set(@ColorInt borderColor) {
      if (borderColor == mBorderColor) {
        return
      }

      mBorderColor = borderColor
      borderPaint.color = mBorderColor
      invalidate()
    }

  @Suppress("unused")
  fun setBorderColorResource(@ColorRes borderColorRes: Int) {
    borderColor = ContextCompat.getColor(context, borderColorRes)
  }

  var fillColor: Int
    get() = mFillColor
    set(@ColorInt fillColor) {
      if (fillColor == mFillColor) {
        return
      }

      mFillColor = fillColor
      fillPaint.color = fillColor
      invalidate()
    }

  @Suppress("unused")
  fun setFillColorResource(@ColorRes fillColorRes: Int) {
    fillColor = ContextCompat.getColor(context, fillColorRes)
  }

  var borderWidth: Int
    get() = mBorderWidth
    set(borderWidth) {
      if (borderWidth == mBorderWidth) {
        return
      }

      mBorderWidth = borderWidth
      setup()
    }

  @Suppress("unused")
  var isBorderOverlay: Boolean
    get() = borderOverlay
    set(borderOverlay) {
      if (borderOverlay == this.borderOverlay) {
        return
      }

      this.borderOverlay = borderOverlay
      setup()
    }

  override fun setImageBitmap(bm: Bitmap) {
    super.setImageBitmap(bm)
    initializeBitmap()
  }

  override fun setImageDrawable(drawable: Drawable?) {
    super.setImageDrawable(drawable)
    initializeBitmap()
  }

  override fun setImageResource(@DrawableRes resId: Int) {
    super.setImageResource(resId)
    initializeBitmap()
  }

  override fun setImageURI(uri: Uri?) {
    super.setImageURI(uri)
    initializeBitmap()
  }

  override fun setColorFilter(cf: ColorFilter) {
    if (cf === colorFilter) {
      return
    }

    colorFilter = cf
    applyColorFilter()
    invalidate()
  }

  override fun getColorFilter(): ColorFilter {
    return colorFilter!!
  }

  private fun applyColorFilter() {
    bitmapPaint.colorFilter = colorFilter
  }

  private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
    if (drawable == null) {
      return null
    }

    if (drawable is BitmapDrawable) {
      return drawable.bitmap
    }

    try {
      val bitmap: Bitmap = if (drawable is ColorDrawable) {
        Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG)
      } else {
        Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, BITMAP_CONFIG)
      }

      val canvas = Canvas(bitmap)
      drawable.run {
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
      }
      return bitmap
    } catch (e: Exception) {
      e.printStackTrace()
      return null
    }
  }

  private fun initializeBitmap() {
    bitmap = if (isDisableCircularTransformation) {
      null
    } else {
      getBitmapFromDrawable(drawable)
    }
    setup()
  }

  private fun setup() {
    if (!ready) {
      setupPending = true
      return
    }

    if (width == 0 && height == 0) {
      return
    }

    if (bitmap == null) {
      invalidate()
      return
    }

    bitmapShader = BitmapShader(bitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

    bitmapPaint.isAntiAlias = true
    bitmapPaint.shader = bitmapShader

    borderPaint.style = Paint.Style.STROKE
    borderPaint.isAntiAlias = true
    borderPaint.color = mBorderColor
    borderPaint.strokeWidth = mBorderWidth.toFloat()

    fillPaint.style = Paint.Style.FILL
    fillPaint.isAntiAlias = true
    fillPaint.color = mFillColor

    bitmapHeight = bitmap!!.height
    bitmapWidth = bitmap!!.width

    borderRect.set(calculateBounds())
    borderRadius = Math.min(
      (borderRect.height() - mBorderWidth) / 2.0f,
      (borderRect.width() - mBorderWidth) / 2.0f
    )

    drawableRect.set(borderRect)
    if (!borderOverlay) {
      drawableRect.inset(mBorderWidth.toFloat(), mBorderWidth.toFloat())
    }
    drawableRadius = Math.min(drawableRect.height() / 2.0f, drawableRect.width() / 2.0f)

    applyColorFilter()
    updateShaderMatrix()
    invalidate()
  }

  private fun calculateBounds(): RectF {
    val availableWidth = width - paddingLeft - paddingRight
    val availableHeight = height - paddingTop - paddingBottom

    val sideLength = Math.min(availableWidth, availableHeight)

    val left = paddingLeft + (availableWidth - sideLength) / 2f
    val top = paddingTop + (availableHeight - sideLength) / 2f

    return RectF(left, top, left + sideLength, top + sideLength)
  }

  private fun updateShaderMatrix() {
    val scale: Float
    var dx = 0f
    var dy = 0f

    shaderMatrix.set(null)

    if (bitmapWidth * drawableRect.height() > drawableRect.width() * bitmapHeight) {
      scale = drawableRect.height() / bitmapHeight.toFloat()
      dx = (drawableRect.width() - bitmapWidth * scale) * 0.5f
    } else {
      scale = drawableRect.width() / bitmapWidth.toFloat()
      dy = (drawableRect.height() - bitmapHeight * scale) * 0.5f
    }

    shaderMatrix.setScale(scale, scale)
    shaderMatrix.postTranslate(
      (dx + 0.5f).toInt() + drawableRect.left,
      (dy + 0.5f).toInt() + drawableRect.top
    )

    bitmapShader!!.setLocalMatrix(shaderMatrix)
  }

  companion object {

    private val SCALE_TYPE = ScaleType.CENTER_CROP

    private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
    private const val COLORDRAWABLE_DIMENSION = 2

    private const val DEFAULT_BORDER_WIDTH = 0
    private const val DEFAULT_BORDER_COLOR = Color.BLACK
    private const val DEFAULT_FILL_COLOR = Color.TRANSPARENT
    private const val DEFAULT_BORDER_OVERLAY = false
  }
}
