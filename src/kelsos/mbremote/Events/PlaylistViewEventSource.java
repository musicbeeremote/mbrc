package kelsos.mbremote.Events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlaylistViewEventSource {
    private List<PlaylistViewListener> _listeners = new ArrayList<PlaylistViewListener>();

    public synchronized void addEventListener(PlaylistViewListener listener)
    {
        _listeners.add(listener);
    }

    public synchronized void removeEventListener(PlaylistViewListener listener)
    {
        _listeners.remove(listener);
    }

    public synchronized void dispatchEvent(PlaylistViewEvent e)
    {
        Iterator it = _listeners.iterator();
        while (it.hasNext()){
            ((PlaylistViewListener) it.next()).handlePlaylistViewEvent(e);
        }
    }
}