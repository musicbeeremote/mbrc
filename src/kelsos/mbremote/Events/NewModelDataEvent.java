package kelsos.mbremote.Events;

import java.util.EventObject;

public class NewModelDataEvent extends EventObject {
    private DataType type;
    public NewModelDataEvent(Object source, DataType type) {
        super(source);
        this.type = type;
    }

    public DataType getType()
    {
        return type;
    }
}
