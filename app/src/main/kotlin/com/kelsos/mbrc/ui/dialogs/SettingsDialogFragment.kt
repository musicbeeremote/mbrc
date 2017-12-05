package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.extensions.fail
import com.kelsos.mbrc.networking.connections.ConnectionSettings

class SettingsDialogFragment : DialogFragment() {

  @BindView(R.id.settings_dialog_host) lateinit var hostEdit: EditText
  @BindView(R.id.settings_dialog_name) lateinit var nameEdit: EditText
  @BindView(R.id.settings_dialog_port) lateinit var portEdit: EditText

  private var mListener: SettingsSaveListener? = null
  private lateinit var settings: ConnectionSettings
  private var edit: Boolean = false

  private fun setConnectionSettings(settings: ConnectionSettings) {
    this.settings = settings
  }

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    try {
      mListener = context as SettingsSaveListener?
    } catch (e: ClassCastException) {
      throw ClassCastException(context!!.toString() + " must implement SettingsDialogListener")
    }

  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val context = activity ?: fail("null activity")
    val builder = AlertDialog.Builder(context)
    builder.setView(R.layout.ui_dialog_settings)
    builder.setTitle(if (edit) R.string.settings_dialog_edit else R.string.settings_dialog_add)
    builder.setNegativeButton(android.R.string.cancel) { dialogInterface, _ -> dialogInterface.dismiss() }
    builder.setPositiveButton(if (edit) R.string.settings_dialog_save else R.string.settings_dialog_add) { dialog, _ ->
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

    val settingsDialog = builder.create()
    ButterKnife.bind(this, settingsDialog)
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

  private fun isValid(port: Int): Boolean = if (port < MIN_PORT || port > MAX_PORT) {
    val context = context ?: fail("null context")
    AlertDialog.Builder(context)
        .setTitle(R.string.alert_invalid_range)
        .setMessage(R.string.alert_invalid_port_number)
        .setPositiveButton(android.R.string.ok) { dialogInterface, _ -> dialogInterface.dismiss() }
        .show()
    false
  } else {
    true
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
