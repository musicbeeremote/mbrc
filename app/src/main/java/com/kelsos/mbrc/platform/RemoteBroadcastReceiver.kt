package com.kelsos.mbrc.platform

import android.Manifest.permission.READ_PHONE_STATE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.VolumeModifyUseCase
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder
import com.kelsos.mbrc.preferences.CallAction
import com.kelsos.mbrc.preferences.SettingsManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RemoteBroadcastReceiver : BroadcastReceiver(), KoinComponent {

  private val settingsManager: SettingsManager by inject()
  private val volumeModifyUseCase: VolumeModifyUseCase by inject()
  private val userActionUseCase: UserActionUseCase by inject()

  /**
   * Initialized and installs the IntentFilter listening for the SONG_CHANGED
   * intent fired by the ReplyHandler or the PHONE_STATE intent fired by the
   * Android operating system.
   */
  fun filter(context: Context): IntentFilter {
    val hasPermission =
      context.checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
    val handleCallAction = settingsManager.getCallAction() != CallAction.None

    return IntentFilter().apply {
      if (hasPermission && handleCallAction) {
        addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
      }
      addAction(RemoteViewIntentBuilder.PLAY_PRESSED)
      addAction(RemoteViewIntentBuilder.NEXT_PRESSED)
      addAction(RemoteViewIntentBuilder.CLOSE_PRESSED)
      addAction(RemoteViewIntentBuilder.PREVIOUS_PRESSED)
      addAction(RemoteViewIntentBuilder.CANCELLED_NOTIFICATION)
    }
  }

  override fun onReceive(context: Context, intent: Intent) {
    when (intent.action) {
      TelephonyManager.ACTION_PHONE_STATE_CHANGED -> onPhoneStateChange(intent)
      RemoteViewIntentBuilder.PLAY_PRESSED -> performAction(Protocol.PlayerPlayPause)
      RemoteViewIntentBuilder.NEXT_PRESSED -> performAction(Protocol.PlayerNext)
      RemoteViewIntentBuilder.CLOSE_PRESSED -> context.stopService()
      RemoteViewIntentBuilder.PREVIOUS_PRESSED -> performAction(Protocol.PlayerPrevious)
      RemoteViewIntentBuilder.CANCELLED_NOTIFICATION -> context.stopService()
    }
  }

  private fun Context.stopService() {
    stopService(Intent(this, RemoteService::class.java))
  }

  private fun onPhoneStateChange(intent: Intent) {
    val bundle = intent.extras ?: return
    val state = bundle.getString(TelephonyManager.EXTRA_STATE) ?: return
    if (!TelephonyManager.EXTRA_STATE_RINGING.equals(state, ignoreCase = true)) return

    when (settingsManager.getCallAction()) {
      CallAction.Pause -> performAction(Protocol.PlayerPause)
      CallAction.Stop -> performAction(Protocol.PlayerStop)
      CallAction.Reduce -> volumeModifyUseCase.reduceVolume()
      else -> {}
    }
  }

  private fun performAction(protocol: Protocol) {
    userActionUseCase.perform(UserAction.create(protocol, true))
  }
}
