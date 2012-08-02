package kelsos.mbremote.Events;

import kelsos.mbremote.enums.SocketServiceEventType;
import kelsos.mbremote.Interfaces.IEvent;

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
