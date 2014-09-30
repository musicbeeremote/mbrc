package com.kelsos.mbrc.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import com.google.inject.Inject;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.squareup.otto.Bus;
import roboguice.receiver.RoboBroadcastReceiver;

public class RemoteBroadcastReceiver extends RoboBroadcastReceiver {
    private SettingsManager settingsManager;
    private Bus bus;
    private Context context;

    @Inject public RemoteBroadcastReceiver(SettingsManager settingsManager, Bus bus, Context context) {
        this.settingsManager = settingsManager;
        this.bus = bus;
        this.context = context;
        this.installFilter();
    }

    @Override protected void handleReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action == null) {
            return;
        }

        switch (action) {
            case TelephonyManager.ACTION_PHONE_STATE_CHANGED:
                Bundle bundle = intent.getExtras();
                if (null == bundle) {
                    return;
                }
                String state = bundle.getString(TelephonyManager.EXTRA_STATE);
                if (state != null && state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)
                        && settingsManager.isVolumeReducedOnRinging()) {
                    bus.post(new MessageEvent(ProtocolEventType.REDUCE_VOLUME));
                }
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.State niState;
                if (networkInfo != null) {
                    niState = networkInfo.getState();
                    if (niState.equals(NetworkInfo.State.CONNECTED)) {
                        bus.post(new MessageEvent(UserInputEventType.START_CONNECTION));
                    }
                }
                break;
            case NotificationService.NOTIFICATION_PLAY_PRESSED:
//                bus.post(new MessageEvent(ProtocolEventType.USER_ACTION,
//                        new UserAction(Notification.PLAYER_PLAY_PAUSE, true)));
                break;
            case NotificationService.NOTIFICATION_NEXT_PRESSED:
//                bus.post(new MessageEvent(ProtocolEventType.USER_ACTION,
//                        new UserAction(Notification.PLAYER_NEXT, true)));
                break;
            case NotificationService.NOTIFICATION_CLOSE_PRESSED:
                bus.post(new MessageEvent(UserInputEventType.CANCEL_NOTIFICATION));
                break;
            case NotificationService.NOTIFICATION_PREVIOUS_PRESSED:
//                bus.post(new MessageEvent(ProtocolEventType.USER_ACTION,
//                        new UserAction(Notification.PLAYER_PREVIOUS, true)));
                break;
            default:
                break;
        }

    }

    /**
     * Initialized and installs the IntentFilter listening for the SONG_CHANGED
     * intent fired by the ReplyHandler or the PHONE_STATE intent fired by the
     * Android operating system.
     */
    private void installFilter() {
        IntentFilter nmFilter = new IntentFilter();
        nmFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        nmFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        nmFilter.addAction(NotificationService.NOTIFICATION_PLAY_PRESSED);
        nmFilter.addAction(NotificationService.NOTIFICATION_NEXT_PRESSED);
        nmFilter.addAction(NotificationService.NOTIFICATION_CLOSE_PRESSED);
        nmFilter.addAction(NotificationService.NOTIFICATION_PREVIOUS_PRESSED);
        context.registerReceiver(this, nmFilter);
    }
}
