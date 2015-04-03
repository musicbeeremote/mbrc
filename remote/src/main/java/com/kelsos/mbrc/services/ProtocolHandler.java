package com.kelsos.mbrc.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.squareup.otto.Bus;
import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import roboguice.util.Ln;

@Singleton public class ProtocolHandler {
  private Bus bus;
  private boolean isHandshakeComplete;
  private ObjectMapper mapper;

  @Inject public ProtocolHandler(Bus bus, ObjectMapper mapper) {
    this.bus = bus;
    this.mapper = mapper;
  }

  public void resetHandshake() {
    isHandshakeComplete = false;
  }

  public void preProcessIncoming(final String incoming) {
    try {
      final String[] replies = incoming.split("\r\n");
      for (String reply : replies) {

        JsonNode node = mapper.readValue(reply, JsonNode.class);
        String context = node.path("context").getTextValue();

        if (context.contains(Protocol.ClientNotAllowed)) {
          bus.post(new MessageEvent(ProtocolEventType.InformClientNotAllowed));
          return;
        }

        if (!isHandshakeComplete) {
          if (context.contains(Protocol.Player)) {
            bus.post(new MessageEvent(ProtocolEventType.InitiateProtocolRequest));
          } else if (context.contains(Protocol.ProtocolTag)) {
            isHandshakeComplete = true;
            bus.post(new MessageEvent(ProtocolEventType.HandshakeComplete, true));
          } else {
            return;
          }
        }

        bus.post(new MessageEvent(context, node.path(Const.DATA)));
      }
    } catch (IOException e) {
      if (BuildConfig.DEBUG) {
        Ln.d("Incoming preprocessor", e);
        Ln.d("While processing: %s", incoming);
      }
    }
  }
}
