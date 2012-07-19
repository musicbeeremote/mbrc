package kelsos.mbremote;

import com.google.inject.Singleton;
import com.squareup.otto.Bus;

@Singleton
public class BusAdapter
{
	private Bus eventBus;

	public BusAdapter()
	{
		this.eventBus = new Bus();
	}

	public Bus getEventBus()
	{
		return this.eventBus;
	}


}
