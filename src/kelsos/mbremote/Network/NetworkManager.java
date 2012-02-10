package kelsos.mbremote.Network;

import android.app.Service;
import android.content.*;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import kelsos.mbremote.R;
import kelsos.mbremote.Network.ProtocolHandler.PlayerAction;

import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkManager extends Service {

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

    private static int _retryCount;
    private static final int _maxRetries = 3;
    private static boolean _timerIsRunning;

    private Handler _nmHandler;
    private Socket _cSocket = new Socket();
    private PrintWriter _output;

    private final IBinder _mBinder = new LocalBinder();

    private Thread _connectionThread;
    private boolean _requestCoverAndInfo;
    private Timer _pollingTimer;

    private final BroadcastReceiver nmBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AnswerHandler.SONG_CHANGED)) {
                _requestCoverAndInfo = true;
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
                        requestVolumeChange((int) (AnswerHandler.getInstance().getCurrentVolume() * 0.2));
                }
            }
        }
    };

    private PollingTimerTask _ptt;

    @Override
    public IBinder onBind(Intent intent) {
        return _mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        installFilter();
        startPollingTimer();
        AnswerHandler.getInstance().setContext(getApplicationContext());
        _retryCount = 0; // Initialize the connection retry counter.
        _requestCoverAndInfo = true;

    }

    /**
     * Initialized and installs the IntentFilter listening for the SONG_CHANGED
     * intent fired by the AnswerHandler or the PHONE_STATE intent fired by the
     * Android operating system.
     */
    private void installFilter() {
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

    public void requestAction(PlayerAction action, String actionContent)
    {
        this.sendData(ProtocolHandler.getActionString(action,actionContent));
    }

    public void requestAction(PlayerAction action)
    {
        this.sendData(ProtocolHandler.getActionString(action,""));
    }

    private void resetPollingTimer() {
        if (!_timerIsRunning) {
            _retryCount = 0;
            _requestCoverAndInfo = true;
            startPollingTimer();
        }
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

    public void requestVolumeChange(int Volume) {
        this.sendData(ProtocolHandler.VOLUME_OPEN + Volume + ProtocolHandler.VOLUME_CLOSED);
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

    private void startPollingTimer() {
        if (_pollingTimer == null)
            _pollingTimer = new Timer(true);
        if (_ptt == null)
            _ptt = new PollingTimerTask();
        _pollingTimer.schedule(_ptt, 1000, 1000);
        _timerIsRunning = true;
    }

    /**
     * This function starts the Thread that handles the socket connection.
     */
    private void startSocketThread() {
        if (_retryCount > _maxRetries) {
            if (_timerIsRunning) {
                _pollingTimer.cancel();
                _timerIsRunning = false;
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
        _retryCount++;
    }
}
