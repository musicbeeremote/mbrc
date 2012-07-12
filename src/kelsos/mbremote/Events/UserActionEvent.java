package kelsos.mbremote.Events;

import kelsos.mbremote.Enumerations.UserAction;

import java.util.EventObject;

public class UserActionEvent extends EventObject{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1563748901468970084L;
	private UserAction _userAction;
    private String _eventData;

    public UserActionEvent(Object source, UserAction action)
    {
        super(source);
        _userAction = action;
    }

    public UserActionEvent(Object source, UserAction action, String eventData)
    {
        super(source);
        _userAction = action;
        _eventData = eventData;
    }

    public UserAction getUserAction()
    {
        return _userAction;
    }

    public String getEventData()
    {
        return _eventData;
    }

}
