package kelsos.mbremote.Events;

import kelsos.mbremote.enums.ModelDataEventType;
import kelsos.mbremote.Interfaces.IEvent;

public class ModelDataEvent implements IEvent
{
	private ModelDataEventType type;
	private String data;
    public ModelDataEvent(ModelDataEventType type) {
        this.type = type;
    }

    public ModelDataEventType getType()
    {
        return type;
    }

	public String getData()
	{
		return this.data;
	}
}
