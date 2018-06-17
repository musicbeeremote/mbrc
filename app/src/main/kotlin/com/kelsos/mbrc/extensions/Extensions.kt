package com.kelsos.mbrc.extensions

import android.content.Context
import android.text.SpannedString
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color

fun Context.getDimens(): Int {
  val displayMetrics = resources.displayMetrics
  val dpHeight = displayMetrics.heightPixels / displayMetrics.density
  val dpWidth = displayMetrics.widthPixels / displayMetrics.density
  return if (dpHeight > dpWidth) {
    dpWidth.toInt()
  } else {
    dpHeight.toInt()
  }
}

fun Context.coloredSpan(@StringRes resId: Int, @ColorRes colorResId: Int): SpannedString {
  return buildSpannedString {
    color(ContextCompat.getColor(this@coloredSpan, colorResId)) {
      append(getString(resId))
    }
  }
}
