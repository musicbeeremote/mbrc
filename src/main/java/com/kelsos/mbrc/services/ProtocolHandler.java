package com.kelsos.mbrc.services;

import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.events.ProtocolEvent;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.others.Protocol;
import com.squareup.otto.Bus;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

@Singleton
public class ProtocolHandler
{
	private Bus bus;

	private boolean isHandshakeComplete;
    private ObjectMapper mapper;

	public static double ServerProtocolVersion;

	@Inject
	public ProtocolHandler(Bus bus, ObjectMapper mapper)
	{
		this.bus = bus;
        this.mapper = mapper;
	}

	/**
	 * Returns true if the handshake is complete and false if it is not.
	 * @return the handshake status
	 */
	public boolean getHandshakeComplete()
	{
		return isHandshakeComplete;
	}

    public void resetHandshake() {
        isHandshakeComplete = false;
    }

	/**
	 * Given the socket server's answer this function processes the send data, extracts needed
	 * information and then notifies the interested parts via Intents for the new changes/data.
	 *
	 * @param answer the answer that came from the server
	 */
	public void answerProcessor(String answer)
	{
        Log.d("Protocol", "Processing answer:\t" + answer + '\t' + "aL:\t" + answer.length());
		try
		{
			String[] replies = answer.split("\r\n");
			for (String reply : replies)
			{
				if (replies.length>1)Log.d("Protocol","Processing current:\t" + reply);

                SocketMessage msg = mapper.readValue(reply, SocketMessage.class);

				if (msg.getContext().contains(Protocol.ClientNotAllowed))
				{
					bus.post(new MessageEvent(ProtocolEvent.InformClientNotAllowed));
					return;
				}

				if (!isHandshakeComplete)
				{
					if (msg.getContext().contains(Protocol.Player))
					{
						bus.post(new MessageEvent(ProtocolEvent.InitiateProtocolRequest));
					} else if (msg.getContext().contains(Protocol.Protocol))
					{
						ServerProtocolVersion = Double.parseDouble(msg.getDataString());
						if(ServerProtocolVersion < 2)
						{
							bus.post(new MessageEvent(ProtocolEvent.InformClientPluginOutOfDate));
						}
						isHandshakeComplete = true;
						bus.post(new MessageEvent(ProtocolEvent.HandshakeComplete, true));
					} else
					{
						return;
					}
				}

                bus.post(new MessageEvent(msg.getContext(), msg.getData()));
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}

    }
}
