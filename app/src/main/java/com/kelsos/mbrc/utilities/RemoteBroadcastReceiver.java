package com.kelsos.mbrc.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import com.google.inject.Inject;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.squareup.otto.Bus;

public class RemoteBroadcastReceiver extends BroadcastReceiver {
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

  @Override public void onReceive(Context context, Intent intent) {
    if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())) {
      Bundle bundle = intent.getExtras();
      if (null == bundle) {
        return;
      }
      String state = bundle.getString(TelephonyManager.EXTRA_STATE);
      if (TelephonyManager.EXTRA_STATE_RINGING.equalsIgnoreCase(state)
          && settingsManager.isVolumeReducedOnRinging()) {
        bus.post(new MessageEvent(ProtocolEventType.ReduceVolume));
      }
    } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
      NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
      if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
        bus.post(new MessageEvent(UserInputEventType.StartConnection));
      } else //noinspection StatementWithEmptyBody
        if (NetworkInfo.State.DISCONNECTING.equals(networkInfo.getState())) {
        }
    } else if (RemoteViewIntentBuilder.REMOTE_PLAY_PRESSED.equals(intent.getAction())) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction,
          new UserAction(Protocol.PlayerPlayPause, true)));
    } else if (RemoteViewIntentBuilder.REMOTE_NEXT_PRESSED.equals(intent.getAction())) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction,
          new UserAction(Protocol.PlayerNext, true)));
    } else if (RemoteViewIntentBuilder.REMOTE_CLOSE_PRESSED.equals(intent.getAction())) {
      bus.post(new MessageEvent(UserInputEventType.CancelNotification));
    } else if (RemoteViewIntentBuilder.REMOTE_PREVIOUS_PRESSED.equals(intent.getAction())) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction,
          new UserAction(Protocol.PlayerPrevious, true)));
    }
  }
}
