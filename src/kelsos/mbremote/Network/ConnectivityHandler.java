package kelsos.mbremote.Network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import kelsos.mbremote.Messaging.AppNotificationManager;
import kelsos.mbremote.Messaging.Communicator;
import kelsos.mbremote.Messaging.ServerCommunicationEvent;
import kelsos.mbremote.Others.Const;
import kelsos.mbremote.Others.DelayTimer;
import kelsos.mbremote.Others.SettingsManager;
import kelsos.mbremote.Network.Input;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static kelsos.mbremote.Others.DelayTimer.TimerFinishEvent;

public class ConnectivityHandler extends Service {

    private static int _numberOfTries;
    public static final int MAX_RETRIES = 4;

    private Socket _cSocket;
    private PrintWriter _output;

    private final IBinder _mBinder = new LocalBinder();

    private Thread _connectionThread;
    private RequestHandler requestHandler;

    private DelayTimer _connectionTimer;
    private DelayTimer _statusUpdateTimer;


    private class connectSocket implements Runnable {

        public void run() {
            Log.d("ConnectivityHandler", "connectSocket Running");
            SocketAddress socketAddress = SettingsManager.getInstance().getSocketAddress();
            if (null == socketAddress) return;
            BufferedReader _input;
            try {
                _cSocket = new Socket();
                _cSocket.connect(socketAddress);
                _output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_cSocket.getOutputStream())), true);
                _input = new BufferedReader(new InputStreamReader(_cSocket.getInputStream()));
                requestHandler.requestPlayerData();
                sendConnectionIntent(true);

                while (_cSocket.isConnected()) {
                    try {
                        final String serverAnswer = _input.readLine();
                        ReplyHandler.getInstance().answerProcessor(serverAnswer);
                    } catch (IOException e) {
                        _input.close();
                        _cSocket.close();
                        throw e;
                    }
                }
            } catch (SocketTimeoutException e) {
                final String message = "Connection timed out";
                AppNotificationManager.getInstance().showToastMessage(message);
            } catch (SocketException e) {
                final String exceptionMessage = e.toString().substring(26);
                AppNotificationManager.getInstance().showToastMessage(exceptionMessage);
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

                requestHandler.requestPlayerData();
                Log.d("ConnectivityHandler", "ListeningThread terminated");
                attemptToStartSocketThread(Input.system);

            }
        }
    }


    /**
     * Sends a connection intent to the Receivers listening, containing the connection status.
     * @param status
     */
    private void sendConnectionIntent(boolean status) {
        Intent connectionIntent = new Intent();
        connectionIntent.setAction(Const.CONNECTION_STATUS);
        connectionIntent.putExtra(Const.STATUS, status);
        sendBroadcast(connectionIntent);
    }

    public class LocalBinder extends Binder {
        public ConnectivityHandler getService() {
            return ConnectivityHandler.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        sendConnectionIntent(socketExistsAndIsConnected());
        return _mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _connectionTimer = new DelayTimer(1000);
        _statusUpdateTimer = new DelayTimer(2000);
        ReplyHandler.getInstance().setContext(getApplicationContext());

        _numberOfTries = 0; // Initialize the connection retry counter.
        requestHandler = new RequestHandler(this);
        requestHandler.requestPlayerData();
        // Initialize the settings manager context
        SettingsManager.getInstance().setContext(getApplicationContext());

        Communicator.getInstance().setServerCommunicationEventListener(serverCommunicationEvent);
        _connectionTimer.setTimerFinishEventListener(timerFinishEvent);
        _statusUpdateTimer.setTimerFinishEventListener(statusUpdateTimerFinishEvent);
    }

    TimerFinishEvent statusUpdateTimerFinishEvent = new TimerFinishEvent() {
        public void onTimerFinish() {
            sendConnectionIntent(socketExistsAndIsConnected());
        }
    };

    private ServerCommunicationEvent serverCommunicationEvent = new ServerCommunicationEvent() {
        public void onRequestConnect() {
            attemptToStartSocketThread(Input.user);
        }

        public void onRequestConnectionStatus() {
            Log.d("ConIn", String.valueOf(socketExistsAndIsConnected()));
                    _statusUpdateTimer.start();
        }
    };

    private TimerFinishEvent timerFinishEvent = new TimerFinishEvent() {
        public void onTimerFinish() {
            startSocketThread();
            _numberOfTries++;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
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
        return super.onStartCommand(intent, flags, startId);

    }

    protected void sendData(String data) {
        try {
            if (socketExistsAndIsConnected())
                _output.println(data + Const.NEWLINE);
        } catch (Exception ignored) {
        }
    }

    /**
     * This function starts the Thread that handles the socket connection.
     */
    private void startSocketThread() {
        if (socketExistsAndIsConnected() || connectionThreadExistsAndIsAlive())
            return;
        else if (!socketExistsAndIsConnected() && connectionThreadExistsAndIsAlive()) {
            _connectionThread.destroy();
            _connectionThread = null;
        }
        Runnable connect = new connectSocket();
        _connectionThread = new Thread(connect);
        _connectionThread.start();
        Log.d("ConnectivityHandler", "startSocketThread();");
    }

    private boolean connectionThreadExistsAndIsAlive() {
        return _connectionThread != null && _connectionThread.isAlive();
    }

    /**
     * Depending on the user input the function either retries to connect until the MAX_RETRIES number is reached
     * or it resets the number of retries counter and then retries to connect until the MAX_RETRIES number is reached
     *
     * @param input kelsos.mbremote.Network.Input.User resets the counter, kelsos.mbremote.Network.Input.System just tries one more time.
     */
    public void attemptToStartSocketThread(Input input) {
    	if(!isOnline())
    	{
    		AppNotificationManager.getInstance().showToastMessage("Check for Connection");
    		return;
    	}
        if (socketExistsAndIsConnected()) return;
        if (input == Input.user|| input == Input.initialize) {
            _numberOfTries = 0;
            if (_connectionTimer.isRunning()) _connectionTimer.stop();
        }
        if ((_numberOfTries > MAX_RETRIES) && _connectionTimer.isRunning()) {
            _connectionTimer.stop();
        } else if ((_numberOfTries < MAX_RETRIES) && !_connectionTimer.isRunning())
            _connectionTimer.start();
    }

    /**
     * Returns true if the socket is not null and it is connected, false in any other case.
     * @return Boolean
     */
    private boolean socketExistsAndIsConnected() {
        return _cSocket != null && _cSocket.isConnected();
    }

    /**
     * Returns if the device is connected to internet/network
     * @return Boolean online status, true if online false if not.
     */
    private boolean isOnline()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo !=null && networkInfo.isConnected())
            return true;
        return false;
    }


}
