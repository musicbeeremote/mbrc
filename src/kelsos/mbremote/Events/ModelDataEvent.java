package kelsos.mbremote.Events;

import java.util.EventObject;

public class ModelDataEvent extends EventObject {
    private ProtocolDataType type;
    public ModelDataEvent(Object source, ProtocolDataType type) {
        super(source);
        this.type = type;
    }

    public ProtocolDataType getType()
    {
        return type;
    }
}
