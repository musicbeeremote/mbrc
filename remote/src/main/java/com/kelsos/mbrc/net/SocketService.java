package com.kelsos.mbrc.net;

import android.text.TextUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.annotations.Connection;
import com.kelsos.mbrc.domain.ConnectionSettings;
import com.kelsos.mbrc.dto.WebSocketMessage;
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent;
import com.kelsos.mbrc.utilities.RxBus;
import com.kelsos.mbrc.utilities.SettingsManager;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketCall;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

@Singleton public class SocketService implements WebSocketListener {
  private final PublishSubject<String> messagePublisher;
  private SettingsManager settingsManager;
  private ObjectMapper mapper;
  private OkHttpClient client;
  private boolean connected;
  private Executor executor = Executors.newSingleThreadExecutor();
  private Subscription subscription;
  @Inject private RxBus rxBus;
  private WebSocket webSocket;

  @Inject
  public SocketService(SettingsManager settingsManager, ObjectMapper mapper, OkHttpClient client) {
    this.settingsManager = settingsManager;
    this.mapper = mapper;
    OkHttpClient.Builder newBuilder = client.newBuilder();
    newBuilder.interceptors().clear();
    this.client = newBuilder.build();

    messagePublisher = PublishSubject.create();
    messagePublisher.subscribeOn(Schedulers.io()).subscribe((incoming) -> {
      try {
        processIncoming(incoming);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  public void startWebSocket() {
    if (connected) {
      return;
    }

    ConnectionSettings settings = settingsManager.getDefault();
    if (TextUtils.isEmpty(settings.getAddress()) || settings.getPort() == 0) {
      return;
    }

    String url = String.format("ws://%s:%d", settings.getAddress(), settings.getPort());
    Request request = new Request.Builder().url(url).build();

    Timber.v("[WebSocket] attempting to connect to [%s]", url);
    WebSocketCall.create(client, request).enqueue(this);
  }

  private void processIncoming(String incoming) throws IOException {
    final WebSocketMessage message = mapper.readValue(incoming, WebSocketMessage.class);

    if (Notification.CLIENT_NOT_ALLOWED.equals(message.getMessage())) {
      return;
    }

    rxBus.post(message);
    Timber.v("[Incoming] %s", message);
  }

  @Override public void onOpen(WebSocket webSocket, Response response) {
    this.webSocket = webSocket;
    this.connected = true;
    rxBus.post(ConnectionStatusChangeEvent.create(Connection.ON));
    String message = "{\"message\":\"connected\"}";
    Send(webSocket, message);

    stopPing();
    startPing(webSocket);
  }

  public void startPing(WebSocket webSocket) {
    subscription = Observable.interval(15, TimeUnit.SECONDS).subscribe(aLong -> {
      try {
        webSocket.sendPing(new Buffer());
        Timber.v("send ping");
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  public void stopPing() {
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  private void Send(WebSocket webSocket, String message) {
    executor.execute(() -> {
      try {
        webSocket.sendMessage(RequestBody.create(WebSocket.TEXT, message.getBytes()));
      } catch (IOException e) {
        Timber.e(e, "Failed to send the message");
      }
    });
  }

  @Override public void onFailure(IOException e, Response response) {
    stopPing();
    this.connected = false;
    Timber.e(e, "[Websocket] io ex");
    rxBus.post(ConnectionStatusChangeEvent.create(Connection.OFF));
  }

  @Override public void onMessage(ResponseBody responseBody) throws IOException {
    messagePublisher.onNext(responseBody.string());
  }

  @Override public void onPong(Buffer payload) {
    Timber.v("pong");
  }

  @Override public void onClose(int code, String reason) {
    this.connected = false;
    subscription.unsubscribe();
    webSocket = null;
    Timber.v("[Websocket] closing (%d) %s", code, reason);
    rxBus.post(ConnectionStatusChangeEvent.create(Connection.OFF));
  }

  public void disconnect() {
    stopPing();

    if (webSocket == null) {
      Timber.v("No WebSocket available nothing to do here");
      return;
    }
    try {
      webSocket.close(1000, "Disconnecting");
    } catch (IOException e) {
      Timber.v(e, "While closing the websocket");
    }
  }
}
