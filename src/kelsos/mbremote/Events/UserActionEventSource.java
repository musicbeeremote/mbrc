package kelsos.mbremote.Events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * *****************
 */
public class UserActionEventSource {
    private List<UserActionEventListener> _listeners = new ArrayList<UserActionEventListener>();

    public synchronized void addEventListener(UserActionEventListener listener) {
        _listeners.add(listener);
    }

    public synchronized void removeEventListener(UserActionEventListener listener) {
        _listeners.remove(listener);
    }

    public synchronized void dispatchEvent(UserActionEvent e) {
        Iterator it = _listeners.iterator();
        while (it.hasNext()) {
            ((UserActionEventListener) it.next()).handleUserActionEvent(e);
        }
    }
}
