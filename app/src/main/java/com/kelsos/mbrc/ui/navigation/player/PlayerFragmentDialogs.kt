package com.kelsos.mbrc.ui.navigation.player

import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kelsos.mbrc.R
import com.kelsos.mbrc.extensions.coloredSpan

fun PlayerFragment.showPluginOutOfDateDialog(): AlertDialog {
  return requireContext().run {
    MaterialAlertDialogBuilder(this)
      .setTitle(coloredSpan(R.string.main__dialog_plugin_outdated_title, R.color.accent))
      .setMessage(R.string.main__dialog_plugin_outdated_message)
      .setPositiveButton(coloredSpan(android.R.string.ok, R.color.accent)) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .show()
  }
}
