package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.webkit.WebView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.kelsos.mbrc.ui.preferences.SettingsFragment

fun SettingsFragment.webDialog(@StringRes titleResId: Int, url: String): Dialog {
  return AlertDialog.Builder(requireContext()).apply {
    setView(
      WebView(context).apply {
        loadUrl(url)
      }
    )
    setTitle(titleResId)
    setPositiveButton(android.R.string.ok) { dialogInterface, _ -> dialogInterface.dismiss() }
  }.create()
}