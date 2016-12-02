package com.kelsos.mbrc.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.utilities.SettingsManager
import javax.inject.Inject

class StateBroadcastReceiver : BroadcastReceiver() {
  @Inject lateinit var settingsManager: SettingsManager
  @Inject lateinit var bus: RxBus

  override fun onReceive(context: Context, intent: Intent) {
    if (TelephonyManager.ACTION_PHONE_STATE_CHANGED == intent.action) {
      onPhoneStateChange(intent)
    } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION == intent.action) {
      onNetworkStateChange(intent)
    }
  }

  private fun onNetworkStateChange(intent: Intent) {
    val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
    if (networkInfo.state == NetworkInfo.State.CONNECTED) {

      bus.post(MessageEvent(UserInputEventType.StartConnection))
    } else //noinspection StatementWithEmptyBody
      if (NetworkInfo.State.DISCONNECTING == networkInfo.state) {

      }
  }

  private fun onPhoneStateChange(intent: Intent) {
    val bundle = intent.extras ?: return

    val state = bundle.getString(TelephonyManager.EXTRA_STATE)
    val phoneIsRinging = TelephonyManager.EXTRA_STATE_RINGING.equals(state, ignoreCase = true)

    if (!phoneIsRinging) {
      return
    }

    when (settingsManager.callAction) {
      SettingsManager.PAUSE -> TODO("Pause")
      SettingsManager.STOP -> TODO("STOP")
      SettingsManager.NONE -> { }
      SettingsManager.REDUCE -> TODO("REDUCE")
      else -> {
      }
    }
  }

  val intentFilter: IntentFilter
    get() {
      val filter = IntentFilter()
      filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
      filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
      return filter
    }
}
