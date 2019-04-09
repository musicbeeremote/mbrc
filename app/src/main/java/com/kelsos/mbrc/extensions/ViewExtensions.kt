package com.kelsos.mbrc.extensions

import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun androidx.recyclerview.widget.RecyclerView.ViewHolder.string(@StringRes resId: Int): String {
  return this.itemView.context.getString(resId)
}

fun Fragment.snackbar(@StringRes resId: Int) {
  val contentView = requireActivity().findViewById<View>(android.R.id.content)
  Snackbar.make(contentView, resId, Snackbar.LENGTH_SHORT).show()
}