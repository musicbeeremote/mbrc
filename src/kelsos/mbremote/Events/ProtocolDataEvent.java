package kelsos.mbremote.Events;

import kelsos.mbremote.Data.MusicTrack;

import java.util.ArrayList;
import java.util.EventObject;

public class ProtocolDataEvent extends EventObject {
    private ProtocolDataType _type;
    private String _data;
    private ArrayList<MusicTrack> _trackList;

    public ProtocolDataEvent(Object source, ProtocolDataType type, String data) {
        super(source);
        _type = type;
        _data = data;
        _trackList = null;
    }

    public ProtocolDataEvent(Object source, ProtocolDataType type, ArrayList<MusicTrack> trackList)
    {
        super(source);
        _type = type;
        _data = "";
        _trackList = trackList;
    }


    public ProtocolDataType getType()
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
