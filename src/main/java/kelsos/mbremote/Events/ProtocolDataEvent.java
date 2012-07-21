package kelsos.mbremote.Events;

import kelsos.mbremote.Data.MusicTrack;
import kelsos.mbremote.Enumerations.ProtocolDataType;
import kelsos.mbremote.Interfaces.IEvent;

import java.util.ArrayList;

public class ProtocolDataEvent implements IEvent
{

	private ProtocolDataType type;
	private String data;
	private ArrayList<MusicTrack> _trackList;

	public ProtocolDataEvent(ProtocolDataType type, String data)
	{
		this.type = type;
		this.data = data;
		_trackList = null;
	}

	public ProtocolDataEvent(ProtocolDataType type, ArrayList<MusicTrack> trackList)
	{
		this.type = type;
		data = "";
		_trackList = trackList;
	}


	public ProtocolDataType getType()
	{
		return type;
	}

	public String getData()
	{
		return data;
	}

	public ArrayList<MusicTrack> getTrackList()
	{
		return _trackList;
	}
}
