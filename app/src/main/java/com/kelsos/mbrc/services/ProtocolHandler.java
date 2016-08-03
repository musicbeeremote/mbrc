package com.kelsos.mbrc.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.model.MainDataModel;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton
public class ProtocolHandler {
  private RxBus bus;
  private boolean isHandshakeComplete;
  private ObjectMapper mapper;
  private MainDataModel model;

  @Inject
  public ProtocolHandler(RxBus bus, ObjectMapper mapper, MainDataModel model) {
    this.bus = bus;
    this.mapper = mapper;
    this.model = model;
  }

  public void resetHandshake() {
    isHandshakeComplete = false;
  }

  public void preProcessIncoming(final String incoming) {
    try {
      final String[] replies = incoming.split("\r\n");
      for (String reply : replies) {

        Timber.v("message -> %s", reply);

        JsonNode node = mapper.readValue(reply, JsonNode.class);
        String context = node.path("context").textValue();

        if (context.contains(Protocol.ClientNotAllowed)) {
          bus.post(new MessageEvent(ProtocolEventType.InformClientNotAllowed));
          return;
        }

        if (!isHandshakeComplete) {
          if (context.contains(Protocol.Player)) {
            bus.post(new MessageEvent(ProtocolEventType.InitiateProtocolRequest));
          } else if (context.contains(Protocol.ProtocolTag)) {

            double protocolVersion;
            try {
              protocolVersion = Double.parseDouble(node.path(Const.DATA).asText());
            } catch (Exception ignore) {
              protocolVersion = 2.0;
            }

            model.setPluginProtocol(protocolVersion);
            isHandshakeComplete = true;
            bus.post(new MessageEvent(ProtocolEventType.HandshakeComplete, true));
          } else {
            return;
          }
        }

        bus.post(new MessageEvent(context, node.path(Const.DATA)));
      }
    } catch (IOException e) {
      Timber.d(e, "Incoming preprocessor");
      Timber.d("While processing: %s", incoming);
    }
  }
}
