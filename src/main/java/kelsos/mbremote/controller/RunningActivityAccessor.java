package kelsos.mbremote.controller;

import android.app.Activity;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RunningActivityAccessor
{
	@Inject
	public RunningActivityAccessor() {

	}

	private Activity runningActivity;

	public void register(Activity activity)
	{
		this.runningActivity = activity;
	}

	public void unRegister(Activity activity)
	{
		if(this.runningActivity==activity)
		{
			this.runningActivity = null;
		}
	}

	public Activity getRunningActivity()
	{
		return this.runningActivity;
	}

}
