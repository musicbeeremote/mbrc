package com.kelsos.mbrc.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.data.PageRange;
import com.kelsos.mbrc.data.ProtocolPayload;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.utilities.SettingsManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

class ServiceBase {
  @Inject ObjectMapper mapper;
  @Inject SettingsManager settings;
  private Socket socket;

  @NonNull
  private byte[] getMessage(SocketMessage message) throws JsonProcessingException {
    return (mapper.writeValueAsString(message) + "\r\n").getBytes();
  }

  @NonNull
  Observable<SocketMessage> request(String request, Object data) {
    return Observable.using(this::getSocket, this::getObservable, this::cleanup)
        .flatMap(s -> getSocketMessageObservable(request, data, s))
        .skipWhile(this::shouldSkip);
  }

  @NonNull
  private Observable<SocketMessage> getSocketMessageObservable(String request, Object data, String s) {
    return Observable.create((Subscriber<? super SocketMessage> subscriber) -> {
      try {
        SocketMessage message = mapper.readValue(s, SocketMessage.class);
        String context = message.getContext();

        if (Protocol.Player.equals(context)) {
          final ProtocolPayload payload = new ProtocolPayload();
          payload.setNoBroadcast(true);
          payload.setProtocolVersion(Protocol.ProtocolVersionNumber);
          sendMessage(SocketMessage.create(Protocol.ProtocolTag, payload));
        } else if (Protocol.ProtocolTag.equals(context)) {
          sendMessage(SocketMessage.create(request, data));
        }

        message.setData(mapper.writeValueAsString(message.getData()));

        subscriber.onNext(message);
        subscriber.onCompleted();
      } catch (IOException e) {
        subscriber.onError(e);
      }
    });
  }

  private boolean shouldSkip(SocketMessage ms) {
    return Protocol.Player.equals(ms.getContext()) || Protocol.ProtocolTag.equals(ms.getContext());
  }

  private void sendMessage(SocketMessage socketMessage) throws IOException {
    socket.getOutputStream().write(getMessage(socketMessage));
  }

  private void cleanup(Socket socket) {
    Timber.v("Cleaning auxiliary socket");
    if (!socket.isClosed()) {
      try {
        socket.close();
      } catch (IOException ex) {
        Timber.v(ex, "Failed to clause the auxiliary socket");
      }
    }
  }

  @NonNull
  private Observable<? extends String> getObservable(Socket socket) {
    try {
      final InputStreamReader in = new InputStreamReader(socket.getInputStream(), Const.UTF_8);
      final BufferedReader bufferedReader = new BufferedReader(in);
      return Observable.create(new OnSubscribeReader(bufferedReader));
    } catch (IOException ex) {
      return Observable.error(ex);
    }
  }

  private Socket getSocket() {
    try {
      Timber.v("Creating new socket");
      socket = new Socket();
      socket.setSoTimeout(40 * 1000);
      socket.connect(settings.getSocketAddress());
      sendMessage(SocketMessage.create(Protocol.Player, "Android"));
      return socket;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Nullable
  PageRange getPageRange(int offset, int limit) {
    PageRange range = null;

    if (limit > 0) {
      range = new PageRange();
      range.setOffset(offset);
      range.setLimit(limit);
    }
    return range;
  }
}
