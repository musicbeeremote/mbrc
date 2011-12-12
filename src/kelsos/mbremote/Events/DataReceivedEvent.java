package kelsos.mbremote.Events;

import java.util.EventObject;

public class DataReceivedEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _data;
	public DataReceivedEvent(Object source, String data) {
		super(source);
		_data=data;
	}
	public String getData()
	{
		return _data;
	}

}
