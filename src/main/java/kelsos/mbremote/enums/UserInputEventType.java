package kelsos.mbremote.enums;

import kelsos.mbremote.Interfaces.IEventType;

public enum UserInputEventType implements IEventType
{
    PlayPause,
    Stop,
    Next,
    Previous,
    Repeat,
    Shuffle,
    Scrobble,
    Mute,
    Lyrics,
    Refresh,
    Playlist,
    Volume,
    PlaybackPosition,
    Initialize,
	PlaySpecifiedTrack,
	NowPlaying_RemoveSelected

}
