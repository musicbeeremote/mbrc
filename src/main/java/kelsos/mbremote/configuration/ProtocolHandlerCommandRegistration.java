package kelsos.mbremote.configuration;

import com.google.inject.Inject;
import kelsos.mbremote.commands.*;
import kelsos.mbremote.controller.Controller;
import kelsos.mbremote.enums.ProtocolHandlerEventType;

public class ProtocolHandlerCommandRegistration
{
	@Inject
	public static void register(Controller controller)
	{
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_TITLE_AVAILABLE,UpdateTitleCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_ARTIST_AVAILABLE,UpdateArtistCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_ALBUM_AVAILABLE,UpdateAlbumCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_YEAR_AVAILABLE,UpdateYearCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_VOLUME_AVAILABLE,UpdateVolumeCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_COVER_AVAILABLE,UpdateAlbumCoverCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_REPEAT_STATE_AVAILABLE,UpdateRepeatStateCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_SHUFFLE_STATE_AVAILABLE,UpdateShuffleStateCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_SCROBBLE_STATE_AVAILABLE,UpdateScrobbleStateCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_MUTE_STATE_AVAILABLE,UpdateMuteStateCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAY_STATE_AVAILABLE,UpdatePlayStateCommand.class);
		//controller.registerCommand(ProtocolHandlerEventType.OnlineStatus,.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAYBACK_POSITION_AVAILABLE,UpdatePlaybackPositionCommand.class);
		//controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAYLIST_AVAILABLE,.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_REPLY_AVAILABLE,ProtocolReplyAvailableCommand.class);
		//controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_LYRICS_AVAILABLE .class);
		/* responsible for reducing the volume on incoming call */
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_REDUCE_VOLUME, ReduceVolumeOnRingCommand.class);
	}

	@Inject
	public static void unRegister(Controller controller)
	{

	}
}
