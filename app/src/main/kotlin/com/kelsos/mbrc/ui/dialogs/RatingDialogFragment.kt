package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.RatingBar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.MainDataModel
import com.kelsos.mbrc.events.RatingChanged
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.extensions.fail
import com.kelsos.mbrc.networking.protocol.Protocol
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class RatingDialogFragment : DialogFragment(), RatingBar.OnRatingBarChangeListener {

  @Inject lateinit var bus: RxBus
  @Inject lateinit var model: MainDataModel
  private val ratingBar: RatingBar by bindView(R.id.ratingBar)
  private var rating: Float = 0.toFloat()
  private var scope: Scope? = null
  private lateinit var dialog: AlertDialog

  override fun onCreate(savedInstanceState: Bundle?) {
    val context = activity ?: fail("context was null")
    scope = Toothpick.openScopes(context.application, this)
    super.onCreate(savedInstanceState)
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
    ratingBar.rating = rating
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    rating = model.rating
    val activity = activity ?: fail("no context")
    val builder = AlertDialog.Builder(activity)
    builder.setTitle(R.string.rate_the_playing_track)
    builder.setView(R.layout.ui_dialog_rating)
    dialog = builder.create()
    return dialog
  }

  override fun onRatingChanged(bar: RatingBar?, rating: Float, userInitiated: Boolean) {
    if (userInitiated) {
      bus.post(UserAction(Protocol.NowPlayingRating, rating))
    }
  }

  override fun onStart() {
    super.onStart()
    ratingBar.onRatingBarChangeListener = this
    ratingBar.rating = rating
  }
}
