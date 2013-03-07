package com.kelsos.mbrc.events;

import com.kelsos.mbrc.interfaces.IEvent;

public class MessageEvent implements IEvent{

	private String type;
    private Object data;

    public MessageEvent(String type)
    {
        this.type = type;
    }

    public MessageEvent(String type, Object data)
    {
        this.type = type;
        this.data = data;
    }


    public String getType()
    {
        return type;
    }

    public Object getData()
    {
        return data;
    }

    public String getDataString() {
        String result = null;
        if (data.getClass() == String.class) {
            result = (String)data;
        }
        return result;
    }

}
