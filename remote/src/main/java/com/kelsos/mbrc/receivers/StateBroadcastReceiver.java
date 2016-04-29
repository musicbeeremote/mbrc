package com.kelsos.mbrc.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import com.google.inject.Inject;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.interactors.VolumeInteractor;
import com.kelsos.mbrc.utilities.RxBus;
import com.kelsos.mbrc.utilities.SettingsManager;
import timber.log.Timber;

public class StateBroadcastReceiver extends BroadcastReceiver {
  @Inject private SettingsManager settingsManager;
  @Inject private RxBus bus;
  @Inject private VolumeInteractor interactor;

  public StateBroadcastReceiver() {

  }

  @Override public void onReceive(Context context, Intent intent) {
    if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())) {
      onPhoneStateChange(intent);
    } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
      onNetworkStateChange(intent);
    }
  }

  private void onNetworkStateChange(Intent intent) {
    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {

      bus.post(MessageEvent.Companion.newInstance(UserInputEventType.StartConnection));
    } else //noinspection StatementWithEmptyBody
      if (NetworkInfo.State.DISCONNECTING.equals(networkInfo.getState())) {

      }
  }

  private void onPhoneStateChange(Intent intent) {
    Bundle bundle = intent.getExtras();
    if (null == bundle) {
      return;
    }

    String state = bundle.getString(TelephonyManager.EXTRA_STATE);
    if (TelephonyManager.EXTRA_STATE_RINGING.equalsIgnoreCase(state) && settingsManager.isVolumeReducedOnRinging()) {
      interactor.getVolume()
          .flatMap(volume -> interactor.setVolume((int) Math.round(volume * 0.2)))
          .subscribe(vol -> {
            Timber.v("Volume reducted to %s due to incoming call", vol);
          });
    }
  }

  public IntentFilter getIntentFilter() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    return filter;
  }
}
