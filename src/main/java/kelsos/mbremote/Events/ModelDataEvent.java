package kelsos.mbremote.Events;

import kelsos.mbremote.Enumerations.ProtocolHandlerEventType;
import kelsos.mbremote.Interfaces.IEvent;

public class ModelDataEvent implements IEvent
{
	private ProtocolHandlerEventType type;
	private String data;
    public ModelDataEvent(ProtocolHandlerEventType type) {
        this.type = type;
    }

    public ProtocolHandlerEventType getType()
    {
        return type;
    }

	public String getData()
	{
		return this.data;
	}
}
