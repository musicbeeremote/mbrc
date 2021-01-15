package com.kelsos.mbrc.ui.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kelsos.mbrc.R
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity

class SettingsDialogFragment : DialogFragment() {
  private lateinit var hostEdit: EditText
  private lateinit var nameEdit: EditText
  private lateinit var portEdit: EditText

  private var saveListener: SettingsSaveListener? = null
  private lateinit var settings: ConnectionSettingsEntity
  private var edit: Boolean = false

  private lateinit var fm: FragmentManager

  private fun setConnectionSettings(settings: ConnectionSettingsEntity) {
    this.settings = settings
  }

  @SuppressLint("InflateParams")
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val context = requireContext()
    val title = if (edit) R.string.settings_dialog_edit else R.string.settings_dialog_add
    val positive = if (edit) R.string.settings_dialog_save else R.string.settings_dialog_add
    val view = LayoutInflater.from(context).inflate(R.layout.dialog__settings, null, false)
    hostEdit = view.findViewById(R.id.settings_dialog__hostname_edit)
    portEdit = view.findViewById(R.id.settings_dialog__port_edit)
    nameEdit = view.findViewById(R.id.settings_dialog__name_edit)

    val builder = MaterialAlertDialogBuilder(context)
      .setView(view)
      .setTitle(title)
      .setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .setPositiveButton(positive) { dialog, _ ->
        onPositiveAction(dialog)
      }
    return builder.create()
  }

  private fun onPositiveAction(dialog: DialogInterface) {
    var shouldIClose = true
    val hostname = hostEdit.text.toString()
    val computerName = nameEdit.text.toString()

    if (hostname.isEmpty() || computerName.isEmpty()) {
      shouldIClose = false
    }

    val portText = portEdit.text.toString()

    val portNum = if (TextUtils.isEmpty(portText)) 0 else Integer.parseInt(portText)
    if (isValid(portNum) && shouldIClose) {
      settings.apply {
        name = computerName
        address = hostname
        port = portNum
      }

      saveListener?.onSave(settings)
      dialog.dismiss()
    }
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
    val context = context ?: error("null context")
    MaterialAlertDialogBuilder(context)
      .setTitle(R.string.alert_invalid_range)
      .setMessage(R.string.alert_invalid_port_number)
      .setPositiveButton(android.R.string.ok) { dialogInterface, _ -> dialogInterface.dismiss() }
      .show()
    false
  } else {
    true
  }

  fun show(listener: SettingsSaveListener) {
    this.saveListener = listener
    show(fm, "settings_dialog")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (!edit) {
      settings = ConnectionSettingsEntity()
    }
  }

  interface SettingsSaveListener {
    fun onSave(settings: ConnectionSettingsEntity)
  }

  companion object {

    private const val MAX_PORT = 65535
    private const val MIN_PORT = 1

    fun newInstance(
      settings: ConnectionSettingsEntity,
      fm: FragmentManager
    ): SettingsDialogFragment {

      return SettingsDialogFragment().apply {
        this.fm = fm
        setConnectionSettings(settings)
        edit = true
      }
    }

    fun create(fm: FragmentManager): SettingsDialogFragment {
      return SettingsDialogFragment().apply { this.fm = fm }
    }
  }
}
