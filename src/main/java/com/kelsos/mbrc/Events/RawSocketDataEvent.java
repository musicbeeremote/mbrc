package com.kelsos.mbrc.events;

import com.kelsos.mbrc.enums.SocketServiceEventType;
import com.kelsos.mbrc.interfaces.IEvent;

public class RawSocketDataEvent implements IEvent
{

	private String data;
    private SocketServiceEventType type;

    public RawSocketDataEvent(SocketServiceEventType type, String data) {
        this.data = data;
        this.type = type;
    }

    public SocketServiceEventType getType()
    {
        return type;
    }

    public String getData()
    {
        return data;
    }
}
