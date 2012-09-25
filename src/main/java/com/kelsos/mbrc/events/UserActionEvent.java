package com.kelsos.mbrc.events;

import com.kelsos.mbrc.enums.UserInputEventType;
import com.kelsos.mbrc.interfaces.IEvent;

public class UserActionEvent implements IEvent{

	private UserInputEventType type;
    private String data;

    public UserActionEvent(UserInputEventType type)
    {

        this.type = type;
    }

    public UserActionEvent(UserInputEventType type, String data)
    {
        this.type = type;
        this.data = data;
    }

    public UserInputEventType getType()
    {
        return type;
    }

    public String getData()
    {
        return data;
    }

}
