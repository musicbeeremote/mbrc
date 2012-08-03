package kelsos.mbremote.commands;

import com.google.inject.Inject;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Models.MainDataModel;
import kelsos.mbremote.Views.MainView;
import kelsos.mbremote.controller.RunningActivityAccessor;

public class UpdateMainViewCommand implements ICommand
{
	@Inject
	private RunningActivityAccessor accessor;
	@Inject
	private MainDataModel model;

	public void execute(IEvent e)
	{
		if (accessor.getRunningActivity().getClass() != MainView.class) return;
		(accessor.getRunningActivity()).runOnUiThread(new Runnable()
		{
			public void run()
			{
				MainView view = (MainView) accessor.getRunningActivity();
				view.updateTitleText(model.getTitle());
				view.updateArtistText(model.getArtist());
				view.updateAlbumText(model.getAlbum());
				view.updateYearText(model.getYear());
				view.updateVolumeData(model.getVolume());
				if(model.getAlbumCover()!=null)
				{
					view.updateAlbumCover(model.getAlbumCover());
				}
				else
				{
					view.resetAlbumCover();
				}
				view.updateConnectionIndicator(model.getIsConnectionActive());
				view.updateRepeatButtonState(model.getIsRepeatButtonActive());
				view.updateShuffleButtonState(model.getIsShuffleButtonActive());
				view.updateScrobblerButtonState(model.getIsScrobbleButtonActive());
				view.updateMuteButtonState(model.getIsMuteButtonActive());
				view.updatePlayState(model.getPlayState());
			}
		});

	}
}
