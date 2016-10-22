package com.kelsos.mbrc.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import javax.inject.Inject
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.interactors.VolumeInteractor
import com.kelsos.mbrc.utilities.RxBus
import com.kelsos.mbrc.utilities.SettingsManager
import rx.functions.Func1
import timber.log.Timber

class StateBroadcastReceiver : BroadcastReceiver() {
  @Inject private lateinit var settingsManager: SettingsManager
  @Inject private lateinit var bus: RxBus
  @Inject private lateinit var interactor: VolumeInteractor

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

      bus.post(MessageEvent.newInstance(UserInputEventType.StartConnection))
    } else //noinspection StatementWithEmptyBody
      if (NetworkInfo.State.DISCONNECTING == networkInfo.state) {

      }
  }

  private fun onPhoneStateChange(intent: Intent) {
    val bundle = intent.extras ?: return

    val state = bundle.getString(TelephonyManager.EXTRA_STATE)
    if (TelephonyManager.EXTRA_STATE_RINGING.equals(state,
        ignoreCase = true) && settingsManager.isVolumeReducedOnRinging) {
      interactor.getVolume()
          .flatMap<Int>(Func1 {
            interactor.setVolume(Math.round(it!! * 0.2).toInt())
          })
          .subscribe { Timber.v("Volume reducted to %s due to incoming call", it) }
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
