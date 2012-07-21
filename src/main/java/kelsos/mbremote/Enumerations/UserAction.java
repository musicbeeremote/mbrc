package kelsos.mbremote.Enumerations;

import kelsos.mbremote.Interfaces.IEventType;

public enum UserAction implements IEventType
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
	GetPlaylist,
	PlaySpecifiedTrack

}
