package kelsos.mbremote.Events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProtocolDataEventSource {
    private List<ProtocolDataEventListener> _listeners = new ArrayList<ProtocolDataEventListener>();

    public synchronized void addEventListener(ProtocolDataEventListener listener) {
        _listeners.add(listener);
    }

    public synchronized void removeEventListener(ProtocolDataEventListener listener) {
        _listeners.remove(listener);
    }

    public synchronized void dispatchEvent(ProtocolDataEvent e) {
        Iterator it = _listeners.iterator();
        while (it.hasNext()) {
            ((ProtocolDataEventListener) it.next()).handleSocketDataEvent(e);
        }
    }
}
