package com.kelsos.mbrc.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.SocketEvent;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.others.Const;
import com.kelsos.mbrc.others.DelayTimer;
import com.kelsos.mbrc.others.SettingsManager;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

@Singleton
public class SocketService {

    public static final int MAX_RETRIES = 5;
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
            cThread = new Thread(new socketConnection());
            cThread.start();
            numOfRetries++;
        }
    };

    @Inject
    public SocketService(SettingsManager settingsManager, MainThreadBusWrapper bus, ObjectMapper mapper) {
        this.bus = bus;
        this.settingsManager = settingsManager;
        this.mapper = mapper;

        cTimer = new DelayTimer(2500, timerFinishEvent);
        numOfRetries = 0;
        shouldStop = false;
        SocketManager(SocketAction.START);
    }

    public void SocketManager(SocketAction action) {
        switch (action) {
            case RESET:
                cleanupSocket();
                if (cThread != null) cThread.interrupt();
                cThread = null;
                shouldStop = false;
                numOfRetries = 0;
                cTimer.start();
                break;
            case START:
                if (sIsConnected() || cThreadIsAlive()) return;
                else if (!sIsConnected() && cThreadIsAlive()) {
                    cThread.interrupt();
                    cThread = null;
                }
                cTimer.start();
                break;
            case RETRY:
                cleanupSocket();
                if (cThread != null) cThread.interrupt();
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
        if (!sIsConnected())
            return;
        try {
            if (output != null) {
                output.flush();
                output.close();
                output = null;
            }
            clSocket.close();
            clSocket = null;
        } catch (IOException ignore) {

        }
    }

    private boolean cThreadIsAlive() {
        return cThread != null && cThread.isAlive();
    }

    public void sendData(SocketMessage message) {
        try {
            if (sIsConnected())
                output.println(mapper.writeValueAsString(message) + Const.NEWLINE);
        } catch (Exception ignored) {
        }
    }

    public void informEventBus(final MessageEvent event) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bus.post(event);
            }
        }).start();
    }

    private class socketConnection implements Runnable {
        public void run() {
            SocketAddress socketAddress = settingsManager.getSocketAddress();
            informEventBus(new MessageEvent(SocketEvent.SocketHandshakeUpdate, false));
            if (null == socketAddress) return;
            BufferedReader input;
            try {
                clSocket = new Socket();
                clSocket.connect(socketAddress);
                output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clSocket.getOutputStream()), 4096), true);
                input = new BufferedReader(new InputStreamReader(clSocket.getInputStream()), 4096);

                String socketStatus = String.valueOf(clSocket.isConnected());

                informEventBus(new MessageEvent(SocketEvent.SocketStatusChanged, socketStatus));
                while (clSocket.isConnected()) {
                    try {
                        final String incoming = input.readLine();
                        if (incoming == null) throw new IOException();
                        if (incoming.length() > 0)
                            informEventBus(new MessageEvent(SocketEvent.SocketDataAvailable, incoming));
                    } catch (IOException e) {
                        input.close();
                        clSocket.close();
                        throw e;
                    }
                }
            } catch (SocketTimeoutException e) {
                bus.post(new NotifyUser(R.string.notification_connection_timeout));
            } catch (SocketException e) {
                bus.post(new NotifyUser(e.toString().substring(26)));
            } catch (IOException ignored) {
            } catch (NullPointerException ignored) {
            } finally {
                if (output != null) {
                    output.flush();
                    output.close();
                }
                clSocket = null;

                informEventBus(new MessageEvent(SocketEvent.SocketStatusChanged, false));
                if (numOfRetries < MAX_RETRIES) SocketManager(SocketAction.RETRY);
            }
        }
    }
}
