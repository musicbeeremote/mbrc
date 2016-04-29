package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.activities.SettingsActivity

class SetupDialogFragment : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = MaterialDialog.Builder(activity)
    builder.content(R.string.dialog_application_setup)
    builder.title(R.string.dialog_application_setup_title)
    builder.positiveText(R.string.dialog_application_setup_positive)
    builder.negativeText(R.string.dialog_application_setup_negative)
    builder.onPositive { dialog, which ->
      dialog.dismiss()
      startActivity(Intent(activity, SettingsActivity::class.java))
    }
    builder.onNegative { dialog, which -> dialog.dismiss() }

    return builder.build()
  }
}
