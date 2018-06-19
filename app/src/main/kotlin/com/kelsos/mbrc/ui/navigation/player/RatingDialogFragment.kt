package com.kelsos.mbrc.ui.navigation.player

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.TrackRating
import com.kelsos.mbrc.databinding.DialogRatingBinding
import toothpick.Toothpick
import javax.inject.Inject

class RatingDialogFragment : DialogFragment(), RatingDialogView {

  @Inject
  lateinit var presenter: RatingDialogPresenter

  private var _binding: DialogRatingBinding? = null
  private val binding get() = _binding!!

  override fun onCreate(savedInstanceState: Bundle?) {
    val scope = Toothpick.openScopes(requireActivity().application, this)
    scope.installModules(ratingDialogModule)
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    _binding = DialogRatingBinding.inflate(LayoutInflater.from(requireContext()))
    val dialog = MaterialAlertDialogBuilder(requireContext())
      .setTitle(R.string.rate_the_playing_track)
      .setView(binding.root)
      .show()

    binding.ratingBar.setOnRatingBarChangeListener { rating ->
      presenter.changeRating(rating)
    }

    presenter.loadRating()
    return dialog
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun updateRating(rating: TrackRating) {
    binding.ratingBar.rating = rating.rating
  }
}
