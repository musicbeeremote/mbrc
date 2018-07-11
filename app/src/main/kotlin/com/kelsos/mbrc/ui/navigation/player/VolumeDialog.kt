package com.kelsos.mbrc.ui.navigation.player

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.DialogVolumeBinding
import com.kelsos.mbrc.extensions.setIcon
import com.kelsos.mbrc.extensions.setStatusColor
import org.koin.androidx.viewmodel.ext.android.viewModel

class VolumeDialog : DialogFragment() {

  private val viewModel: VolumeDialogViewModel by viewModel()

  private var _binding: DialogVolumeBinding? = null
  private val binding get() = _binding!!

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    _binding = DialogVolumeBinding.inflate(requireActivity().layoutInflater)
    return MaterialAlertDialogBuilder(requireContext())
      .setView(binding.root)
      .show()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.volumeDialogVolume.setOnSeekBarChangeListener { volume ->
      viewModel.changeVolume(volume)
    }
    binding.volumeDialogMute.setOnClickListener { viewModel.mute() }

    viewModel.playerStatus.observe(viewLifecycleOwner) { status ->
      binding.volumeDialogMute.setIcon(
        enabled = status.mute,
        onRes = R.drawable.ic_volume_up_black_24dp,
        offRes = R.drawable.ic_volume_off_black_24dp
      )
      binding.volumeDialogMute.setStatusColor(status.mute)
      binding.volumeDialogVolume.progress = if (status.mute) 0 else status.volume
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
