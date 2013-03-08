package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.fragments.MainFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.others.Protocol;
import com.kelsos.mbrc.services.SocketService;

public class VisualUpdateHandshakeComplete implements ICommand
{
	@Inject ActiveFragmentProvider afProvider;
    @Inject
    SocketService service;

	public void execute(IEvent e)
	{
		if(!(Boolean)e.getData()) return;

        service.sendData(new SocketMessage(Protocol.PlayerStatus,Protocol.Request, ""));
        service.sendData(new SocketMessage(Protocol.NowPlayingTrack, Protocol.Request, ""));
        service.sendData(new SocketMessage(Protocol.NowPlayingCover, Protocol.Request, ""));

		if(afProvider.getActiveFragment(MainFragment.class)!=null)
		{
			Activity cActivity = afProvider.getActiveFragment(MainFragment.class).getActivity();
			cActivity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					((MainFragment)afProvider.getActiveFragment(MainFragment.class)).updateConnectivityStatus(ConnectionStatus.CONNECTION_ACTIVE);
				}
			});
		}
	}
}


