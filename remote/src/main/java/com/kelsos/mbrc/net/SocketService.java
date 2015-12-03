package com.kelsos.mbrc.net;

import android.text.TextUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.domain.ConnectionSettings;
import com.kelsos.mbrc.dto.WebSocketMessage;
import com.kelsos.mbrc.utilities.MainThreadBus;
import com.kelsos.mbrc.utilities.SettingsManager;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.ws.WebSocket;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import okio.Buffer;
import roboguice.util.Ln;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

@Singleton public class SocketService implements WebSocketListener {
  private SettingsManager settingsManager;
  private ObjectMapper mapper;
  private OkHttpClient client;
  private boolean connected;
  private final PublishSubject<String> messagePublisher;
  private Executor executor = Executors.newSingleThreadExecutor();
  private Subscription subscription;
  @Inject private MainThreadBus bus;

  @Inject
  public SocketService(SettingsManager settingsManager, ObjectMapper mapper, OkHttpClient client) {
    this.settingsManager = settingsManager;
    this.mapper = mapper;
    this.client = client.clone();
    this.client.interceptors().clear();

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

    Ln.v("[WebSocket] attempting to connect to [%s]", url);
    WebSocketCall.create(client, request).enqueue(this);
  }

  private void processIncoming(String incoming) throws IOException {
    final WebSocketMessage message = mapper.readValue(incoming, WebSocketMessage.class);

    if (Notification.CLIENT_NOT_ALLOWED.equals(message.getMessage())) {
      return;
    }

    bus.post(message);
    Ln.v("[Incoming] %s", message);
  }

  @Override public void onOpen(WebSocket webSocket, Response response) {
    this.connected = true;
    String message = "{\"message\":\"connected\"}";
    Send(webSocket, message);

    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }

    subscription = Observable.interval(15, TimeUnit.SECONDS).subscribe(aLong -> {
      try {
        webSocket.sendPing(new Buffer());
        Ln.v("send ping");
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private void Send(WebSocket webSocket, String message) {
    executor.execute(() -> {
      try {
        final MediaType contentType = MediaType.parse("application/json");
        webSocket.sendMessage(RequestBody.create(contentType, message.getBytes()));
      } catch (IOException e) {
        Ln.v(e);
      }
    });
  }

  @Override public void onFailure(IOException e, Response response) {
    this.connected = false;
    Ln.v(e, "[Websocket] io ex");
  }

  @Override public void onMessage(ResponseBody responseBody) throws IOException {
    messagePublisher.onNext(responseBody.string());

  }

  @Override public void onPong(Buffer payload) {
    Ln.v("pong");
  }

  @Override public void onClose(int code, String reason) {
    this.connected = false;
    subscription.unsubscribe();
    Ln.v("[Websocket] closing (%d) %s", code, reason);
  }
}
