package com.kelsos.mbrc.ui.navigation.player

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.DialogRatingBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RatingDialogFragment : DialogFragment() {

  private var _binding: DialogRatingBinding? = null
  private val binding get() = _binding!!

  private val viewModel: RatingDialogViewModel by viewModel()

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    _binding = DialogRatingBinding.inflate(LayoutInflater.from(requireContext()))
    val dialog = MaterialAlertDialogBuilder(requireContext())
      .setTitle(R.string.rate_the_playing_track)
      .setView(binding.root)
      .show()

    binding.ratingBar.setOnRatingBarChangeListener { rating ->
      viewModel.changeRating(rating)
    }

    lifecycleScope.launch {
      viewModel.trackRating.collect {
        binding.ratingBar.rating = it.rating
      }
    }
    viewModel.loadRating()

    return dialog
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
