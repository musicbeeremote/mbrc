package com.kelsos.mbrc.controller;

import android.app.Activity;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.views.LyricsView;
import com.kelsos.mbrc.views.MainView;
import com.kelsos.mbrc.views.PlaylistView;
import com.kelsos.mbrc.configuration.LyricsViewCommandRegistration;
import com.kelsos.mbrc.configuration.MainViewCommandRegistration;
import com.kelsos.mbrc.configuration.PlaylistViewCommandRegistration;

@Singleton
public class RunningActivityAccessor
{
	private Controller controller;

	@Inject
	public RunningActivityAccessor(Controller controller) {
		this.controller = controller;
	}

	private Activity runningActivity;

	public void register(Activity activity)
	{
		if(this.runningActivity == activity) return;
		this.runningActivity = activity;
		CommandRegistrationHandler(activity);
	}

	public void unRegister(Activity activity)
	{
		if(this.runningActivity==activity)
		{
			this.runningActivity = null;
		}
		CommandUnRegistrationHandler(activity);
	}

	public Activity getRunningActivity()
	{
		return this.runningActivity;
	}

	private void CommandRegistrationHandler(Activity activity)
	{
		if(activity.getClass() == MainView.class)
		{
			MainViewCommandRegistration.register(controller);
		}
		else if(activity.getClass() == LyricsView.class)
		{
			LyricsViewCommandRegistration.register(controller);
		}
		else if(activity.getClass() == PlaylistView.class)
		{
			PlaylistViewCommandRegistration.register(controller);
		}
	}

	private void CommandUnRegistrationHandler(Activity activity)
	{
		if(activity.getClass() == MainView.class)
		{
			MainViewCommandRegistration.unRegister(controller);
		}
		else if(activity.getClass() == LyricsView.class)
		{
			LyricsViewCommandRegistration.unRegister(controller);
		}
		else if(activity.getClass() == PlaylistView.class)
		{
			PlaylistViewCommandRegistration.unRegister(controller);
		}
	}

}
