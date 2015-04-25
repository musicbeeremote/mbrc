package com.kelsos.mbrc.services;

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
import com.kelsos.mbrc.utilities.DelayTimer;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.kelsos.mbrc.utilities.SettingsManager;
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
import org.codehaus.jackson.map.ObjectMapper;
import roboguice.util.Ln;

@Singleton public class SocketService {
  public static final int MAX_RETRIES = 3;
  public static final int SOCKET_BUFFER = 4096;
  private int numOfRetries;
  private MainThreadBusWrapper bus;
  private SettingsManager settingsManager;
  private ObjectMapper mapper;
  private boolean shouldStop;
  private Socket socket;
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

  @Inject public SocketService(SettingsManager settingsManager, MainThreadBusWrapper bus,
      ObjectMapper mapper) {
    this.bus = bus;
    this.settingsManager = settingsManager;
    this.mapper = mapper;

    cTimer = new DelayTimer(3, timerFinishEvent);
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
      case TERMINATE:
        shouldStop = true;
        cleanupSocket();
        if (cThreadIsAlive()) {
          cThread.interrupt();
          cThread = null;
        }
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
    return socket != null && socket.isConnected();
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
      socket.close();
      socket = null;
    } catch (IOException ignore) {

    }
  }

  private boolean cThreadIsAlive() {
    return cThread != null && cThread.isAlive();
  }

  public synchronized void sendData(SocketMessage message) {
    try {
      if (sIsConnected()) {
        output.print(mapper.writeValueAsString(message) + Const.NEWLINE);
        if (output.checkError()) {
          throw new Exception("Check error");
        }
      }
    } catch (Exception ignored) {
      if (BuildConfig.DEBUG) {
        Ln.d(ignored, "Trying to send a message");
      }
    }
  }

  private class SocketConnection implements Runnable {
    public void run() {
      SocketAddress socketAddress = settingsManager.getSocketAddress();
      bus.post(new MessageEvent(SocketEventType.SocketHandshakeUpdate, false));
      if (null == socketAddress) {
        return;
      }
      BufferedReader input;
      try {
        socket = new Socket();
        socket.connect(socketAddress);
        final OutputStreamWriter out =
            new OutputStreamWriter(socket.getOutputStream(), Const.UTF_8);
        output = new PrintWriter(new BufferedWriter(out, SOCKET_BUFFER), true);
        final InputStreamReader in = new InputStreamReader(socket.getInputStream(), Const.UTF_8);
        input = new BufferedReader(in, SOCKET_BUFFER);

        String socketStatus = String.valueOf(socket.isConnected());

        bus.post(new MessageEvent(SocketEventType.SocketStatusChanged, socketStatus));
        while (socket.isConnected()) {
          try {
            final String incoming = input.readLine();
            if (incoming == null) {
              throw new IOException();
            }
            if (incoming.length() > 0) {
              bus.post(new MessageEvent(SocketEventType.SocketDataAvailable, incoming));
            }
          } catch (IOException e) {
            input.close();
            if (socket != null) {
              socket.close();
            }
            throw e;
          }
        }
      } catch (SocketTimeoutException e) {
        bus.post(new NotifyUser(R.string.notification_connection_timeout));
      } catch (SocketException e) {
        bus.post(new NotifyUser(e.toString().substring(26)));
      } catch (IOException ignored) {
      } catch (NullPointerException npe) {
        Ln.d(npe);
      } finally {
        if (output != null) {
          output.close();
        }

        socket = null;

        bus.post(new MessageEvent(SocketEventType.SocketStatusChanged, false));
        if (numOfRetries < MAX_RETRIES) {
          Ln.d("Trying to reconnect. Try %d of %d", numOfRetries, MAX_RETRIES);
          socketManager(SocketAction.RETRY);
        }
        if (BuildConfig.DEBUG) {
          Ln.d("socket closed");
        }
      }
    }
  }
}
