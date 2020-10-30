package com.kelsos.mbrc.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.controller.RemoteService
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import javax.inject.Inject

class RemoteBroadcastReceiver
@Inject
constructor(
  private val settingsManager: SettingsManager,
  private val bus: RxBus
) : BroadcastReceiver() {

  /**
   * Initialized and installs the IntentFilter listening for the SONG_CHANGED
   * intent fired by the ReplyHandler or the PHONE_STATE intent fired by the
   * Android operating system.
   */
  fun filter(): IntentFilter {
    val filter = IntentFilter()
    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
    filter.addAction(RemoteViewIntentBuilder.REMOTE_PLAY_PRESSED)
    filter.addAction(RemoteViewIntentBuilder.REMOTE_NEXT_PRESSED)
    filter.addAction(RemoteViewIntentBuilder.REMOTE_CLOSE_PRESSED)
    filter.addAction(RemoteViewIntentBuilder.REMOTE_PREVIOUS_PRESSED)
    filter.addAction(RemoteViewIntentBuilder.CANCELLED_NOTIFICATION)
    return filter
  }

  override fun onReceive(context: Context, intent: Intent) {
    when (intent.action) {
      RemoteViewIntentBuilder.REMOTE_PLAY_PRESSED -> {
        postAction(UserAction(Protocol.PlayerPlayPause, true))
      }
      RemoteViewIntentBuilder.REMOTE_NEXT_PRESSED -> {
        postAction(UserAction(Protocol.PlayerNext, true))
      }
      RemoteViewIntentBuilder.REMOTE_CLOSE_PRESSED -> {
        bus.post(MessageEvent(UserInputEventType.CancelNotification))
      }
      RemoteViewIntentBuilder.REMOTE_PREVIOUS_PRESSED -> {
        postAction(UserAction(Protocol.PlayerPrevious, true))
      }
      RemoteViewIntentBuilder.CANCELLED_NOTIFICATION -> {
        context.stopService(Intent(context, RemoteService::class.java))
      }
    }
  }

  private fun postAction(data: UserAction) {
    bus.post(MessageEvent(ProtocolEventType.UserAction, data))
  }
}
