package kelsos.mbremote.Controller;

import android.app.Activity;
import com.google.inject.Inject;
import com.google.inject.Singleton;
@Singleton
public class RunningActivityAccessor
{
	private Activity runningActiviy;

	@Inject
	public void register(Activity activity)
	{
		this.runningActiviy = activity;
	}

	public Activity getRunningActivity()
	{
		return this.runningActiviy;
	}

}
