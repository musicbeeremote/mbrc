package com.kelsos.mbrc.events;

import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.enums.ProtocolHandlerEventType;
import com.kelsos.mbrc.interfaces.IEvent;

import java.util.ArrayList;

public class ProtocolDataEvent implements IEvent
{

	private ProtocolHandlerEventType type;
	private String data;
	private ArrayList<MusicTrack> trackList;
    private ArrayList<String> list;

	public ProtocolDataEvent(ProtocolHandlerEventType type, String data)
	{
		this.type = type;
		this.data = data;
		this.trackList = null;
	}

	public ProtocolDataEvent(ProtocolHandlerEventType type, ArrayList<MusicTrack> trackList)
	{
		this.type = type;
		this.data = "";
		this.trackList = trackList;
	}

    public ProtocolDataEvent(ProtocolHandlerEventType type, ArrayList<String> list, String extra){
        this.type =type;
        this.list = list;
        data = extra;
    }

	public ProtocolDataEvent(ProtocolHandlerEventType type)
	{
		this.type = type;
		this.data = "";
		this.trackList = null;
	}

    public ArrayList<String> getList(){
        return list;
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
		return trackList;
	}
}
