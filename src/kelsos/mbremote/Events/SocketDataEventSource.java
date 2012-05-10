package kelsos.mbremote.Events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * *****************
 */
public class SocketDataEventSource {
    private List<SocketDataEventListener> _listeners = new ArrayList<SocketDataEventListener>();

    public synchronized void addEventListener(SocketDataEventListener listener) {
        _listeners.add(listener);
    }

    public synchronized void removeEventListener(SocketDataEventListener listener) {
        _listeners.remove(listener);
    }

    public synchronized void dispatchEvent(SocketDataEvent e) {
        Iterator it = _listeners.iterator();
        while (it.hasNext()) {
            ((SocketDataEventListener) it.next()).handleSocketDataEvent(e);
        }
    }
}
