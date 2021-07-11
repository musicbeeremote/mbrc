package com.kelsos.mbrc.ui.navigation.player

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.extensions.setIcon
import com.kelsos.mbrc.common.ui.extensions.setStatusColor
import com.kelsos.mbrc.databinding.DialogVolumeBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class VolumeDialog : DialogFragment() {

  private val viewModel: VolumeDialogViewModel by viewModel()

  private var _binding: DialogVolumeBinding? = null
  private val binding get() = _binding!!

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    _binding = DialogVolumeBinding.inflate(requireActivity().layoutInflater)
    val dialog = MaterialAlertDialogBuilder(requireContext())
      .setView(binding.root)
      .show()
    var isFromUser = false
    binding.volumeDialogVolume.setOnSeekBarChangeListener({ fromUser ->
      isFromUser = fromUser
    }) { volume ->
      viewModel.changeVolume(volume)
    }
    binding.volumeDialogMute.setOnClickListener { viewModel.mute() }
    lifecycleScope.launch {
      viewModel.playerStatus.collect { status ->
        binding.volumeDialogMute.setIcon(
          enabled = status.mute,
          onRes = R.drawable.ic_volume_off_black_24dp,
          offRes = R.drawable.ic_volume_up_black_24dp
        )
        binding.volumeDialogMute.setStatusColor(status.mute)
        if (!isFromUser) {
          binding.volumeDialogVolume.progress = if (status.mute) 0 else status.volume
        }
      }
    }
    return dialog
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
