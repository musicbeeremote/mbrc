package kelsos.mbremote.Events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModelDataEventSource {
    private List<ModelDataEventListener> _listeners = new ArrayList<ModelDataEventListener>();

    public synchronized void addEventListener(ModelDataEventListener listener) {
        _listeners.add(listener);
    }

    public synchronized void removeEventListener(ModelDataEventListener listener) {
        _listeners.remove(listener);
    }

    public synchronized void dispatchEvent(ModelDataEvent e) {
        Iterator it = _listeners.iterator();
        while (it.hasNext()) {
            ((ModelDataEventListener) it.next()).handleModelDataEvent(e);
        }
    }
}
