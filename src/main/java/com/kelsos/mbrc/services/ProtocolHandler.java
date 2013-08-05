package com.kelsos.mbrc.services;

import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ProtocolEvent;
import com.kelsos.mbrc.others.Protocol;
import com.squareup.otto.Bus;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

@Singleton
public class ProtocolHandler {
    public static double ServerProtocolVersion;
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
                    bus.post(new MessageEvent(ProtocolEvent.InformClientNotAllowed));
                    return;
                }

                if (!isHandshakeComplete) {
                    if (context.contains(Protocol.Player)) {
                        bus.post(new MessageEvent(ProtocolEvent.InitiateProtocolRequest));
                    } else if (context.contains(Protocol.Protocol)) {
                        ServerProtocolVersion = node.path("data").getDoubleValue();
                        isHandshakeComplete = true;
                        bus.post(new MessageEvent(ProtocolEvent.HandshakeComplete, true));
                    } else {
                        return;
                    }
                }

                bus.post(new MessageEvent(context, node.path("data")));
            }

        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.d("mbrc-log", "Incoming message pre-processor", e);

            }
        }

    }
}
