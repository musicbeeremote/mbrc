package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.webkit.WebView
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kelsos.mbrc.ui.preferences.SettingsFragment

fun SettingsFragment.webDialog(@StringRes titleResId: Int, url: String): Dialog {
  return MaterialAlertDialogBuilder(requireActivity())
    .setView(
      WebView(requireActivity()).apply {
        loadUrl(url)
      }
    )
    .setTitle(titleResId)
    .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
    .show()
}
