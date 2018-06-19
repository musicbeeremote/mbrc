package com.kelsos.mbrc.ui.navigation.player

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.kelsos.mbrc.content.activestatus.TrackRating
import com.kelsos.mbrc.databinding.DialogRatingBinding
import com.kelsos.mbrc.di.inject
import toothpick.Toothpick
import javax.inject.Inject

class RatingDialogFragment : DialogFragment(), RatingDialogView {

  private lateinit var databinding: DialogRatingBinding
  private lateinit var fm: FragmentManager

  @Inject
  lateinit var presenter: RatingDialogPresenter

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val scope = Toothpick.openScopes(requireActivity().application, this)
    scope.installModules(ratingDialogModule)
    scope.inject(this)
    databinding = DialogRatingBinding.inflate(requireActivity().layoutInflater)
    databinding.presenter = presenter
    return AlertDialog.Builder(requireContext())
      .setView(databinding.root)
      .setOnDismissListener {
        presenter.detach()
      }
      .create()
      .also {
        presenter.attach(this@RatingDialogFragment)
        presenter.loadRating()
      }
  }

  override fun updateRating(rating: TrackRating) {
    databinding.rating = rating
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

  fun show() {
    show(
      fm,
      TAG
    )
  }

  companion object {
    private const val TAG = "com.kelsos.mbrc.ui.dialogs.RATING_DIALOG"
    fun create(activity: AppCompatActivity): RatingDialogFragment {
      return RatingDialogFragment().apply {
        fm = activity.supportFragmentManager
      }
    }
  }
}