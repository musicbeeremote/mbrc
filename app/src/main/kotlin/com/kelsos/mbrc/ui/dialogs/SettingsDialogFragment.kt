package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.kelsos.mbrc.R
import com.kelsos.mbrc.data.ConnectionSettings

class SettingsDialogFragment : DialogFragment() {

  @BindView(R.id.settings_dialog_host)
  lateinit var hostEdit: EditText
  @BindView(R.id.settings_dialog_name)
  lateinit var nameEdit: EditText
  @BindView(R.id.settings_dialog_port)
  lateinit var portEdit: EditText

  private var mListener: SettingsSaveListener? = null
  private lateinit var settings: ConnectionSettings
  private var edit: Boolean = false

  private fun setConnectionSettings(settings: ConnectionSettings) {
    this.settings = settings
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    try {
      mListener = context as SettingsSaveListener?
    } catch (e: ClassCastException) {
      throw ClassCastException("$context must implement SettingsDialogListener")
    }

  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = MaterialDialog.Builder(requireActivity())
    builder.theme(Theme.DARK)
    builder.customView(R.layout.ui_dialog_settings, false)
    builder.title(if (edit) R.string.settings_dialog_edit else R.string.settings_dialog_add)
    builder.positiveText(if (edit) R.string.settings_dialog_save else R.string.settings_dialog_add)
    builder.negativeText(android.R.string.cancel)
    builder.onPositive { dialog, which ->
      var shouldIClose = true
      val hostname = hostEdit.text.toString()
      val computerName = nameEdit.text.toString()

      if (hostname.isEmpty() || computerName.isEmpty()) {
        shouldIClose = false
      }

      val portText = portEdit.text.toString()

      val portNum = if (TextUtils.isEmpty(portText)) 0 else Integer.parseInt(portText)
      if (isValid(portNum) && shouldIClose) {
        settings.name = computerName
        settings.address = hostname
        settings.port = portNum
        mListener?.onSave(settings)
        dialog.dismiss()
      }
    }
    builder.onNegative { dialog, which -> dialog.dismiss() }

    val settingsDialog = builder.build()
    val view = settingsDialog.customView ?: return settingsDialog

    ButterKnife.bind(this, view)
    return settingsDialog
  }

  override fun onStart() {
    super.onStart()
    nameEdit.setText(settings.name)
    hostEdit.setText(settings.address)

    if (settings.port > 0) {
      portEdit.setText(settings.port.toString())
    }
  }

  private fun isValid(port: Int): Boolean {
    return if (port < MIN_PORT || port > MAX_PORT) {
      val alert = MaterialDialog.Builder(requireActivity())
      alert.title(R.string.alert_invalid_range)
      alert.content(R.string.alert_invalid_port_number)
      alert.positiveText(android.R.string.ok)
      alert.show()
      false
    } else {
      true
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (!edit) {
      settings = ConnectionSettings()
    }
  }

  interface SettingsSaveListener {
    fun onSave(settings: ConnectionSettings)
  }

  companion object {

    private val MAX_PORT = 65535
    private val MIN_PORT = 1

    fun newInstance(settings: ConnectionSettings): SettingsDialogFragment {
      val fragment = SettingsDialogFragment()
      fragment.setConnectionSettings(settings)
      fragment.edit = true
      return fragment
    }
  }
}
