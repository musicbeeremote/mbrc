package kelsos.mbremote.Network;

import java.util.Timer;
import java.util.TimerTask;

import kelsos.mbremote.*;
import kelsos.mbremote.Network.ProtocolHandler.PlayerAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class RequestHandler {
	private final ConnectivityHandler connectivityHandler;
	private static boolean _isUpdateTimerRunning;
	private static boolean _requestPlayerData;
	private Timer _updateTimer;
	private PollingTimerTask _utt;

	public RequestHandler(ConnectivityHandler connectivityHandler) {
		this.connectivityHandler = connectivityHandler;
		installFilter();

        // Event Listener for the communicator events
        Communicator.getInstance().setUserInterfaceEventsListener(new UserInterfaceEvents() {
            public void onActivityButtonClicked(ClickSource clickSource) {
                switch (clickSource) {

                    case PlayPause:
                        requestAction(PlayerAction.PlayPause);
                        break;
                    case Stop:
                        requestAction(PlayerAction.Stop);
                        break;
                    case Next:
                        requestAction(PlayerAction.Next);
                        break;
                    case Previous:
                        requestAction(PlayerAction.Previous);
                        break;
                    case Repeat:
                        requestAction(PlayerAction.Repeat, Const.TOGGLE);
                        break;
                    case Shuffle:
                        requestAction(PlayerAction.Shuffle, Const.TOGGLE);
                        break;
                    case Scrobble:
                        requestAction(PlayerAction.Scrobble, Const.TOGGLE);
                        break;
                    case Mute:
                        requestAction(PlayerAction.Mute, Const.TOGGLE);
                        break;
                    case Lyrics:
                        requestAction(PlayerAction.Lyrics);
                        break;
                    case Refresh:
                        coverAndInfoOutdated();
                        break;
                    case Playlist:
                        requestAction(PlayerAction.Playlist);
                        break;
                }
            }

            public void onSeekBarChanged(int volume) {
                requestAction(PlayerAction.Volume, Integer.toString(volume));
            }

            public void onPlayNowRequest(String track) {
                requestAction(PlayerAction.PlayNow, track);
            }
        });
	}

	public static boolean isPollingTimerRunning() {
		return _isUpdateTimerRunning;
	}

	public void coverAndInfoOutdated() {
		_requestPlayerData = true;
        if(!_isUpdateTimerRunning)
            startUpdateTimer();
	}

	public void requestAction(ProtocolHandler.PlayerAction action, String actionContent) {
		connectivityHandler.sendData(ProtocolHandler.getActionString(action, actionContent));
	}

	public void requestAction(ProtocolHandler.PlayerAction action) {
		connectivityHandler.sendData(ProtocolHandler.getActionString(action, ""));
	}

    /**
     * Sends request for Player Status, Song Information and Song Cover data.
     */
	void requestPlayerDataUpdate() {
		if (_requestPlayerData) {
			requestAction(PlayerAction.SongCover);
			requestAction(PlayerAction.SongInformation);
			requestAction(PlayerAction.PlayerStatus);
			_requestPlayerData = false;
			stopUpdateTimer();
		}
	}

    /**
     * Schedules an update request that will be send after 2 seconds.
     */
	void startUpdateTimer() {
		if (_updateTimer == null) _updateTimer = new Timer(true);
		if (_utt == null) _utt = new PollingTimerTask();
		_updateTimer.schedule(_utt, 2000);
		_isUpdateTimerRunning = true;
	}

    /**
     * Stops the update request timer and prepares it for reuse.
     */
	void stopUpdateTimer() {
		_utt.cancel();
		_utt = null;
		_updateTimer.cancel();
		_updateTimer = null;
		_isUpdateTimerRunning = false;
		Log.d("ConnectivityHandler", "stopUpdateTimer();");
	}

	private class PollingTimerTask extends TimerTask {
		@Override
		public void run() {
			requestPlayerDataUpdate();
		}
	}

	private final BroadcastReceiver nmBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
				Bundle bundle = intent.getExtras();
				if (null == bundle) return;
				String state = bundle.getString(TelephonyManager.EXTRA_STATE);
				if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
					if (SettingsManager.getInstance().isVolumeReducedOnRinging()) {
						int newVolume = ((int) (ReplyHandler.getInstance().getCurrentVolume() * 0.2));
						requestAction(PlayerAction.Volume, Integer.toString(newVolume));
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
		_nmFilter.addAction(Const.SONG_CHANGED);
		_nmFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		connectivityHandler.getApplicationContext().registerReceiver(nmBroadcastReceiver, _nmFilter);
	}

	protected void finalize() {
		connectivityHandler.getApplicationContext().unregisterReceiver(nmBroadcastReceiver);
	}
}