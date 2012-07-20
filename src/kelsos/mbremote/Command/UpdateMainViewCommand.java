package kelsos.mbremote.Command;

import android.app.Activity;
import com.google.inject.Inject;
import com.google.inject.Provider;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Models.MainDataModel;
import kelsos.mbremote.Views.MainView;

public class UpdateMainViewCommand implements ICommand
{
	@Inject private Provider<MainView> mainViewProvider;
	@Inject private MainDataModel model;

	public void execute(IEvent e)
	{

		MainView mainView = mainViewProvider.get();
		mainView.runOnUiThread(new Runnable() {
			public void run() {
				mainView.updateTitleText(model.getTitle());
				mainView.updateArtistText(model.getArtist());
				mainView.updateAlbumText(model.getAlbum());
				mainView.updateYearText(model.getYear());
				mainView.updateVolumeData(model.getVolume());
				mainView.updateAlbumCover(model.getAlbumCover());
				mainView.updateConnectionIndicator(model.getIsConnectionActive());
				mainView.updateRepeatButtonState(model.getIsRepeatButtonActive());
				mainView.updateShuffleButtonState(model.getIsShuffleButtonActive());
				mainView.updateScrobblerButtonState(model.getIsScrobbleButtonActive());
				mainView.updateMuteButtonState(model.getIsMuteButtonActive());
				mainView.updatePlayState(model.getPlayState());
			}
		});

	}
}
