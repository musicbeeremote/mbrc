package kelsos.mbremote.Enumerations;

import kelsos.mbremote.Interfaces.IEventType;

public enum ProtocolDataType implements IEventType
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
