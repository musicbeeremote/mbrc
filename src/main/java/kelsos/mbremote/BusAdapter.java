package kelsos.mbremote;

import com.squareup.otto.ThreadEnforcer;
import kelsos.mbremote.Interfaces.IEvent;

import com.google.inject.Singleton;
import com.squareup.otto.Bus;

@Singleton
public class BusAdapter
{
	private Bus eventBus;

	public BusAdapter()
	{
		this.eventBus = new Bus(ThreadEnforcer.ANY);
	}

	public Bus getEventBus()
	{
		return this.eventBus;
	}
	
	public void dispatchEvent(IEvent e)
	{
		eventBus.post(e);
	}
	
	public void register(Object item)
	{
		eventBus.register(item);
	}
	
	public void unregister(Object item)
	{
		eventBus.unregister(item);
	}


}
