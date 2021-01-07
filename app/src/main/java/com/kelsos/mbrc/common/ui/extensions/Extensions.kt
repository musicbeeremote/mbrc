package com.kelsos.mbrc.common.ui.extensions

import android.content.Context
import android.text.SpannedString
import android.widget.ImageButton
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.NavigationActivity
import com.kelsos.mbrc.R

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

fun ImageButton.setStatusColor(enabled: Boolean) {
  val colorResId = if (enabled) R.color.accent else R.color.button_dark
  setColorFilter(context.getColor(colorResId))
}

fun ImageButton.setIcon(
  enabled: Boolean,
  @DrawableRes onRes: Int,
  @DrawableRes offRes: Int
) {
  val iconResId = if (enabled) onRes else offRes
  setImageResource(iconResId)
}

fun Fragment.setAppBarTitle(title: String? = null) {
  val activity = (requireActivity() as NavigationActivity)
  val supportActionBar = checkNotNull(activity.supportActionBar)
  supportActionBar.title = title
}
