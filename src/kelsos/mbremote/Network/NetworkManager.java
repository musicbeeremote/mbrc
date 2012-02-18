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

import kelsos.mbremote.Intents;
import kelsos.mbremote.R;
import kelsos.mbremote.Network.ProtocolHandler.PlayerAction;
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

	private Timer _connectionTimer;
	private ConnectorTimer _ctt;
	private static int _numberOfTries;
	private static final int MAX_RETRIES = 4;
	private static boolean _pollingTimerIsRunning;
	private static boolean _connectionTimerIsRunning;

	private Handler _nmHandler;
	private Socket _cSocket;
	private PrintWriter _output;

	private final IBinder _mBinder = new LocalBinder();

	private Thread _connectionThread;
	private boolean _requestCoverAndInfo;
	private Timer _pollingTimer;
	private PollingTimerTask _ptt;

	private class connectSocket implements Runnable {

		public void run() {
			Log.d("NetworkManager", "connectSocket Running");
			SocketAddress socketAddress = getSocketAddress();
			if (null == socketAddress)
				return;
			if (_connectionTimerIsRunning)
				stopConnectionTimer();
			BufferedReader _input;
			try {
				_cSocket = new Socket();
				_cSocket.connect(socketAddress);
				_output = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(_cSocket.getOutputStream())),
						true);
				_input = new BufferedReader(new InputStreamReader(
						_cSocket.getInputStream()));
				if (!_pollingTimerIsRunning)
					startPollingTimer();
				while (_cSocket.isConnected()) {
					try {
						final String serverAnswer = _input.readLine();
						AnswerHandler.getInstance().answerProcessor(
								serverAnswer);
					} catch (IOException e) {
						stopPollingTimer();
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
			} catch (NullPointerException e) {
				Log.d("NetworkManager", "NullPointer");
			} finally {
				if (_output != null) {
					_output.flush();
					_output.close();
				}
				_cSocket = null;
				_requestCoverAndInfo = true;
				Log.d("NetworkManager", "ListeningThread terminated");
				attemptToStartSocketThread(Input.system);

			}
		}

		private SocketAddress getSocketAddress() {
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			String server_hostname = sharedPreferences.getString(
					getApplicationContext().getString(
							R.string.settings_server_hostname), null);
			String server_port_string = sharedPreferences.getString(
					getApplicationContext().getString(
							R.string.settings_server_port), null);
			if (server_port_string == null || server_port_string.equals("")
					|| server_hostname == null || server_hostname.equals("")) {
				Toast.makeText(getApplicationContext(),
						R.string.network_manager_check_hostname_or_port,
						Toast.LENGTH_SHORT).show();
				return null;
			}
			int server_port = Integer.parseInt(server_port_string);

			return new InetSocketAddress(server_hostname, server_port);
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

	private class ConnectorTimer extends TimerTask {

		@Override
		public void run() {
			startSocketThread();
			_numberOfTries++;
		}

	}

	private final BroadcastReceiver nmBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			if (intent.getAction().equals(Intents.SONG_CHANGED)) {
				coverAndInfoOutdated();
			} else if (intent.getAction().equals(
					TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
				Bundle bundle = intent.getExtras();
				if (null == bundle)
					return;
				String state = bundle.getString(TelephonyManager.EXTRA_STATE);
				if (state
						.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
					if (sharedPreferences.getBoolean(
							getApplicationContext().getString(
									R.string.settings_reduce_volume_on_ring),
							false)) {
						requestAction(
								PlayerAction.Volume,
								Integer.toString((int) (AnswerHandler
										.getInstance().getCurrentVolume() * 0.2)));
					}
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
		installFilter();
		AnswerHandler.getInstance().setContext(getApplicationContext());
		_numberOfTries = 0; // Initialize the connection retry counter.
		_requestCoverAndInfo = true;

	}

	/**
	 * Initialized and installs the IntentFilter listening for the SONG_CHANGED
	 * intent fired by the AnswerHandler or the PHONE_STATE intent fired by the
	 * Android operating system.
	 */
	private void installFilter() {
		IntentFilter _nmFilter = new IntentFilter();
		_nmFilter.addAction(Intents.SONG_CHANGED);
		_nmFilter.addAction("android.intent.action.PHONE_STATE");
		registerReceiver(nmBroadcastReceiver, _nmFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			// On destroy stop the Polling Timer and close the socket.
			_pollingTimer.cancel();
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
		attemptToStartSocketThread(Input.system);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		_nmHandler = new Handler();
		coverAndInfoOutdated();
		return super.onStartCommand(intent, flags, startId);

	}

	public void coverAndInfoOutdated() {
		_requestCoverAndInfo = true;
	}

	public void requestAction(PlayerAction action, String actionContent) {
		this.sendData(ProtocolHandler.getActionString(action, actionContent));
	}

	public void requestAction(PlayerAction action) {
		this.sendData(ProtocolHandler.getActionString(action, ""));
	}

	private void requestUpdate() {
		if (_requestCoverAndInfo) {
			requestAction(PlayerAction.SongCover);
			requestAction(PlayerAction.SongInformation);
			_requestCoverAndInfo = false;
		}
		requestAction(PlayerAction.SongChangedStatus);
		requestAction(PlayerAction.PlayerStatus);
	}

	private void sendData(String data) {
		try {
			if (_cSocket != null && _cSocket.isConnected())
				_output.println(data + "\r\n");
		} catch (Exception ignored) {
		}
	}

	private void startPollingTimer() {
		if (_pollingTimer == null)
			_pollingTimer = new Timer(true);
		if (_ptt == null)
			_ptt = new PollingTimerTask();
		_pollingTimer.schedule(_ptt, 1000, 1000);
		_pollingTimerIsRunning = true;
		Log.d("NewtorkManager", "startPollingTimer();");
	}

	private void stopPollingTimer() {
		_ptt.cancel();
		_ptt = null;
		_pollingTimer.cancel();
		_pollingTimer = null;
		_pollingTimerIsRunning = false;
		Log.d("NetworkManager", "stopPollingTimer();");
	}

	/**
	 * This function starts the Thread that handles the socket connection.
	 */
	private void startSocketThread() {
		if ((_cSocket != null && _cSocket.isConnected())
				|| (_connectionThread != null && _connectionThread.isAlive()))
			return;
		Runnable connect = new connectSocket();
		_connectionThread = new Thread(connect);
		_connectionThread.start();
		Log.d("NetworkManager", "startSocketThread();");
	}

	public void attemptToStartSocketThread(Input input) {
		if (input == Input.user) {
			_numberOfTries = 0;
		}
		if ((_numberOfTries > MAX_RETRIES) && _connectionTimerIsRunning) {
			_connectionTimer.cancel();
			_connectionTimerIsRunning = false;
			Log.d("NetworkManager", "attemptToStartSocketThread() Max Tries");
		} else if ((_numberOfTries < MAX_RETRIES) && !_connectionTimerIsRunning)
			startConnectionTimer();
		Log.d("NetworkManager", "attemptToStartSocketThread() Current: "
				+ _numberOfTries);
	}

	private void startConnectionTimer() {
		if (_connectionTimerIsRunning)
			return;
		if (_connectionTimer == null)
			_connectionTimer = new Timer(true);
		if (_ctt == null)
			_ctt = new ConnectorTimer();
		_connectionTimer.schedule(_ctt, 1000);
		_connectionTimerIsRunning = true;
		Log.d("NetworkManager", "Connection Timer Started");
	}

	private void stopConnectionTimer() {
		_ctt.cancel();
		_ctt = null;
		_connectionTimer.cancel();
		_connectionTimer = null;
		_connectionTimerIsRunning = false;
		Log.d("NetworkManager", "Connection Timer Stopped");
	}

	public enum Input {
		user, system
	}
}
