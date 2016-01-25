package com.kelsos.mbrc.utilities;

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
import com.squareup.otto.Bus;

import roboguice.receiver.RoboBroadcastReceiver;

public class RemoteBroadcastReceiver extends RoboBroadcastReceiver {
  private SettingsManager settingsManager;
  private Bus bus;
  private Context context;

  @Inject
  public RemoteBroadcastReceiver(SettingsManager settingsManager, Bus bus, Context context) {
    this.settingsManager = settingsManager;
    this.bus = bus;
    this.context = context;
    this.installFilter();
  }

  @Override protected void handleReceive(Context context, Intent intent) {
    if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())) {
      Bundle bundle = intent.getExtras();
      if (null == bundle) {
        return;
      }
      String state = bundle.getString(TelephonyManager.EXTRA_STATE);
      if (TelephonyManager.EXTRA_STATE_RINGING.equalsIgnoreCase(state)
          && settingsManager.isVolumeReducedOnRinging()) {

      }
    } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
      NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
      if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {

        bus.post(MessageEvent.newInstance(UserInputEventType.StartConnection));
      } else //noinspection StatementWithEmptyBody
        if (NetworkInfo.State.DISCONNECTING.equals(networkInfo.getState())) {
        }
    } else if (RemoteViewIntentBuilder.REMOTE_PLAY_PRESSED.equals(intent.getAction())) {

    } else if (RemoteViewIntentBuilder.REMOTE_NEXT_PRESSED.equals(intent.getAction())) {

    } else if (RemoteViewIntentBuilder.REMOTE_CLOSE_PRESSED.equals(intent.getAction())) {

    } else if (RemoteViewIntentBuilder.REMOTE_PREVIOUS_PRESSED.equals(intent.getAction())) {

    }
  }

  /**
   * Initialized and installs the IntentFilter listening for the SONG_CHANGED
   * intent fired by the ReplyHandler or the PHONE_STATE intent fired by the
   * Android operating system.
   */
  private void installFilter() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    filter.addAction(RemoteViewIntentBuilder.REMOTE_PLAY_PRESSED);
    filter.addAction(RemoteViewIntentBuilder.REMOTE_NEXT_PRESSED);
    filter.addAction(RemoteViewIntentBuilder.REMOTE_CLOSE_PRESSED);
    filter.addAction(RemoteViewIntentBuilder.REMOTE_PREVIOUS_PRESSED);
    context.registerReceiver(this, filter);
  }
}
