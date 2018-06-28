package com.kelsos.mbrc.ui.navigation.player

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.databinding.DialogVolumeBinding
import org.koin.android.ext.android.inject

class VolumeDialog : DialogFragment(), VolumeView {

  private val volumeDialogPresenter: VolumeDialogPresenter by inject()

  private lateinit var databinding: DialogVolumeBinding
  private lateinit var fm: FragmentManager

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    databinding = DialogVolumeBinding.inflate(requireActivity().layoutInflater)
    databinding.presenter = volumeDialogPresenter
    return AlertDialog.Builder(requireContext())
      .setView(databinding.root)
      .setOnDismissListener {
        volumeDialogPresenter.detach()
      }
      .create().also {
        volumeDialogPresenter.attach(this)
      }
  }

  override fun update(playerStatus: PlayerStatusModel) {
    databinding.status = playerStatus
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