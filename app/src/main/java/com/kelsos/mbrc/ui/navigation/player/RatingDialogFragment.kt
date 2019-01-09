package com.kelsos.mbrc.ui.navigation.player

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.DialogRatingBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RatingDialogFragment : DialogFragment() {

  private var _binding: DialogRatingBinding? = null
  private val binding get() = _binding!!

  private val viewModel: RatingDialogViewModel by viewModel()

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    _binding = DialogRatingBinding.inflate(LayoutInflater.from(requireContext()))
    return MaterialAlertDialogBuilder(requireContext())
      .setTitle(R.string.rate_the_playing_track)
      .setView(binding.root)
      .show()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.ratingBar.setOnRatingBarChangeListener { rating ->
      viewModel.changeRating(rating)
    }

    viewModel.trackRatingLiveDataProvider.observe(viewLifecycleOwner) {
      binding.ratingBar.rating = it.rating
    }
    viewModel.loadRating()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
