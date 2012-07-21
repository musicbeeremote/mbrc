package kelsos.mbremote.Events;

import kelsos.mbremote.Enumerations.RawSocketAction;
import kelsos.mbremote.Interfaces.IEvent;

public class RawSocketDataEvent implements IEvent
{

	private String data;
    private RawSocketAction type;

    public RawSocketDataEvent(RawSocketAction type, String data) {
        this.data = data;
        this.type = type;
    }

    public RawSocketAction getType()
    {
        return type;
    }

    public String getData()
    {
        return data;
    }
}
