package kelsos.mbremote.Enumerations;

import kelsos.mbremote.Interfaces.IEventType;

public enum ProtocolHandlerEventType implements IEventType
{
	TITLE_AVAILABLE,
	ARTIST_AVAILABLE,
    Album,
    Year,
    Volume,
    AlbumCover,
    ConnectionState,
    RepeatState,
    ShuffleState,
    ScrobbleState,
    MuteState,
    PlayState,
    OnlineStatus,
    PlaybackPosition,
    Playlist,
    ReplyAvailable,
    Lyrics
}
