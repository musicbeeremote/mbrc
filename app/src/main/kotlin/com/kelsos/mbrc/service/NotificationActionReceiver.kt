package com.kelsos.mbrc.service

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import com.kelsos.mbrc.core.networking.protocol.actions.UserAction
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.usecases.UserActionUseCase
import com.kelsos.mbrc.core.networking.protocol.usecases.VolumeModifyUseCase
import com.kelsos.mbrc.core.platform.intents.MediaIntentActions
import com.kelsos.mbrc.feature.settings.data.CallAction
import com.kelsos.mbrc.feature.settings.domain.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/**
 * Handles notification media button actions and phone state changes.
 * Receives broadcast intents for play/pause, next, previous, and close actions
 * from the media notification, as well as incoming call state changes.
 */
class NotificationActionReceiver(
  private val settingsManager: SettingsManager,
  private val userActionUseCase: UserActionUseCase,
  private val volumeModifyUseCase: VolumeModifyUseCase
) : BroadcastReceiver() {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

  /**
   * Creates an IntentFilter for the actions this receiver handles.
   * Includes media button actions and optionally phone state changes
   * based on permissions and user settings.
   */
  fun filter(context: Context): IntentFilter {
    val hasPermission =
      context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) ==
        PackageManager.PERMISSION_GRANTED

    // Get current call action synchronously for filter setup
    val handleCallAction = runBlocking {
      settingsManager.incomingCallActionFlow.first() != CallAction.None
    }

    return IntentFilter().apply {
      if (hasPermission && handleCallAction) {
        addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
      }
      addAction(MediaIntentActions.PLAY_PRESSED)
      addAction(MediaIntentActions.NEXT_PRESSED)
      addAction(MediaIntentActions.CLOSE_PRESSED)
      addAction(MediaIntentActions.PREVIOUS_PRESSED)
      addAction(MediaIntentActions.CANCELLED_NOTIFICATION)
    }
  }

  override fun onReceive(context: Context, intent: Intent) {
    Timber.v("Incoming %s", intent)
    when (intent.action) {
      TelephonyManager.ACTION_PHONE_STATE_CHANGED -> {
        Timber.v("Incoming")
        val bundle = intent.extras ?: return
        val state = bundle.getString(TelephonyManager.EXTRA_STATE)
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state, ignoreCase = true)) {
          handleRinging()
        }
      }

      MediaIntentActions.PLAY_PRESSED -> performAction(Protocol.PlayerPlayPause)

      MediaIntentActions.NEXT_PRESSED -> performAction(Protocol.PlayerNext)

      MediaIntentActions.CLOSE_PRESSED -> stopService(context)

      MediaIntentActions.PREVIOUS_PRESSED -> performAction(Protocol.PlayerPrevious)

      MediaIntentActions.CANCELLED_NOTIFICATION -> stopService(context)
    }
  }

  private fun stopService(context: Context) {
    if (!ServiceState.isStopping) {
      val intent = Intent(context, RemoteService::class.java)
      context.stopService(intent)
    }
  }

  private fun handleRinging() {
    scope.launch {
      val callAction = settingsManager.incomingCallActionFlow.first()
      when (callAction) {
        CallAction.Pause -> performAction(Protocol.PlayerPause)
        CallAction.Stop -> performAction(Protocol.PlayerStop)
        CallAction.Reduce -> volumeModifyUseCase.reduceVolume()
        else -> Timber.v("No call action set, nothing to do.")
      }
    }
  }

  private fun performAction(protocol: Protocol) {
    userActionUseCase.tryPerform(UserAction.create(protocol))
  }
}
