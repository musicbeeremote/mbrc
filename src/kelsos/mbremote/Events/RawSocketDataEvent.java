package kelsos.mbremote.Events;

import java.util.EventObject;

public class RawSocketDataEvent extends EventObject {

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
