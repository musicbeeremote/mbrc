package com.kelsos.mbrc.net;

import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.SocketEventType;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.util.DelayTimer;
import com.kelsos.mbrc.util.MainThreadBusWrapper;
import com.kelsos.mbrc.util.SettingsManager;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

@Singleton
public class SocketService {
    public static final int MAX_RETRIES = 3;
    public static final int BUFFER_SIZE = 4096;
    public static final int DELAY = 3;
    public static final int SUB_START = 26;
    private static int numOfRetries;
    private MainThreadBusWrapper bus;
    private SettingsManager settingsManager;
    private ObjectMapper mapper;
    private boolean shouldStop;
    private Socket clSocket;
    private PrintWriter output;
    private Thread cThread;
    private DelayTimer cTimer;
    private DelayTimer.TimerFinishEvent timerFinishEvent = new DelayTimer.TimerFinishEvent() {
        public void onTimerFinish() {
            cThread = new Thread(new SocketConnection());
            cThread.start();
            numOfRetries++;
        }
    };

    @Inject public SocketService(SettingsManager settingsManager, MainThreadBusWrapper bus, ObjectMapper mapper) {
        this.bus = bus;
        this.settingsManager = settingsManager;
        this.mapper = mapper;

        cTimer = new DelayTimer(DELAY, timerFinishEvent);
        numOfRetries = 0;
        shouldStop = false;
        socketManager(SocketAction.START);
    }

    public void socketManager(SocketAction action) {
        switch (action) {
            case RESET:
                cleanupSocket();
                if (cThread != null) {
                    cThread.interrupt();
                }
                cThread = null;
                shouldStop = false;
                numOfRetries = 0;
                cTimer.start();
                break;
            case START:
                if (sIsConnected() || cThreadIsAlive()) {
                    return;
                } else if (!sIsConnected() && cThreadIsAlive()) {
                    cThread.interrupt();
                    cThread = null;
                }
                cTimer.start();
                break;
            case RETRY:
                cleanupSocket();
                if (cThread != null) {
                    cThread.interrupt();
                }
                cThread = null;
                if (shouldStop) {
                    shouldStop = false;
                    numOfRetries = 0;
                    return;
                }
                cTimer.start();
                break;
            case STOP:
                shouldStop = true;
                break;
            default:
                break;
        }
    }

    /**
     * Returns true if the socket is not null and it is connected, false in any other case.
     *
     * @return Boolean
     */
    private boolean sIsConnected() {
        return clSocket != null && clSocket.isConnected();
    }

    private void cleanupSocket() {
        if (!sIsConnected()) {
            return;
        }
        try {
            if (output != null) {
                output.flush();
                output.close();
                output = null;
            }
            clSocket.close();
            clSocket = null;
        } catch (IOException ignore) { }
    }

    private boolean cThreadIsAlive() {
        return cThread != null && cThread.isAlive();
    }

    public void sendData(SocketMessage message) {
        try {
            if (sIsConnected()) {
                output.print(mapper.writeValueAsString(message) + Const.NEWLINE);
                if (output.checkError()) {
                    throw new Exception("Check error");
                }
            }
        } catch (Exception ignored) {
            if (BuildConfig.DEBUG) {
                Log.d("mbrc-log", "socket send data exception", ignored);
            }
        }
    }

    private class SocketConnection implements Runnable {
        public void run() {
            SocketAddress socketAddress = settingsManager.getSocketAddress();
            bus.post(new MessageEvent(SocketEventType.HANDSHAKE_UPDATE, false));
            if (null == socketAddress) {
                return;
            }
            BufferedReader input;
            try {
                clSocket = new Socket();
                clSocket.connect(socketAddress);
                output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clSocket.getOutputStream()), BUFFER_SIZE), true);
                input = new BufferedReader(new InputStreamReader(clSocket.getInputStream()), BUFFER_SIZE);

                String socketStatus = String.valueOf(clSocket.isConnected());

                bus.post(new MessageEvent(SocketEventType.STATUS_CHANGED, socketStatus));
                while (clSocket.isConnected()) {
                    try {
                        final String incoming = input.readLine();
                        if (incoming != null && incoming.length() > 0) {
                            bus.post(new MessageEvent(SocketEventType.DATA_AVAILABLE, incoming));
                        }
                    } catch (IOException e) {
                        input.close();
                        clSocket.close();
                        throw e;
                    }
                }
            } catch (SocketTimeoutException e) {
                bus.post(new NotifyUser(R.string.notification_connection_timeout));
            } catch (SocketException e) {
                bus.post(new NotifyUser(e.toString().substring(SUB_START)));
            } catch (IOException | NullPointerException ignored) {
            } finally {
                if (output != null) {
                    output.flush();
                    output.close();
                }
                clSocket = null;

                bus.post(new MessageEvent(SocketEventType.STATUS_CHANGED, false));
                if (numOfRetries < MAX_RETRIES) {
                    socketManager(SocketAction.RETRY);
                }
                if (BuildConfig.DEBUG) {
                    Log.d("mbrc-log", "socket closed");
                }
            }
        }
    }
}
