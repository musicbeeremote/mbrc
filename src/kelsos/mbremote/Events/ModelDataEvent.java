package kelsos.mbremote.Events;

import kelsos.mbremote.Enumerations.ProtocolDataType;

import java.util.EventObject;

public class ModelDataEvent extends EventObject {

	private static final long serialVersionUID = 4575540442030688983L;
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
