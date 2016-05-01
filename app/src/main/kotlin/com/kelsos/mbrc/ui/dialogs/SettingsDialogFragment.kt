package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog
import com.kelsos.mbrc.R
import com.kelsos.mbrc.dao.DeviceSettings

class SettingsDialogFragment : DialogFragment() {

  private lateinit var hostEdit: EditText
  private lateinit var nameEdit: EditText
  private lateinit var portEdit: EditText
  private lateinit var httpEdit: EditText

  private var currentName: String? = null
  private var currentAddress: String? = null
  private var currentPort: Int = 0
  private var currentIndex: Int = 0
  private var currentHttpPort: Int = 0

  private var mListener: SettingsDialogListener? = null

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = MaterialDialog.Builder(activity)
    builder.customView(R.layout.ui_dialog_settings, false)
    builder.title(R.string.settings_dialog_add)
    builder.positiveText(R.string.settings_dialog_add)
    builder.negativeText(android.R.string.cancel)
    builder.onPositive { dialog, which ->
      var shouldIClose = true
      val hostname = hostEdit.text.toString()
      val computerName = nameEdit.text.toString()

      if (hostname.length == 0 || computerName.length == 0) {
        shouldIClose = false
      }

      val portText = portEdit.text.toString()
      val httpText = httpEdit.text.toString()

      val portNum = if (TextUtils.isEmpty(portText)) 0 else Integer.parseInt(portText)
      val httpNum = if (TextUtils.isEmpty(httpText)) 0 else Integer.parseInt(httpText)

      if (isValid(portNum) && isValid(httpNum) && shouldIClose) {

        val settings = DeviceSettings()
        settings.address = hostname
        settings.name = computerName
        settings.port = portNum
        settings.http = httpNum

        mListener?.onDialogPositiveClick(this@SettingsDialogFragment, settings)
        dialog.dismiss()
      }
    }

    val materialDialog = builder.build()
    val view = materialDialog.customView as View
    hostEdit = view.findViewById(R.id.settings_dialog_host) as EditText
    nameEdit = view.findViewById(R.id.settings_dialog_name) as EditText
    portEdit = view.findViewById(R.id.settings_dialog_port) as EditText

    httpEdit = view.findViewById(R.id.settings_dialog_http) as EditText
    return materialDialog
  }

  override fun onStart() {
    super.onStart()
    nameEdit.setText(currentName)
    hostEdit.setText(currentAddress)

    if (currentHttpPort > 0) {
      httpEdit.setText(String.format("%d", currentHttpPort))
    }
    if (currentPort > 0) {
      portEdit.setText(String.format("%d", currentPort))
    }
  }

  private fun isValid(port: Int): Boolean {
    if (port < MIN_PORT || port > MAX_PORT) {
      val alert = MaterialDialog.Builder(activity)
      alert.title(R.string.alert_invalid_range)
      alert.content(R.string.alert_invalid_port_number)
      alert.positiveText(android.R.string.ok)
      alert.show()
      return false
    } else {
      return true
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val args = arguments
    if (args != null) {
      currentIndex = args.getInt(ID)
      currentPort = args.getInt(PORT)
      currentAddress = args.getString(ADDRESS)
      currentName = args.getString(NAME)
      currentHttpPort = args.getInt(HTTP)
    }
  }

  interface SettingsDialogListener {
    fun onDialogPositiveClick(dialog: SettingsDialogFragment, settings: DeviceSettings)
  }

  companion object {

    const val TAG = "settings_dialog"

    const val MAX_PORT = 65535
    const val MIN_PORT = 1

    const val ID = "index"
    const val PORT = "port"
    const val ADDRESS = "address"
    const val NAME = "name"
    const val HTTP = "http"

    fun newInstance(index: Int): SettingsDialogFragment {
      val fragment = SettingsDialogFragment()
      val args = Bundle()
      args.putInt(ID, index)
      fragment.arguments = args
      return fragment
    }

    fun newInstance(settings: DeviceSettings): SettingsDialogFragment {
      val fragment = SettingsDialogFragment()
      val args = Bundle()
      args.putLong(ID, settings.id)
      args.putString(NAME, settings.name)
      args.putString(ADDRESS, settings.address)
      args.putInt(PORT, settings.port)
      fragment.arguments = args
      return fragment
    }
  }
}
