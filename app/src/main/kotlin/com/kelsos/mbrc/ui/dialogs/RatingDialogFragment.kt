package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.RatingBar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.di.scopes
import com.kelsos.mbrc.events.RatingChanged
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class RatingDialogFragment : androidx.fragment.app.DialogFragment(), RatingBar.OnRatingBarChangeListener {

  private val ratingBar: RatingBar by bindView(R.id.ratingBar)
  private var rating: Float = 0.toFloat()
  private var scope: Scope? = null
  private lateinit var dialog: AlertDialog

  @Inject
  lateinit var userActionUseCase: UserActionUseCase

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = scopes(requireActivity().application, this)
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

  private fun handleRatingChange(event: RatingChanged) {
    rating = event.rating
    ratingBar.rating = rating
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val activity = activity ?: error("no context")
    val builder = AlertDialog.Builder(activity)
    builder.setTitle(R.string.rate_the_playing_track)
    builder.setView(R.layout.ui_dialog_rating)
    dialog = builder.create()
    return dialog
  }

  override fun onRatingChanged(bar: RatingBar?, rating: Float, userInitiated: Boolean) {
    if (userInitiated) {
      userActionUseCase.perform(UserAction(Protocol.NowPlayingRating, rating))
    }
  }

  override fun onStart() {
    super.onStart()
    ratingBar.onRatingBarChangeListener = this
    ratingBar.rating = rating
  }

  fun show() {
    show(supportFragmentManager, TAG)
  }

  private lateinit var supportFragmentManager: androidx.fragment.app.FragmentManager

  companion object {
    private const val TAG = "com.kelsos.mbrc.ui.dialogs.RATING_DIALOG"
    fun create(activity: AppCompatActivity): RatingDialogFragment {
      return RatingDialogFragment().apply {
        supportFragmentManager = activity.supportFragmentManager
      }
    }
  }
}