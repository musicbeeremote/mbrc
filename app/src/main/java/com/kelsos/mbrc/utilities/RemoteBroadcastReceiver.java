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
import com.kelsos.mbrc.controller.RemoteService;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;

public class RemoteBroadcastReceiver extends BroadcastReceiver {
  private SettingsManager settingsManager;
  private RxBus bus;

  @Inject public RemoteBroadcastReceiver(SettingsManager settingsManager, RxBus bus) {
    this.settingsManager = settingsManager;
    this.bus = bus;
  }

  /**
   * Initialized and installs the IntentFilter listening for the SONG_CHANGED
   * intent fired by the ReplyHandler or the PHONE_STATE intent fired by the
   * Android operating system.
   */
  public IntentFilter filter() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    filter.addAction(RemoteViewIntentBuilder.REMOTE_PLAY_PRESSED);
    filter.addAction(RemoteViewIntentBuilder.REMOTE_NEXT_PRESSED);
    filter.addAction(RemoteViewIntentBuilder.REMOTE_CLOSE_PRESSED);
    filter.addAction(RemoteViewIntentBuilder.REMOTE_PREVIOUS_PRESSED);
    filter.addAction(RemoteViewIntentBuilder.CANCELLED_NOTIFICATION);
    return filter;
  }

  @Override public void onReceive(Context context, Intent intent) {
    if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())) {
      Bundle bundle = intent.getExtras();
      if (null == bundle) {
        return;
      }
      String state = bundle.getString(TelephonyManager.EXTRA_STATE);
      if (TelephonyManager.EXTRA_STATE_RINGING.equalsIgnoreCase(state)) {

        switch (settingsManager.getCallAction()) {
          case SettingsManager.PAUSE:
            postAction(new UserAction(Protocol.PlayerPause, true));
            break;
          case SettingsManager.STOP:
            postAction(new UserAction(Protocol.PlayerStop, true));
            break;
          case SettingsManager.NONE:
            break;
          case SettingsManager.REDUCE:
            bus.post(new MessageEvent(ProtocolEventType.ReduceVolume));
            break;
          default:
            break;
        }
      }
    } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
      NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
      if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
        bus.post(new MessageEvent(UserInputEventType.StartConnection));
      } else //noinspection StatementWithEmptyBody
        if (NetworkInfo.State.DISCONNECTING.equals(networkInfo.getState())) {
        }
    } else if (RemoteViewIntentBuilder.REMOTE_PLAY_PRESSED.equals(intent.getAction())) {
      postAction(new UserAction(Protocol.PlayerPlayPause, true));
    } else if (RemoteViewIntentBuilder.REMOTE_NEXT_PRESSED.equals(intent.getAction())) {
      postAction(new UserAction(Protocol.PlayerNext, true));
    } else if (RemoteViewIntentBuilder.REMOTE_CLOSE_PRESSED.equals(intent.getAction())) {
      bus.post(new MessageEvent(UserInputEventType.CancelNotification));
    } else if (RemoteViewIntentBuilder.REMOTE_PREVIOUS_PRESSED.equals(intent.getAction())) {
      postAction(new UserAction(Protocol.PlayerPrevious, true));
    } else if (RemoteViewIntentBuilder.CANCELLED_NOTIFICATION.equals(intent.getAction())) {
      context.stopService(new Intent(context, RemoteService.class));
    }
  }

  private void postAction(UserAction data) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction, data));
  }
}
