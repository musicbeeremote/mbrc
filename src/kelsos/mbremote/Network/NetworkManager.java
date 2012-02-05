package kelsos.mbremote.Network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

import kelsos.mbremote.R;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class NetworkManager extends Service {

	private static int retryCount;
	private static final int maxRetries = 3;

	private class connectSocket implements Runnable {

		public void run() {
			SharedPreferences sPrefs = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			String server_hostname = sPrefs.getString(getApplicationContext()
					.getString(R.string.settings_server_hostname), null);
			int server_port = Integer.parseInt(sPrefs.getString(
					getApplicationContext().getString(
							R.string.settings_server_port), null));
			if (server_hostname == null || server_port == 0) {
				Toast.makeText(getApplicationContext(),
						R.string.network_manager_check_hostname_or_port,
						Toast.LENGTH_SHORT).show();
			}
			SocketAddress socketAddress = new InetSocketAddress(
					server_hostname, server_port);
			BufferedReader _input;
			try {
				_cSocket = new Socket();
				_cSocket.connect(socketAddress);
				_output = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(_cSocket.getOutputStream())),
						true);
				_input = new BufferedReader(new InputStreamReader(
						_cSocket.getInputStream()));
				Log.d("NetworkManager", "Entering listening loop");
				while (_cSocket.isConnected()) {
					try {
						// Log.d("ServerInput", "next stop");
						final String serverAnswer = _input.readLine();
						AnswerHandler.getInstance().answerProcessor(
								serverAnswer);
					} catch (IOException e) {
						_input.close();
						_cSocket.close();
						throw e;
					}
				}
			} catch (SocketTimeoutException e) {
				_nmHandler.post(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(),
								"Connection timed out", Toast.LENGTH_SHORT)
								.show();
					}
				});
			} catch (SocketException e) {
				final String exceptionMessage = e.toString().substring(26);
				_nmHandler.post(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(),
								exceptionMessage, Toast.LENGTH_SHORT).show();
					}
				});
			} catch (IOException e) {
				Log.e("NetworkManager", "Listening Loop", e);
			} finally {
				if (_output != null) {
					_output.flush();
					_output.close();
				}
				_cSocket = null;
				_requestCoverAndInfo = true;
			}

		}
	}

	public class LocalBinder extends Binder {
		public NetworkManager getService() {
			return NetworkManager.this;
		}
	}

	private class PollingTimerTask extends TimerTask {
		@Override
		public void run() {
			requestUpdate();
		}
	}

	private Handler _nmHandler;
	private Socket _cSocket = new Socket();
	private PrintWriter _output;

	private final IBinder _mBinder = new LocalBinder();

	private Thread _connectionThread;
	private boolean _requestCoverAndInfo;
	private static final String STATE = "state";
	private static final String PLAYPAUSE = "<playPause/>";
	private static final String PREVIOUS = "<previous/>";
	private static final String NEXT = "<next/>";
	private static final String STOP = "<stopPlayback/>";
	private static final String PLAYSTATE = "<playState/>";
	private static final String VOLUME_OPEN = "<volume>";
	private static final String VOLUME_CLOSED = "</volume>";
	private static final String SONGCHANGED = "<songChanged/>";
	private static final String SONGINFO = "<songInfo/>";
	private static final String SONGCOVER = "<songCover/>";
	private static final String SHUFFLE_OPEN = "<shuffle>";
	private static final String SHUFFLE_CLOSE = "</shuffle>";
	private static final String MUTE_OPEN = "<mute>";
	private static final String MUTE_CLOSE = "</mute>";
	private static final String REPEAT_OPEN = "<repeat>";
	private static final String REPEAT_CLOSE = "</repeat>";
	private static final String PLAYLIST = "<playlist/>";
	private static final String PLAYNOW_OPEN = "<playNow>";
	private static final String PLAYNOW_CLOSE = "</playNow>";
	private static final String SCROBBLE_OPEN = "<scrobbler>";
	private static final String SCROBBLE_CLOSE = "</scrobbler>";
	private static final String LYRICS = "<lyrics/>";
	private static final String RATING_OPEN = "<rating>";
	private static final String RATING_CLOSE = "</rating>";
	private Timer _pollingTimer;

	private final BroadcastReceiver nmBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(AnswerHandler.SONG_CHANGED)) {
				requestCurrentlyPlayingSongCover();
				requestCurrentlyPlayingSongInfo();
				// Log.d("Intent Received","Cover Requested");

			}
			if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
				Bundle bundle = intent.getExtras();
				if (null == bundle)
					return;
				String state = bundle.getString(TelephonyManager.EXTRA_STATE);
				if (state
						.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
					SharedPreferences sPrefs = PreferenceManager
							.getDefaultSharedPreferences(getApplicationContext());
					if (sPrefs.getBoolean(
							getApplicationContext().getString(
									R.string.settings_reduce_volume_on_ring),
							false))
						requestVolumeChange(20);
				}
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return _mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		retryCount = 0; // Initialize the connection retry counter.
		_requestCoverAndInfo = true;
		AnswerHandler.getInstance().setContext(getApplicationContext());
		_pollingTimer = new Timer(true);
		PollingTimerTask _ptt = new PollingTimerTask();
		_pollingTimer.schedule(_ptt, 1000, 1000);
		IntentFilter _nmFilter = new IntentFilter();
		_nmFilter.addAction(AnswerHandler.SONG_CHANGED);
		_nmFilter.addAction("android.intent.action.PHONE_STATE");

		registerReceiver(nmBroadcastReceiver, _nmFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			_cSocket.close();
		} catch (IOException e) {
			Log.e("Socket Close", "Failure", e);
		}
		_cSocket = null;
		unregisterReceiver(nmBroadcastReceiver);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		startSocketThread();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		_nmHandler = new Handler();
		return super.onStartCommand(intent, flags, startId);

	}

	public void requestCoverAndInfo() {
		_requestCoverAndInfo = true;
	}

	public void requestCurrentlyPlayingSongCover() {
		this.sendData(SONGCOVER);
	}

	public void requestCurrentlyPlayingSongInfo() {
		this.sendData(SONGINFO);
	}

	public void requestMuteState(String action) {
		this.sendData(MUTE_OPEN + action + MUTE_CLOSE);
	}

	public void requestNextTrack() {
		this.sendData(NEXT);
	}

	public void requestNowPlayingList() {
		this.sendData(PLAYLIST);
	}

	public void requestPlaylist() {
		this.sendData(PLAYLIST);
	}

	public void requestPlayPause() {
		this.sendData(PLAYPAUSE);
	}

	public void requestPlaySelectedTrackNow(String selectedTrack) {
		this.sendData(PLAYNOW_OPEN + selectedTrack + PLAYNOW_CLOSE);
	}

	public void requestPlayState() {
		this.sendData(PLAYSTATE);
	}

	public void requestPreviousTrack() {
		this.sendData(PREVIOUS);
	}

	public void requestRepeatState(String action) {
		this.sendData(REPEAT_OPEN + action + REPEAT_CLOSE);
	}

	public void requestScrobblerState(String action) {
		this.sendData(SCROBBLE_OPEN + action + SCROBBLE_CLOSE);
	}

	public void requestShuffleState(String action) {
		this.sendData(SHUFFLE_OPEN + action + SHUFFLE_CLOSE);
	}

	public void requestSongChangedInformation() {
		this.sendData(SONGCHANGED);
	}

	public void requestSpecificTrack(String track) {
		this.sendData(PLAYNOW_OPEN + track + PLAYNOW_CLOSE);
	}

	public void requestStopPlayback() {
		this.sendData(STOP);
	}

	public void requestTrackLyrics() {
		this.sendData(LYRICS);
	}

	public void requestRatingChange(String newRating) {
		this.sendData(RATING_OPEN + newRating + RATING_CLOSE);
	}

	private void requestUpdate() {
		if (_requestCoverAndInfo) {
			requestCurrentlyPlayingSongCover();
			requestCurrentlyPlayingSongInfo();
			_requestCoverAndInfo = false;
		}
		requestSongChangedInformation();
		requestMuteState(STATE);
		requestRepeatState(STATE);
		requestScrobblerState(STATE);
		requestShuffleState(STATE);
		requestPlayState();
		requestVolumeChange(-1);
	}

	public void requestVolumeChange(int Volume) {
		this.sendData(VOLUME_OPEN + Volume + VOLUME_CLOSED);
	}

	private void sendData(String sendData) {
		try {
			if (_cSocket != null && _cSocket.isConnected())
				_output.println(sendData + "\r\n");
			else {
				startSocketThread();
			}
		} catch (Exception e) {
			Log.e("NetworkManager", "sendData", e);
		}
	}

	/**
	 * This function starts the Thread that handles the socket connection.
	 * 
	 */
	private void startSocketThread() {
		if (retryCount > maxRetries) {
			try {
				_pollingTimer.wait(300000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
		if (_cSocket != null && _cSocket.isConnected())
			return;
		if (_connectionThread != null && _connectionThread.isAlive())
			return;
		Runnable connect = new connectSocket();
		_connectionThread = new Thread(connect);
		_connectionThread.start();
		retryCount++;
	}
}
