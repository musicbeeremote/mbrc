package kelsos.mbremote.Events;

import kelsos.mbremote.Enumerations.RawSocketAction;

import java.util.EventObject;

public class RawSocketDataEvent extends EventObject {

	private static final long serialVersionUID = 7113832558918340513L;
	private String _data;
    private RawSocketAction _type;

    public RawSocketDataEvent(Object source, RawSocketAction type, String data) {
        super(source);
        _data = data;
        _type = type;
    }

    public RawSocketAction getType()
    {
        return _type;
    }

    public String getData()
    {
        return _data;
    }
}
