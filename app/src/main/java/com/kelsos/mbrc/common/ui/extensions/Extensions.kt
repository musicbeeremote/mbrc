package com.kelsos.mbrc.common.ui.extensions

import android.content.Context
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.NavigationActivity

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

fun Fragment.setAppBarTitle(title: String? = null) {
  val activity = (requireActivity() as NavigationActivity)
  val supportActionBar = checkNotNull(activity.supportActionBar)
  supportActionBar.title = title
}
