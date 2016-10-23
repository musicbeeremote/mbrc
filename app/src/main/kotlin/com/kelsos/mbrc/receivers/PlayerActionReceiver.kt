package com.kelsos.mbrc.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.kelsos.mbrc.annotations.PlayerAction
import com.kelsos.mbrc.annotations.PlayerAction.Action
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.interactors.PlayerInteractor
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder
import timber.log.Timber
import toothpick.Toothpick
import javax.inject.Inject

class PlayerActionReceiver : BroadcastReceiver() {
  @Inject lateinit var playerInteractor: PlayerInteractor

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
    val scope = Toothpick.openScope(context.applicationContext)
    Toothpick.inject(this, scope)

    Timber.i("Received intent %s", intent.action)

    @Action var action: String? = null

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

  private fun perform(@Action action: String) {
    playerInteractor.performAction(action).io().subscribe({
      Timber.v("Action -> %s", it)
    }) { Timber.e(it, "failed") }
  }
}
