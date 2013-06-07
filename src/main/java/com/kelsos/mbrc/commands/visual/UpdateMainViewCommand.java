package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.events.ui.ConnectionStatusChange;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.fragments.MainFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.services.ProtocolHandler;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.squareup.otto.Bus;

public class UpdateMainViewCommand implements ICommand
{
	@Inject
	ActiveFragmentProvider afProvider;
	@Inject private MainDataModel model;
	@Inject private ProtocolHandler handler;
    @Inject MainThreadBusWrapper bus;

	public void execute(IEvent e)
	{

        if(model.getIsConnectionActive()&&!handler.getHandshakeComplete())
        {
            bus.post(new ConnectionStatusChange(ConnectionStatus.CONNECTION_ON));
        }
        else if(model.getIsConnectionActive()&&handler.getHandshakeComplete())
        {
            bus.post(new ConnectionStatusChange(ConnectionStatus.CONNECTION_ACTIVE));
        }
        else
        {
            bus.post(new ConnectionStatusChange(ConnectionStatus.CONNECTION_OFF));
        }

        bus.post(new TrackInfoChange(model.getArtist(),model.getTitle(),model.getAlbum(),model.getYear()));
        CoverAvailable cAv;
        if(model.getAlbumCover()!=null) {
            cAv = new CoverAvailable(model.getAlbumCover());
        } else {
            cAv = new CoverAvailable();
        }
        bus.post(cAv);

		if(afProvider.getActiveFragment(MainFragment.class)!=null)
		{
			Activity cActivity = afProvider.getActiveFragment(MainFragment.class).getActivity();
			cActivity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					MainFragment mFragment = ((MainFragment)afProvider.getActiveFragment(MainFragment.class));

					//mFragment.updateVolumeData(model.getVolume());
					mFragment.updateRepeatButtonState(model.getIsRepeatButtonActive());
					mFragment.updateShuffleButtonState(model.getIsShuffleButtonActive());
					mFragment.updateScrobblerButtonState(model.getIsScrobbleButtonActive());
					//mFragment.updateMuteButtonState(model.getIsMuteButtonActive());
					//mFragment.hadlePlayStateChange(model.getPlayState());


				}
			});
		}
	}
}
