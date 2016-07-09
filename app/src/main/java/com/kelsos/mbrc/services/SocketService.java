package com.kelsos.mbrc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.SocketEventType;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.utilities.SettingsManager;
import com.kelsos.mbrc.utilities.SocketActivityChecker;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rx.Completable;
import rx.Subscription;
import timber.log.Timber;

@Singleton public class SocketService implements SocketActivityChecker.PingTimeoutListener {
  private static final int DELAY = 3;
  @Inject private SocketActivityChecker activityChecker;

  private static final int MAX_RETRIES = 3;
  private static final int SOCKET_BUFFER = 4096;
  private int numOfRetries;
  private RxBus bus;
  private SettingsManager settingsManager;
  private ObjectMapper mapper;
  private boolean shouldStop;
  private Socket socket;
  private PrintWriter output;
  private ExecutorService executor = Executors.newSingleThreadExecutor();

  private Subscription subscription;

  @Inject public SocketService(SettingsManager settingsManager, RxBus bus,
      ObjectMapper mapper) {
    this.bus = bus;
    this.settingsManager = settingsManager;
    this.mapper = mapper;

    startSocket();
    numOfRetries = 0;
    shouldStop = false;
    socketManager(SocketAction.START);
  }

  private void startSocket() {
    if (subscription != null && !subscription.isUnsubscribed()) {
      Timber.v("A subscription is already active");
      return;
    }

    subscription = Completable.timer(DELAY, TimeUnit.SECONDS).subscribe(throwable -> {
      Timber.v(throwable, "Failed");
    }, () -> {
      executor.execute(new SocketConnection());
      numOfRetries++;
    });
  }

  public void socketManager(SocketAction action) {
    switch (action) {
      case RESET:
        startSocket();
        cleanupSocket();
        shouldStop = false;
        numOfRetries = 0;
        break;
      case START:
        startSocket();
        if (sIsConnected()) {
          return;
        }
        break;
      case RETRY:
        startSocket();
        cleanupSocket();

        if (shouldStop) {
          shouldStop = false;
          numOfRetries = 0;
          return;
        }
        break;
      case STOP:
        shouldStop = true;
        break;
      case TERMINATE:
        if (subscription != null && !subscription.isUnsubscribed()) {
          subscription.unsubscribe();
        }
        shouldStop = true;
        cleanupSocket();
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

  public synchronized void sendData(SocketMessage message) {
    try {
      if (sIsConnected()) {
        output.print(mapper.writeValueAsString(message) + Const.NEWLINE);
        if (output.checkError()) {
          throw new Exception("Check error");
        }
      }
    } catch (Exception ignored) {
      Timber.d(ignored, "Trying to send a message");
    }
  }

  @Override public void onTimeout() {
    Timber.v("Timeout received resetting socket");
    socketManager(SocketAction.RESET);
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
        activityChecker.start();
        activityChecker.setPingTimeoutListener(SocketService.this);
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
        Timber.d(npe, "NPE");
      } finally {
        if (output != null) {
          output.close();
        }

        activityChecker.stop();
        activityChecker.setPingTimeoutListener(null);

        socket = null;

        bus.post(new MessageEvent(SocketEventType.SocketStatusChanged, false));
        if (numOfRetries < MAX_RETRIES) {
          Timber.d("Trying to reconnect. Try %d of %d", numOfRetries, MAX_RETRIES);
          socketManager(SocketAction.RETRY);
        }
        Timber.d("Socket closed");
      }
    }
  }
}
