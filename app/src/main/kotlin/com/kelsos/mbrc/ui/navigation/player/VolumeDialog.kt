package com.kelsos.mbrc.ui.navigation.player

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.databinding.DialogVolumeBinding
import com.kelsos.mbrc.di.inject
import toothpick.Toothpick
import javax.inject.Inject

class VolumeDialog : DialogFragment(), VolumeView {

  @Inject
  lateinit var volumeDialogPresenter: VolumeDialogPresenter

  private lateinit var databinding: DialogVolumeBinding
  private lateinit var fm: FragmentManager

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val scope = Toothpick.openScopes(requireActivity().application, this)
    scope.installModules(volumeDialogModule)
    scope.inject(this)
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