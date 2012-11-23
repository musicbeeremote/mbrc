package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.fragments.MainFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.services.ProtocolHandler;

public class UpdateMainViewCommand implements ICommand
{
	@Inject
	ActiveFragmentProvider afProvider;
	@Inject
	private MainDataModel model;
	@Inject
	private ProtocolHandler handler;

	public void execute(IEvent e)
	{
		if(afProvider.getActiveFragment(MainFragment.class)!=null)
		{
			Activity cActivity = afProvider.getActiveFragment(MainFragment.class).getActivity();
			cActivity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					MainFragment mFragment = ((MainFragment)afProvider.getActiveFragment(MainFragment.class));

					mFragment.updateTitleText(model.getTitle());
					mFragment.updateArtistText(model.getArtist());
					mFragment.updateAlbumText(model.getAlbum());
					mFragment.updateYearText(model.getYear());
					mFragment.updateVolumeData(model.getVolume());
					if(model.getAlbumCover()!=null)
					{
						mFragment.updateAlbumCover(model.getAlbumCover());
					}
					else
					{
						mFragment.resetAlbumCover();
					}
					mFragment.updateRepeatButtonState(model.getIsRepeatButtonActive());
					mFragment.updateShuffleButtonState(model.getIsShuffleButtonActive());
					mFragment.updateScrobblerButtonState(model.getIsScrobbleButtonActive());
					mFragment.updateMuteButtonState(model.getIsMuteButtonActive());
					mFragment.updatePlayState(model.getPlayState());
					if(model.getIsConnectionActive()&&!handler.getHandshakeComplete())
					{
						mFragment.updateConnectivityStatus(ConnectionStatus.CONNECTION_ON);
					}
					else if(model.getIsConnectionActive()&&handler.getHandshakeComplete())
					{
						mFragment.updateConnectivityStatus(ConnectionStatus.CONNECTION_ACTIVE);
					}
					else
					{
						mFragment.updateConnectivityStatus(ConnectionStatus.CONNECTION_OFF);
					}

				}
			});
		}
	}
}
