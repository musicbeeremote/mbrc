package kelsos.mbremote.Events;

import kelsos.mbremote.Data.MusicTrack;

import java.util.ArrayList;
import java.util.EventObject;

public class SocketDataEvent extends EventObject {
    private DataType _type;
    private String _data;
    private ArrayList<MusicTrack> _trackList;

    public SocketDataEvent(Object source, DataType type, String data) {
        super(source);
        _type = type;
        _data = data;
        _trackList = null;
    }

    public SocketDataEvent(Object source, DataType type, ArrayList<MusicTrack> trackList)
    {
        super(source);
        _type = type;
        _data = "";
        _trackList = trackList;
    }


    public DataType getType()
    {
        return _type;
    }

    public String getData()
    {
        return _data;
    }

    public ArrayList<MusicTrack> getTrackList()
    {
        return _trackList;
    }
}
