package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.webkit.WebView
import com.afollestad.materialdialogs.MaterialDialog
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.activities.SettingsActivity

class UpgradeDialogFragment : DialogFragment() {
  private var isNewInstall: Boolean = false

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val webView = WebView(activity)
    webView.loadUrl("file:///android_asset/update.html")
    val builder = MaterialDialog.Builder(activity)
    builder.customView(webView, false)
    builder.title(R.string.dialog_upgrade_title)
    builder.negativeText(R.string.dialog_upgrade_negative)

    if (isNewInstall) {
      builder.positiveText(R.string.dialog_application_setup_positive)
    }
    builder.onPositive { dialog, which ->
      startActivity(Intent(activity, SettingsActivity::class.java))
      dialog.dismiss()
    }

    builder.onNegative { dialog, which -> dialog.dismiss() }
    return builder.build()
  }

  fun setNewInstall(newInstall: Boolean) {
    isNewInstall = newInstall
  }
}
