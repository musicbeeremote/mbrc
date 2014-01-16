package com.kelsos.mbrc.net;

import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.squareup.otto.Bus;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

@Singleton
public class ProtocolHandler {
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

    /**
     * Pre-processes the socket messages. Responsible for the handshake procedure.
     * @param incoming string containing the message received.
     */
    public void preProcessIncoming(final String incoming) {
        try {
            final String[] replies = incoming.split("\r\n");
            for (String reply : replies) {

                JsonNode node = mapper.readValue(reply, JsonNode.class);
                String context = node.path("context").getTextValue();

                if (context.contains(Protocol.CLIENT_NOT_ALLOWED)) {
                    bus.post(new MessageEvent(ProtocolEventType.InformClientNotAllowed));
                    return;
                }

                if (!isHandshakeComplete) {
                    if (context.contains(Protocol.PLAYER)) {
                        bus.post(new MessageEvent(ProtocolEventType.InitiateProtocolRequest));
                    } else if (context.contains(Protocol.PROTOCOL)) {
//                        node.path("data").getDoubleValue();
                        isHandshakeComplete = true;
                        bus.post(new MessageEvent(ProtocolEventType.HandshakeComplete, true));
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
