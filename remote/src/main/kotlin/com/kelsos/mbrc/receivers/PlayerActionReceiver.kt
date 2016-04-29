package com.kelsos.mbrc.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.google.inject.Inject
import com.kelsos.mbrc.annotations.PlayerAction
import com.kelsos.mbrc.interactors.PlayerInteractor
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder
import roboguice.RoboGuice
import rx.schedulers.Schedulers
import timber.log.Timber

class PlayerActionReceiver : BroadcastReceiver() {
  @Inject private lateinit var playerInteractor: PlayerInteractor

  val intentFilter: IntentFilter
    get() {
      val filter = IntentFilter()
      filter.addAction(RemoteViewIntentBuilder.REMOTE_PLAY_PRESSED)
      filter.addAction(RemoteViewIntentBuilder.REMOTE_NEXT_PRESSED)
      filter.addAction(RemoteViewIntentBuilder.REMOTE_CLOSE_PRESSED)
      filter.addAction(RemoteViewIntentBuilder.REMOTE_PREVIOUS_PRESSED)
      return filter
    }

  override fun onReceive(context: Context, intent: Intent) {
    if (playerInteractor == null) {
      RoboGuice.getInjector(context).injectMembers(this)
    }

    Timber.i("Received intent %s", intent.action)

    @PlayerAction.Action var action: String? = null

    if (RemoteViewIntentBuilder.REMOTE_PLAY_PRESSED == intent.action) {
      action = PlayerAction.PLAY_PLAUSE
    } else if (RemoteViewIntentBuilder.REMOTE_NEXT_PRESSED == intent.action) {
      action = PlayerAction.NEXT
    } else if (RemoteViewIntentBuilder.REMOTE_CLOSE_PRESSED == intent.action) {
      // TODO: 3/15/16 Terminate application or close notification
    } else if (RemoteViewIntentBuilder.REMOTE_PREVIOUS_PRESSED == intent.action) {
      action = PlayerAction.PREVIOUS
    }

    if (action != null) {
      perform(action)
    }
  }

  private fun perform(@PlayerAction.Action action: String) {
    playerInteractor!!.performAction(action).subscribeOn(Schedulers.io()).subscribe({ response ->
      Timber.v("Action -> %s",
          response)
    }) { t -> Timber.e(t, "failed") }
  }
}
