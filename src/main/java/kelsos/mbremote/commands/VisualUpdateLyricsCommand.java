package kelsos.mbremote.commands;

import com.google.inject.Inject;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Models.MainDataModel;
import kelsos.mbremote.Views.LyricsView;
import kelsos.mbremote.controller.RunningActivityAccessor;

public class VisualUpdateLyricsCommand implements ICommand
{
	@Inject
	RunningActivityAccessor accessor;
	@Inject
	MainDataModel model;

	@Override
	public void execute(IEvent e)
	{
		if(LyricsView.class != accessor.getRunningActivity().getClass()) return;

		final String lyrics = (e.getData()!=null&&!e.getData().equals(""))?e.getData():"Lyrics Not Found";
		accessor.getRunningActivity().runOnUiThread(new Runnable() {
			public void run() {
				LyricsView view = (LyricsView) accessor.getRunningActivity();
				view.updateLyricsData(lyrics, model.getArtist(), model.getTitle());
			}
		});
	}
}
