package kelsos.mbremote.Network;


import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import kelsos.mbremote.Messaging.ClickSource;
import kelsos.mbremote.Messaging.Communicator;
import kelsos.mbremote.Messaging.UserInterfaceEvent;
import kelsos.mbremote.Network.ProtocolHandler.PlayerAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import kelsos.mbremote.Others.Const;
import kelsos.mbremote.Others.DelayTimer;
import kelsos.mbremote.Others.SettingsManager;

import static kelsos.mbremote.Others.DelayTimer.TimerFinishEvent;

public class RequestHandler {
	private final ConnectivityHandler connectivityHandler;
    private DelayTimer _updateTimer;

	public RequestHandler(ConnectivityHandler connectivityHandler) {
		this.connectivityHandler = connectivityHandler;
		installFilter();
        _updateTimer = new DelayTimer(2000);
        // Event Listener for the communicator events
        Communicator.getInstance().setUserInterfaceEventsListener(userInterfaceEvent);
        _updateTimer.setTimerFinishEventListener(timerFinishEvent);
	}

    private UserInterfaceEvent userInterfaceEvent =  new UserInterfaceEvent() {
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
                    requestPlayerData();
                    break;
                case Playlist:
                    requestAction(PlayerAction.Playlist);
                    break;
                case Initialize:
                    connectivityHandler.attemptToStartSocketThread(Input.initialize);
                    break;
            }
        }

        public void onSeekBarChanged(int volume) {
            requestAction(PlayerAction.Volume, Integer.toString(volume));
        }

        public void onPlayNowRequest(String track) {
            requestAction(PlayerAction.PlayNow, track);
        }
    };

    private TimerFinishEvent timerFinishEvent = new TimerFinishEvent() {

        public void onTimerFinish() {
            requestAction(PlayerAction.SongCover);
            requestAction(PlayerAction.SongInformation);
            requestAction(PlayerAction.PlayerStatus);
        }
    };

	public void requestPlayerData() {
        if(!_updateTimer.isRunning())
            _updateTimer.start();
	}

	public void requestAction(ProtocolHandler.PlayerAction action, String actionContent) {
		connectivityHandler.sendData(ProtocolHandler.getActionString(action, actionContent));
	}

	public void requestAction(ProtocolHandler.PlayerAction action) {
		connectivityHandler.sendData(ProtocolHandler.getActionString(action, ""));
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
            else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
            {
                NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
                {
                     connectivityHandler.attemptToStartSocketThread(Input.user);
                }
                else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTING))
                {
                    
                }
            }
            else if(intent.getAction().equals(Const.CONNECTION_STATUS))
            {
                boolean status = intent.getBooleanExtra(Const.STATUS, false);
                if(status)
                {
                    requestAction(PlayerAction.Player);
                    requestAction(PlayerAction.Protocol);
                    requestPlayerData();
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
        _nmFilter.addAction(Const.CONNECTION_STATUS);
		_nmFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        _nmFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		connectivityHandler.getApplicationContext().registerReceiver(nmBroadcastReceiver, _nmFilter);
	}

	protected void finalize() {
		connectivityHandler.getApplicationContext().unregisterReceiver(nmBroadcastReceiver);
	}
}