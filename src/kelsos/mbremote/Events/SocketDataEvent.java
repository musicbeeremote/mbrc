package kelsos.mbremote.Events;

import java.util.EventObject;

public class SocketDataEvent extends EventObject {
    private DataType _type;
    private String _data;

    public SocketDataEvent(Object source, DataType type, String data) {
        super(source);
        _type = type;
        _data = data;
    }

    public DataType getType()
    {
        return _type;
    }

    public String getData()
    {
        return _data;
    }
}
