package com.kelsos.mbrc.net;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.SocketEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.util.DelayTimer;
import com.kelsos.mbrc.util.SettingsManager;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import roboguice.util.Ln;
import rx.schedulers.Schedulers;

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
    private SettingsManager settingsManager;
    private ObjectMapper mapper;
    private boolean shouldStop;
    private Socket clSocket;
    private PrintWriter output;
    private DelayTimer cTimer;
    private Thread mThread;

    @Inject public SocketService(SettingsManager settingsManager, ObjectMapper mapper) {
        this.settingsManager = settingsManager;
        this.mapper = mapper;

        cTimer = new DelayTimer(DELAY, () -> {
            mThread = new Thread(new SocketConnection());
            mThread.start();
            numOfRetries++;
        });
        numOfRetries = 0;
        shouldStop = false;
        socketManager(SocketAction.START);
        SubscribeToEvents();
    }


    private void SubscribeToEvents(){
        Events.Messages.subscribeOn(Schedulers.io())
                .filter(msg -> msg.getType().equals(UserInputEventType.START_CONNECTION))
                .subscribe(event -> socketManager(SocketAction.START));
    }

    public void socketManager(SocketAction action) {
        switch (action) {
            case RESET:
                SocketReset();
                break;
            case START:
                SocketStart();
                break;
            case RETRY:
                SocketRetry();
                break;
            case STOP:
                shouldStop = true;
                break;
            default:
                break;
        }
    }

    private void stopThread() {
        if (mThread != null && mThread.isAlive()){
            mThread.interrupt();
            mThread = null;
        }
    }

    private void SocketRetry() {
        cleanupSocket();
        stopThread();
        if (shouldStop) {
            shouldStop = false;
            numOfRetries = 0;
            return;
        }
        cTimer.start();
    }

    private void SocketReset() {
        cleanupSocket();
        stopThread();
        shouldStop = false;
        numOfRetries = 0;
        cTimer.start();
    }

    private void SocketStart() {
        if(!sIsConnected()){
            cTimer.start();
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
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Ln.e(e, "io exception on socket cleanup");
            }
        }
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
                Ln.e(ignored, "socket send data exception");
            }
        }
    }

    public void tryProcessIncoming(final String incoming) {
        try {
            processIncoming(incoming);
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Ln.e(e, "Incoming message pre-processor");
            }
        }

    }

    private void processIncoming(String incoming) throws IOException {
        final String[] replies = incoming.split("\r\n");
        for (String reply : replies) {

            JsonNode node = mapper.readValue(reply, JsonNode.class);
            String context = node.path("message").getTextValue();

            if (context.contains(Notification.CLIENT_NOT_ALLOWED)) {

                return;
            }

            Events.Messages.onNext(new MessageEvent(context));
        }
    }

    private class SocketConnection implements Runnable {
        public void run() {
            Ln.d("Socket Running");
            SocketAddress socketAddress = settingsManager.getSocketAddress();

            if (null == socketAddress) {
                return;
            }
            BufferedReader input;
            try {
                clSocket = new Socket();
                clSocket.connect(socketAddress);
                final OutputStreamWriter out = new OutputStreamWriter(clSocket.getOutputStream());
                final BufferedWriter wr = new BufferedWriter(out, BUFFER_SIZE);
                output = new PrintWriter(wr, true);
                final InputStreamReader in = new InputStreamReader(clSocket.getInputStream());
                input = new BufferedReader(in, BUFFER_SIZE);

                String socketStatus = String.valueOf(clSocket.isConnected());

                Events.Messages.onNext(new MessageEvent(SocketEventType.STATUS_CHANGED, socketStatus));

                while (clSocket.isConnected()) {
                    readFromSocket(input);
                }
            } catch (SocketTimeoutException e) {
                Ln.e(e, "Connection Timeout");
            } catch (SocketException e) {
                Ln.d(e.toString().substring(SUB_START));
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    Ln.e(e, "socket io/null pointer");
                }
            } finally {
                if (output != null) {
                    output.flush();
                    output.close();
                }
                clSocket = null;

                Events.Messages.onNext(new MessageEvent(SocketEventType.STATUS_CHANGED, false));
                if (numOfRetries < MAX_RETRIES) {
                    socketManager(SocketAction.RETRY);
                }
                if (BuildConfig.DEBUG) {
                    Ln.d("socket closed");
                }
            }
        }

        private void readFromSocket(BufferedReader input) throws IOException {
            try {
                final String incoming = input.readLine();
                if (incoming != null && incoming.length() > 0) {
                    tryProcessIncoming(incoming);
                }
            } catch (IOException e) {
                input.close();
                if (clSocket != null) {
                    clSocket.close();
                }
                throw e;
            }
        }
    }
}
