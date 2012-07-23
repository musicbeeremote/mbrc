package kelsos.mbremote.Enumerations;

import kelsos.mbremote.Interfaces.IEventType;

public enum ProtocolHandlerEventType implements IEventType
{
     Title,
    Artist,
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
