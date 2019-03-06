package com.kelsos.mbrc.ui

import android.app.Activity
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun Activity.snackbar(@StringRes resId: Int) {
  Snackbar.make(
    findViewById(android.R.id.content),
    resId,
    Snackbar.LENGTH_SHORT
  ).show()
}