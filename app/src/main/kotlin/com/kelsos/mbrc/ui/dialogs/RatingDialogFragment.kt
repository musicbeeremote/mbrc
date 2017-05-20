package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.RatingBar
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.kelsos.mbrc.R
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.RatingChanged
import com.kelsos.mbrc.model.MainDataModel
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class RatingDialogFragment : DialogFragment() {

  @Inject lateinit var bus: RxBus
  @Inject lateinit var model: MainDataModel
  private lateinit var ratingBar: RatingBar
  private var rating: Float = 0.toFloat()
  private var scope: Scope? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    scope = Toothpick.openScopes(activity.application, this)
    Toothpick.inject(this, scope)
    bus.register(this, RatingChanged::class.java, { this.handleRatingChange(it) })
  }

  override fun onDestroy() {
    super.onDestroy()
    bus.unregister(this)
    Toothpick.closeScope(this)
  }

  private fun handleRatingChange(event: RatingChanged) {
    rating = event.rating
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    rating = model.rating
    val builder = MaterialDialog.Builder(activity)
    builder.title(R.string.rate_the_playing_track)
    builder.customView(R.layout.ui_dialog_rating, false)

    val dialog = builder.build()

    ratingBar = ButterKnife.findById<RatingBar>(dialog.customView!!, R.id.ratingBar)
    ratingBar.setOnRatingBarChangeListener { _, ratingValue, isUserInitiated ->
      if (isUserInitiated) {
        bus.post(MessageEvent.action(UserAction(Protocol.NowPlayingRating, ratingValue)))
      }
    }
    ratingBar.rating = rating

    return dialog
  }
}
