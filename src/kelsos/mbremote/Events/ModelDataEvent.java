package kelsos.mbremote.Events;

import kelsos.mbremote.Enumerations.ProtocolDataType;
import kelsos.mbremote.Interfaces.IEvent;

import java.util.EventObject;

public class ModelDataEvent implements IEvent
{
	private ProtocolDataType type;
	private String data;
    public ModelDataEvent(ProtocolDataType type) {
        this.type = type;
    }

    public ProtocolDataType getType()
    {
        return type;
    }

	public String getData()
	{
		return this.data;
	}
}
