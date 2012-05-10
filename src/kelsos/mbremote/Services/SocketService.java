package kelsos.mbremote.Services;

import android.util.Log;
import kelsos.mbremote.Events.SocketDataEventListener;
import kelsos.mbremote.Messaging.NotificationService;
import kelsos.mbremote.Network.Input;
import kelsos.mbremote.Others.Const;
import kelsos.mbremote.Others.DelayTimer;
import kelsos.mbremote.Others.SettingsManager;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class SocketService {

    private static int _numberOfTries;
    public static final int MAX_RETRIES = 4;
    private ProtocolHandler protocolHandler;
    private static SocketService _instance;

    private Socket _cSocket;

    private PrintWriter _output;

    private Thread _connectionThread;

    private DelayTimer _connectionTimer;
    private DelayTimer _statusUpdateTimer;

    private SocketService()
    {
        _instance = this;
        _connectionTimer = new DelayTimer(1000);
        _statusUpdateTimer = new DelayTimer(2000);

        _numberOfTries = 0; // Initialize the connection retry counter.

        _connectionTimer.setTimerFinishEventListener(timerFinishEvent);
        _statusUpdateTimer.setTimerFinishEventListener(statusUpdateTimerFinishEvent);

        protocolHandler = new ProtocolHandler();
    }

    DelayTimer.TimerFinishEvent statusUpdateTimerFinishEvent = new DelayTimer.TimerFinishEvent() {
        public void onTimerFinish() {
            protocolHandler.updateConnectionStatus(String.valueOf(socketExistsAndIsConnected()));
        }
    };

    private DelayTimer.TimerFinishEvent timerFinishEvent = new DelayTimer.TimerFinishEvent() {
        public void onTimerFinish() {
            startSocketThread();
            _numberOfTries++;
        }
    };

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
        Runnable socketConnection = new socketConnection();
        _connectionThread = new Thread(socketConnection);
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

    protected void sendData(String data) {
        try {
            if (socketExistsAndIsConnected())
                _output.println(data + Const.NEWLINE);
        } catch (Exception ignored) {
        }
    }

    private class socketConnection implements Runnable {

        public void run() {
            protocolHandler.setHandshakeComplete(false);
            Log.d("ConnectivityHandler", "connectSocket Running");
            SocketAddress socketAddress = SettingsManager.getInstance().getSocketAddress();
            if (null == socketAddress) return;
            BufferedReader _input;
            try {
                _cSocket = new Socket();
                _cSocket.connect(socketAddress);
                _output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_cSocket.getOutputStream())), true);
                _input = new BufferedReader(new InputStreamReader(_cSocket.getInputStream()));

                protocolHandler.updateConnectionStatus("true");
                _statusUpdateTimer.start();
                while (_cSocket.isConnected()) {
                    try {
                        final String incoming = _input.readLine();
                        protocolHandler.answerProcessor(incoming);
                    } catch (IOException e) {
                        _input.close();
                        _cSocket.close();
                        throw e;
                    }
                }
            } catch (SocketTimeoutException e) {
                final String message = "Connection timed out";
                NotificationService.getInstance().showToastMessage(message);
            } catch (SocketException e) {
                final String exceptionMessage = e.toString().substring(26);
                NotificationService.getInstance().showToastMessage(exceptionMessage);
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

                protocolHandler.updateConnectionStatus("false");

                Log.d("ConnectivityHandler", "ListeningThread terminated");
                attemptToStartSocketThread(Input.system);

            }
        }
    }

    public void addEventListener(SocketDataEventListener listener)
    {
        protocolHandler.addEventListener(listener);
    }

    public void removeEventListener(SocketDataEventListener listener)
    {
        protocolHandler.removeEventListener(listener);
    }


    /**
     * Returns the instance of the Singleton ReplyHandler if the instance exists,
     * or creates the instance and then it returns it when it does not exist.
     *
     * @return ReplyHandler Singleton Instance
     */
    public static synchronized SocketService getInstance() {
        if (_instance == null) {
            _instance = new SocketService();
        }
        return _instance;
    }



}
