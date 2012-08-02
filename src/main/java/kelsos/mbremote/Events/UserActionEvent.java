package kelsos.mbremote.Events;

import kelsos.mbremote.enums.UserInputEventType;
import kelsos.mbremote.Interfaces.IEvent;

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
