package com.kelsos.mbrc.net;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.constants.EventType;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.Message;
import com.kelsos.mbrc.utilities.SettingsManager;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ws.WebSocket;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import okio.Buffer;
import okio.BufferedSource;
import roboguice.util.Ln;
import rx.schedulers.Schedulers;

@Singleton public class SocketService implements WebSocketListener {
  private SettingsManager settingsManager;
  private ObjectMapper mapper;
  private OkHttpClient client;
  private boolean shouldStop;
  private Socket clSocket;
  private PrintWriter output;
  private Thread mThread;

  @Inject
  public SocketService(SettingsManager settingsManager, ObjectMapper mapper, OkHttpClient client) {
    this.settingsManager = settingsManager;
    this.mapper = mapper;
    this.client = client;

    shouldStop = false;
    socketManager(SocketAction.START);
    subscribeToEvents();
  }

  private void subscribeToEvents() {
    Events.messages.subscribeOn(Schedulers.io())
        .filter(msg -> EventType.START_CONNECTION.equals(msg.getType()))
        .subscribe(event -> socketManager(SocketAction.START));
  }

  public void socketManager(SocketAction action) {
    switch (action) {
      case RESET:
        reset();
        break;
      case START:
        start();
        break;
      case RETRY:
        retry();
        break;
      case STOP:
        shouldStop = true;
        break;
      default:
        break;
    }
  }

  private void stopThread() {
    if (mThread != null && mThread.isAlive()) {
      mThread.interrupt();
      mThread = null;
    }
  }

  private void retry() {
    cleanupSocket();
    stopThread();
    if (shouldStop) {
      shouldStop = false;
      return;
    }
  }

  private void reset() {
    cleanupSocket();
    stopThread();
    shouldStop = false;
  }

  private void start() {
    if (!sIsConnected()) {
    }
    startWebSocket();
  }

  /**
   * Returns true if the socket is not null and it is connected, false in any other case.
   *
   * @return Boolean
   */
  private boolean sIsConnected() {
    return false;
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

  public void tryProcessIncoming(final String incoming) {
    try {
      processIncoming(incoming);
    } catch (IOException e) {
      if (BuildConfig.DEBUG) {
        Ln.e(e, "Incoming message pre-processor");
      }
    }
  }

  private void startWebSocket() {
    Request request = new Request.Builder().url("ws://development.lan:3000").build();

    WebSocketCall.create(client, request).enqueue(this);
  }

  private void processIncoming(String incoming) throws IOException {
    final String[] replies = incoming.split("\r\n");
    for (String reply : replies) {

      JsonNode node = mapper.readValue(reply, JsonNode.class);
      String context = node.path("message").asText();

      if (context.contains(Notification.CLIENT_NOT_ALLOWED)) {
        return;
      }

      Events.messages.onNext(new Message(context));
    }
  }

  @Override public void onOpen(WebSocket webSocket, Response response) {

  }

  @Override public void onFailure(IOException e, Response response) {

  }

  @Override public void onMessage(BufferedSource payload, WebSocket.PayloadType type)
      throws IOException {

    if (type == WebSocket.PayloadType.TEXT) {
      tryProcessIncoming(payload.readUtf8());
    }
  }

  @Override public void onPong(Buffer payload) {

  }

  @Override public void onClose(int code, String reason) {

  }
}
