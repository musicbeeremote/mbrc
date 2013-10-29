package com.kelsos.mbrc.configuration;

import com.google.inject.Inject;
import com.kelsos.mbrc.commands.*;
import com.kelsos.mbrc.commands.model.*;
import com.kelsos.mbrc.commands.visual.*;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.SocketEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.net.Protocol;

public class Configuration {
    @Inject public static void initialize(Controller controller) {
        controller.register(ProtocolEventType.ReduceVolume, ReduceVolumeOnRingCommand.class);
        controller.register(ProtocolEventType.HandshakeComplete, VisualUpdateHandshakeComplete.class);
        controller.register(ProtocolEventType.InformClientNotAllowed, NotifyNotAllowedCommand.class);
        controller.register(ProtocolEventType.InformClientPluginOutOfDate, NotifyPluginOutOfDateCommand.class);
        controller.register(ProtocolEventType.InitiateProtocolRequest, ProtocolRequest.class);
        controller.register(ProtocolEventType.PluginVersionCheck, VersionCheckCommand.class);
        controller.register(ProtocolEventType.UserAction, ProcessUserAction.class);
        controller.register(Protocol.NowPlayingTrack, UpdateNowPlayingTrack.class);
        controller.register(Protocol.NowPlayingCover, UpdateCover.class);
        controller.register(Protocol.NowPlayingRating, UpdateRating.class);
        controller.register(Protocol.PlayerStatus, UpdatePlayerStatus.class);
        controller.register(Protocol.PlayerState, UpdatePlayState.class);
        controller.register(Protocol.PlayerRepeat, UpdateRepeat.class);
        controller.register(Protocol.PlayerVolume, UpdateVolume.class);
        controller.register(Protocol.PlayerMute, UpdateMute.class);
        controller.register(Protocol.PlayerShuffle, UpdateShuffle.class);
        controller.register(Protocol.PlayerScrobble, UpdateLastFm.class);
        controller.register(Protocol.NowPlayingLyrics, UpdateLyrics.class);
        controller.register(Protocol.NowPlayingList, UpdateNowPlayingList.class);
        controller.register(Protocol.NowPlayingLfmRating, UpdateLfmRating.class);
        controller.register(Protocol.NowPlayingListRemove, UpdateNowPlayingTrackRemoval.class);
        controller.register(Protocol.NowPlayingListMove, UpdateNowPlayingTrackMoved.class);
        controller.register(Protocol.LibrarySearchArtist, UpdateArtistSearchResults.class);
        controller.register(Protocol.LibrarySearchAlbum, UpdateAlbumSearchResults.class);
        controller.register(Protocol.LibrarySearchGenre, UpdateGenreSearchResults.class);
        controller.register(Protocol.LibrarySearchTitle, UpdateTrackSearchResults.class);
        controller.register(Protocol.LibraryArtistAlbums, UpdateAlbumSearchResults.class);
        controller.register(Protocol.LibraryGenreArtists, UpdateArtistSearchResults.class);
        controller.register(Protocol.LibraryAlbumTracks, UpdateTrackSearchResults.class);
        controller.register(Protocol.NowPlayingPosition, UpdatePlaybackPositionCommand.class);
        controller.register(Protocol.PluginVersion, UpdatePluginVersionCommand.class);
        controller.register(Protocol.PlaylistList, UpdateAvailablePlaylists.class);
        controller.register(Protocol.PlaylistGetFiles, UpdatePlaylistTracks.class);

        controller.register(UserInputEventType.SettingsChanged, RestartConnectionCommand.class);
        controller.register(UserInputEventType.CancelNotification, CancelNotificationCommand.class);
        controller.register(UserInputEventType.StartConnection, InitiateConnectionCommand.class);
        controller.register(UserInputEventType.ResetConnection, RestartConnectionCommand.class);
        controller.register(UserInputEventType.StartDiscovery, StartDiscoveryCommand.class);
        controller.register(UserInputEventType.KeyVolumeUp, KeyVolumeUpCommand.class);
        controller.register(UserInputEventType.KeyVolumeDown, KeyVolumeDownCommand.class);
        controller.register(SocketEventType.SocketDataAvailable, SocketDataAvailableCommand.class);
        controller.register(SocketEventType.SocketStatusChanged, ConnectionStatusChangedCommand.class);
        controller.register(SocketEventType.SocketHandshakeUpdate, HandleHanshake.class);
    }
}
