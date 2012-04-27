package kelsos.mbremote.Events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewModelDataEventSource {
    private List _listeners = new ArrayList();

    public synchronized void addEventListener(NewModelDataEventListener listener) {
        _listeners.add(listener);
    }

    public synchronized void removeEventListener(NewModelDataEventListener listener) {
        _listeners.remove(listener);
    }

    public synchronized void fireEvent(NewModelDataEvent e) {
        int listenersSize = _listeners.size();
        Iterator it = _listeners.iterator();
        while (it.hasNext()) {
            ((NewModelDataEventListener) it.next()).handleNewModelDataEvent(e);
        }
    }
}
