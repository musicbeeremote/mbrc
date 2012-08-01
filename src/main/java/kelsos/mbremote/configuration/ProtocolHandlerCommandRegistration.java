package kelsos.mbremote.configuration;

import com.google.inject.Inject;
import kelsos.mbremote.Command.*;
import kelsos.mbremote.Controller.Controller;
import kelsos.mbremote.Enumerations.ProtocolHandlerEventType;

public class ProtocolHandlerCommandRegistration
{
	@Inject
	public static void register(Controller controller)
	{
		controller.registerCommand(ProtocolHandlerEventType.TITLE_AVAILABLE,UpdateTitleCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.ARTIST_AVAILABLE,UpdateArtistCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.Album,UpdateAlbumCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.Year,UpdateYearCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.Volume,UpdateVolumeCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.AlbumCover,UpdateAlbumCoverCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.ConnectionState,UpdateConnectionStateCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.RepeatState,UpdateRepeatStateCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.ShuffleState,UpdateShuffleStateCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.ScrobbleState,UpdateScrobbleStateCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.MuteState,UpdateMuteStateCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PlayState,UpdatePlayStateCommand.class);
		//controller.registerCommand(ProtocolHandlerEventType.OnlineStatus,.class);
		controller.registerCommand(ProtocolHandlerEventType.PlaybackPosition,UpdatePlaybackPositionCommand.class);
		//controller.registerCommand(ProtocolHandlerEventType.Playlist,.class);
		controller.registerCommand(ProtocolHandlerEventType.ReplyAvailable,ProtocolReplyAvailableCommand.class);
		//controller.registerCommand(ProtocolHandlerEventType.Lyrics .class);
	}

	@Inject
	public static void unRegister(Controller controller)
	{

	}
}
