package kelsos.mbremote.Events;

import java.util.EventObject;

public class ModelDataEvent extends EventObject {
    private DataType type;
    public ModelDataEvent(Object source, DataType type) {
        super(source);
        this.type = type;
    }

    public DataType getType()
    {
        return type;
    }
}
