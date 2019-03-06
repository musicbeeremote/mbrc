package com.kelsos.mbrc.ui.navigation.player

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.kelsos.mbrc.databinding.DialogVolumeBinding
import org.koin.android.ext.android.inject

class VolumeDialog : DialogFragment() {

  private val viewModel: VolumeDialogViewModel by inject()

  private lateinit var databinding: DialogVolumeBinding
  private lateinit var fm: FragmentManager

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    databinding = DialogVolumeBinding.inflate(requireActivity().layoutInflater)
    databinding.viewModel = viewModel
    return AlertDialog.Builder(requireContext())
      .setView(databinding.root)
      .setOnDismissListener {
      }
      .create().also {
        viewModel.playerStatus.observe(this@VolumeDialog) {
          databinding.status = it
        }
      }
  }

  fun show() {
    show(fm, TAG)
  }

  companion object {
    private const val TAG = "net.kelsos.mbrc.VOLUME_DIALOG"
    fun create(activity: FragmentActivity): VolumeDialog {
      return VolumeDialog().apply {
        fm = activity.supportFragmentManager
      }
    }
  }
}