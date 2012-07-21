package kelsos.mbremote.Events;

import kelsos.mbremote.Enumerations.UserAction;
import kelsos.mbremote.Interfaces.IEvent;

public class UserActionEvent implements IEvent{

	private UserAction type;
    private String data;

    public UserActionEvent(UserAction type)
    {

        this.type = type;
    }

    public UserActionEvent(UserAction type, String data)
    {
        this.type = type;
        this.data = data;
    }

    public UserAction getType()
    {
        return type;
    }

    public String getData()
    {
        return data;
    }

}
