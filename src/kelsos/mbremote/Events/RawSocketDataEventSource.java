package kelsos.mbremote.Events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RawSocketDataEventSource {
    private List<RawSocketDataEventListener> _listeners = new ArrayList<RawSocketDataEventListener>();

    public synchronized void addEventListener(RawSocketDataEventListener listener)
    {
        _listeners.add(listener);
    }

    public synchronized void removeEventListener(RawSocketDataEventListener listener)
    {
        _listeners.remove(listener);
    }
    public synchronized void dispatchEvent(RawSocketDataEvent e) {
        Iterator it = _listeners.iterator();
        while (it.hasNext()) {
            ((RawSocketDataEventListener) it.next()).handleRawSocketData(e);
        }
    }
}



