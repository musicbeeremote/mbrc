package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.RatingBar
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import javax.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.events.ui.RatingChanged
import com.kelsos.mbrc.interactors.TrackRatingInteractor
import com.kelsos.mbrc.utilities.RxBus
import timber.log.Timber
import toothpick.Toothpick

class RatingDialogFragment : DialogFragment() {

  @BindView(R.id.ratingBar) internal lateinit var ratingBar: RatingBar
  @Inject lateinit var bus: RxBus
  @Inject lateinit var ratingInteractor: TrackRatingInteractor
  private var mRating: Float = 0.toFloat()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val scope = Toothpick.openScopes(activity.application, this)
    Toothpick.inject(this, scope)
    bus.registerOnMain(this, RatingChanged::class.java, { this.handleRatingChange(it) })
  }

  override fun onDestroy() {
    bus.unregister(this)
    super.onDestroy()
  }

  fun handleRatingChange(event: RatingChanged) {
    mRating = event.rating
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = MaterialDialog.Builder(activity)
    builder.customView(R.layout.ui_dialog_rating, false)

    val dialog = builder.build()
    ButterKnife.bind(this, dialog.customView!!)

    ratingBar.setOnRatingBarChangeListener { ratingBar, ratingValue, isUserInitiated ->
      if (isUserInitiated) {
        ratingInteractor.updateRating(ratingValue).subscribe({ Timber.v("su") }) { }
      }
    }
    ratingBar.rating = mRating

    return dialog
  }
}
