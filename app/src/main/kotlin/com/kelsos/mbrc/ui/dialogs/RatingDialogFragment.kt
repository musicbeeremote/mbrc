package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kelsos.mbrc.R
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class RatingDialogFragment : DialogFragment() {

  private var ratingBar: RatingBar? = null
  private var rating: Float = 0.toFloat()
  private var scope: Scope? = null

  @Inject
  lateinit var userActionUseCase: UserActionUseCase

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(requireActivity().application, this)
    super.onCreate(savedInstanceState)
    scope = Toothpick.openScopes(requireActivity().application, this)
    Toothpick.inject(this, scope)
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = MaterialAlertDialogBuilder(requireContext())
      .setTitle(R.string.rate_the_playing_track)
      .setView(R.layout.ui_dialog_rating)
      .show()

    ratingBar = dialog.findViewById(R.id.ratingBar)
    ratingBar?.setOnRatingBarChangeListener { _, ratingValue, isUserInitiated ->
      if (isUserInitiated) {
        userActionUseCase.perform(UserAction(Protocol.NowPlayingRating, rating))
      }
    }
    ratingBar?.rating = rating
    return dialog
  }

  fun show() {
    show(supportFragmentManager, TAG)
  }

  private lateinit var supportFragmentManager: FragmentManager

  companion object {
    private const val TAG = "com.kelsos.mbrc.ui.dialogs.RATING_DIALOG"
    fun create(activity: AppCompatActivity): RatingDialogFragment {
      return RatingDialogFragment().apply {
        supportFragmentManager = activity.supportFragmentManager
      }
    }
  }
}
