package kelsos.mbremote.configuration;

import com.google.inject.Inject;
import kelsos.mbremote.commands.*;
import kelsos.mbremote.controller.Controller;
import kelsos.mbremote.enums.ModelDataEventType;

public class MainDataModelCommandRegistration
{
	@Inject
	public static void registerCommands(Controller controller)
	{
		controller.registerCommand(ModelDataEventType.MODEL_ALBUM_UPDATED, VisualUpdateAlbumCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_ARTIST_UPDATED, VisualUpdateArtistCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_CONNECTION_STATE_UPDATED, VisualUpdateConnectionStateCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_COVER_UPDATED, VisualUpdateCoverCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_MUTE_STATE_UPDATED, VisualUpdateMuteCommand.class);
		//controller.registerCommand(ModelDataEventType.MODEL_ONLINE_STATE_UPDATED, );
		controller.registerCommand(ModelDataEventType.MODEL_PLAY_STATE_UPDATED, VisualUpdatePlaystateCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_REPEAT_STATE_UPDATED, VisualUpdateRepeatCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_SCROBBLE_STATE_UPDATED, VisualUpdateScrobbleCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_SHUFFLE_STATE_UPDATED, VisualUpdateShuffleCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_TITLE_UPDATED, VisualUpdateTitleCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_VOLUME_UPDATED, VisualUpdateVolumeCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_YEAR_UPDATED, VisualUpdateYearTitleCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_COVER_NOT_FOUND, NoCoverFoundCommand.class);


	}

	@Inject
	public static void unRegisterCommands(Controller controller)
	{

	}
}
