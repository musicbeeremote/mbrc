package kelsos.mbremote.Network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

import kelsos.mbremote.Intents;
import kelsos.mbremote.SettingsManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ConnectivityHandler extends Service {

	private Timer _connectionTimer;
	private ConnectorTimer _ctt;
	private static int _numberOfTries;
	private static final int MAX_RETRIES = 4;
	private static boolean _connectionTimerIsRunning;

	private Handler _nmHandler;
	private Socket _cSocket;
	private PrintWriter _output;

	private final IBinder _mBinder = new LocalBinder();

	private Thread _connectionThread;
	private RequestHandler requestHandler;

	public RequestHandler requestHandler() {
		return requestHandler;
	}

	private class connectSocket implements Runnable {

		public void run() {
			Log.d("ConnectivityHandler", "connectSocket Running");
			SettingsManager settingsManager = new SettingsManager(
					getApplicationContext());
			SocketAddress socketAddress = settingsManager.getSocketAddress();
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
				if (!RequestHandler.isPollingTimerRunning())
					requestHandler.startPollingTimer();
				sendConnectionIntent(true);
				while (_cSocket.isConnected()) {
					try {
						final String serverAnswer = _input.readLine();
						ReplyHandler.getInstance()
								.answerProcessor(serverAnswer);
					} catch (IOException e) {
						requestHandler.stopPollingTimer();
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
				Log.e("ConnectivityHandler", "Listening Loop", e);
			} catch (NullPointerException e) {
				Log.d("ConnectivityHandler", "NullPointer");
			} finally {
				if (_output != null) {
					_output.flush();
					_output.close();
				}
				_cSocket = null;

				sendConnectionIntent(false);

				RequestHandler.coverAndInfoOutdated();
				Log.d("ConnectivityHandler", "ListeningThread terminated");
				attemptToStartSocketThread(Input.system);

			}
		}

		/**
		 * 
		 */
		private void sendConnectionIntent(boolean status) {
			Intent connectionIntent = new Intent();
			connectionIntent.setAction(Intents.CONNECTION_STATUS);
			connectionIntent.putExtra("status", status);
			sendBroadcast(connectionIntent);
		}
	}

	public class LocalBinder extends Binder {
		public ConnectivityHandler getService() {
			return ConnectivityHandler.this;
		}
	}

	private class ConnectorTimer extends TimerTask {

		@Override
		public void run() {
			startSocketThread();
			_numberOfTries++;
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return _mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ReplyHandler.getInstance().setContext(getApplicationContext());
		_numberOfTries = 0; // Initialize the connection retry counter.
		requestHandler = new RequestHandler(this);
		RequestHandler.coverAndInfoOutdated();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			// On destroy stop the Polling Timer and close the socket.
			requestHandler.stopPollingTimer();
			_cSocket.close();
		} catch (IOException e) {
			Log.e("Socket Close", "Failure", e);
		}
		_cSocket = null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		attemptToStartSocketThread(Input.system);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		_nmHandler = new Handler();
		RequestHandler.coverAndInfoOutdated();
		return super.onStartCommand(intent, flags, startId);

	}

	protected void sendData(String data) {
		try {
			if (_cSocket != null && _cSocket.isConnected())
				_output.println(data + "\r\n");
		} catch (Exception ignored) {
		}
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
		Log.d("ConnectivityHandler", "startSocketThread();");
	}

	public void attemptToStartSocketThread(Input input) {
		if (input == Input.user) {
			_numberOfTries = 0;
		}
		if ((_numberOfTries > MAX_RETRIES) && _connectionTimerIsRunning) {
			stopConnectionTimer();
			Log.d("ConnectivityHandler",
					"attemptToStartSocketThread() Max Tries");
		} else if ((_numberOfTries < MAX_RETRIES) && !_connectionTimerIsRunning)
			startConnectionTimer();
		// Log.d("ConnectivityHandler", "attemptToStartSocketThread() Current: "
		// + _numberOfTries);
	}

	private void startConnectionTimer() {
		if (_connectionTimerIsRunning)
			return;
		if (_connectionTimer == null)
			_connectionTimer = new Timer(true);
		if (_ctt == null)
			_ctt = new ConnectorTimer();
		_connectionTimerIsRunning = true;
		_connectionTimer.schedule(_ctt, 1000);

		Log.d("ConnectivityHandler", "Connection Timer Started");
	}

	private void stopConnectionTimer() {
		_ctt.cancel();
		_ctt = null;
		_connectionTimer.cancel();
		_connectionTimer = null;
		_connectionTimerIsRunning = false;
		Log.d("ConnectivityHandler", "Connection Timer Stopped");
	}

	public enum Input {
		user, system
	}
}
