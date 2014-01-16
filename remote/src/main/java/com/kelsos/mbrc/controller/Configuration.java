package com.kelsos.mbrc.controller;

import com.google.inject.Inject;
import com.kelsos.mbrc.commands.*;
import com.kelsos.mbrc.commands.model.*;
import com.kelsos.mbrc.commands.visual.*;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.SocketEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.net.Protocol;

public final class Configuration {

    private Configuration() { }

    @Inject public static void initialize(Controller controller) {
        controller.register(ProtocolEventType.ReduceVolume, ReduceVolumeOnRingCommand.class);
        controller.register(ProtocolEventType.HandshakeComplete, VisualUpdateHandshakeComplete.class);
        controller.register(ProtocolEventType.InformClientNotAllowed, NotifyNotAllowedCommand.class);
        controller.register(ProtocolEventType.InformClientPluginOutOfDate, NotifyPluginOutOfDateCommand.class);
        controller.register(ProtocolEventType.InitiateProtocolRequest, ProtocolRequest.class);
        controller.register(ProtocolEventType.PluginVersionCheck, VersionCheckCommand.class);
        controller.register(ProtocolEventType.UserAction, ProcessUserAction.class);
        controller.register(Protocol.NOW_PLAYING_TRACK, UpdateNowPlayingTrack.class);
        controller.register(Protocol.NOW_PLAYING_COVER, UpdateCover.class);
        controller.register(Protocol.NOW_PLAYING_RATING, UpdateRating.class);
        controller.register(Protocol.PLAYER_STATUS, UpdatePlayerStatus.class);
        controller.register(Protocol.PLAYER_STATE, UpdatePlayState.class);
        controller.register(Protocol.PLAYER_REPEAT, UpdateRepeat.class);
        controller.register(Protocol.PLAYER_VOLUME, UpdateVolume.class);
        controller.register(Protocol.PLAYER_MUTE, UpdateMute.class);
        controller.register(Protocol.PLAYER_SHUFFLE, UpdateShuffle.class);
        controller.register(Protocol.PLAYER_SCROBBLE, UpdateLastFm.class);
        controller.register(Protocol.NOW_PLAYING_LYRICS, UpdateLyrics.class);
        controller.register(Protocol.NOW_PLAYING_LIST, UpdateNowPlayingList.class);
        controller.register(Protocol.NOW_PLAYING_LFM_RATING, UpdateLfmRating.class);
        controller.register(Protocol.NOW_PLAYING_REMOVE, UpdateNowPlayingTrackRemoval.class);
        controller.register(Protocol.NOW_PLAYING_MOVE, UpdateNowPlayingTrackMoved.class);
        controller.register(Protocol.LIBRARY_SEARCH_ARTIST, UpdateArtistSearchResults.class);
        controller.register(Protocol.LIBRARY_SEARCH_ALBUM, UpdateAlbumSearchResults.class);
        controller.register(Protocol.LIBRARY_SEARCH_GENRE, UpdateGenreSearchResults.class);
        controller.register(Protocol.LIBRARY_SEARCH_TITLE, UpdateTrackSearchResults.class);
        controller.register(Protocol.LIBRARY_ARTIST_ALBUMS, UpdateAlbumSearchResults.class);
        controller.register(Protocol.LIBRARY_GENRE_ARTISTS, UpdateArtistSearchResults.class);
        controller.register(Protocol.LIBRARY_ALBUM_TRACKS, UpdateTrackSearchResults.class);
        controller.register(Protocol.NOW_PLAYING_POSITION, UpdatePlaybackPositionCommand.class);
        controller.register(Protocol.PLUGIN_VERSION, UpdatePluginVersionCommand.class);
        controller.register(Protocol.PLAYLIST_LIST, UpdateAvailablePlaylists.class);
        controller.register(Protocol.PLAYLIST_GET_FILES, UpdatePlaylistTracks.class);
        controller.register(Protocol.LIBRARY_SYNC, HandleLibrarySync.class);

        controller.register(UserInputEventType.SettingsChanged, RestartConnectionCommand.class);
        controller.register(UserInputEventType.CancelNotification, CancelNotificationCommand.class);
        controller.register(UserInputEventType.StartConnection, InitiateConnectionCommand.class);
        controller.register(UserInputEventType.ResetConnection, RestartConnectionCommand.class);
        controller.register(UserInputEventType.StartDiscovery, StartDiscoveryCommand.class);
        controller.register(UserInputEventType.KeyVolumeUp, KeyVolumeUpCommand.class);
        controller.register(UserInputEventType.KeyVolumeDown, KeyVolumeDownCommand.class);
        controller.register(SocketEventType.DATA_AVAILABLE, SocketDataAvailableCommand.class);
        controller.register(SocketEventType.STATUS_CHANGED, ConnectionStatusChangedCommand.class);
        controller.register(SocketEventType.HANDSHAKE_UPDATE, HandleHanshake.class);
    }
}
