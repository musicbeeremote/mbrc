package kelsos.mbremote.Events;

import java.util.EventObject;

public class PlaylistViewEvent extends EventObject {
    private PlaylistViewAction _type;
    private String _data;

    public PlaylistViewEvent(Object source, PlaylistViewAction type) {
        super(source);
        _type = type;
        _data = "";
    }

    public PlaylistViewEvent(Object source, PlaylistViewAction type, String data)
    {
        super(source);
        _type = type;
        _data = data;

    }

    public PlaylistViewAction getType()
    {
        return _type;
    }

    public String getData()
    {
        return _data;
    }

}
