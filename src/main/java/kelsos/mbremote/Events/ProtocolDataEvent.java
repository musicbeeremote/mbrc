package kelsos.mbremote.Events;

import kelsos.mbremote.Data.MusicTrack;
import kelsos.mbremote.Enumerations.ProtocolHandlerEventType;
import kelsos.mbremote.Interfaces.IEvent;

import java.util.ArrayList;

public class ProtocolDataEvent implements IEvent
{

	private ProtocolHandlerEventType type;
	private String data;
	private ArrayList<MusicTrack> _trackList;

	public ProtocolDataEvent(ProtocolHandlerEventType type, String data)
	{
		this.type = type;
		this.data = data;
		_trackList = null;
	}

	public ProtocolDataEvent(ProtocolHandlerEventType type, ArrayList<MusicTrack> trackList)
	{
		this.type = type;
		data = "";
		_trackList = trackList;
	}


	public ProtocolHandlerEventType getType()
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
