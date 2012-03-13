package kelsos.mbremote.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import kelsos.mbremote.Intents;
import kelsos.mbremote.Network.ProtocolHandler.PlayerAction;
import kelsos.mbremote.SettingsManager;

import java.util.Timer;
import java.util.TimerTask;

public class RequestHandler {
    private final ConnectivityHandler connectivityHandler;
    private static boolean _isPollingTimerRunning;
    private static boolean _requestCoverAndInfo;
    private Timer _pollingTimer;
    private PollingTimerTask _ptt;

    public RequestHandler(ConnectivityHandler connectivityHandler) {
        this.connectivityHandler = connectivityHandler;
        installFilter();
    }

    public static boolean isPollingTimerRunning() {
        return _isPollingTimerRunning;
    }

    public static void coverAndInfoOutdated() {
        _requestCoverAndInfo = true;
    }

    public void requestAction(ProtocolHandler.PlayerAction action,
                              String actionContent) {
        connectivityHandler.sendData(ProtocolHandler.getActionString(action,
                actionContent));
    }

    public void requestAction(ProtocolHandler.PlayerAction action) {
        connectivityHandler.sendData(ProtocolHandler
                .getActionString(action, ""));
    }

    void requestUpdate() {
        if (_requestCoverAndInfo) {
            requestAction(PlayerAction.SongCover);
            requestAction(PlayerAction.SongInformation);
            requestAction(PlayerAction.PlayerStatus);
            _requestCoverAndInfo = false;
            stopPollingTimer();
        }
    }

    void startPollingTimer() {
        if (_pollingTimer == null)
            _pollingTimer = new Timer(true);
        if (_ptt == null)
            _ptt = new PollingTimerTask();
        _pollingTimer.schedule(_ptt, 1000);
        _isPollingTimerRunning = true;
        Log.d("ConnectivityHandler", "startPollingTimer();");
    }

    void stopPollingTimer() {
        _ptt.cancel();
        _ptt = null;
        _pollingTimer.cancel();
        _pollingTimer = null;
        _isPollingTimerRunning = false;
        Log.d("ConnectivityHandler", "stopPollingTimer();");
    }

    private class PollingTimerTask extends TimerTask {
        @Override
        public void run() {
            requestUpdate();
        }
    }

    private final BroadcastReceiver nmBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            SettingsManager settingsManager = new SettingsManager(connectivityHandler.getApplicationContext());
            if (intent.getAction().equals(
                    TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                Bundle bundle = intent.getExtras();
                if (null == bundle)
                    return;
                String state = bundle.getString(TelephonyManager.EXTRA_STATE);
                if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
                    if (settingsManager.isVolumeReducedOnRinging()) {
                        String newVolume = Integer.toString((int) (ReplyHandler
                                .getInstance().getCurrentVolume() * 0.2));
                        requestAction(PlayerAction.Volume, newVolume);
                    }
                }
            }
        }
    };

    /**
     * Initialized and installs the IntentFilter listening for the SONG_CHANGED
     * intent fired by the ReplyHandler or the PHONE_STATE intent fired by the
     * Android operating system.
     */
    private void installFilter() {
        IntentFilter _nmFilter = new IntentFilter();
        _nmFilter.addAction(Intents.SONG_CHANGED);
        _nmFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        connectivityHandler.getApplicationContext().registerReceiver(
                nmBroadcastReceiver, _nmFilter);
    }

    protected void finalize() {
        connectivityHandler.getApplicationContext().unregisterReceiver(
                nmBroadcastReceiver);
    }
}