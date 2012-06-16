package kelsos.mbremote.Events;

import java.util.EventObject;

/**
 * *****************
 */
public interface ProtocolDataEventListener {
    public void handleSocketDataEvent(EventObject eventObject);
}
