package com.kelsos.mbrc.events;

import com.kelsos.mbrc.enums.ModelDataEventType;
import com.kelsos.mbrc.interfaces.IEvent;

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
