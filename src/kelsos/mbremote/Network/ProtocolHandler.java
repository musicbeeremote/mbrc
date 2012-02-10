package kelsos.mbremote.Network;

public class ProtocolHandler {
    static final String STATE = "state";
    static final String PLAYPAUSE = "<playPause/>";
    static final String PREVIOUS = "<previous/>";
    static final String NEXT = "<next/>";
    static final String STOP = "<stopPlayback/>";
    static final String PLAYSTATE = "<playState/>";
    static final String VOLUME_OPEN = "<volume>";
    static final String VOLUME_CLOSED = "</volume>";
    static final String SONGCHANGED = "<songChanged/>";
    static final String SONGINFO = "<songInfo/>";
    static final String SONGCOVER = "<songCover/>";
    static final String SHUFFLE_OPEN = "<shuffle>";
    static final String SHUFFLE_CLOSE = "</shuffle>";
    static final String MUTE_OPEN = "<mute>";
    static final String MUTE_CLOSE = "</mute>";
    static final String REPEAT_OPEN = "<repeat>";
    static final String REPEAT_CLOSE = "</repeat>";
    static final String PLAYLIST = "<playlist/>";
    static final String PLAYNOW_OPEN = "<playNow>";
    static final String PLAYNOW_CLOSE = "</playNow>";
    static final String SCROBBLE_OPEN = "<scrobbler>";
    static final String SCROBBLE_CLOSE = "</scrobbler>";
    static final String LYRICS = "<lyrics/>";
    static final String RATING_OPEN = "<rating>";
    static final String RATING_CLOSE = "</rating>";
    static final String PLAYER_STATUS = "<playerStatus/>";

    public static enum PlayerAction {
        PlayPause,
        Previous,
        Next,
        Stop,
        PlayState,
        Volume,
        SongChangedStatus,
        SongInformation,
        SongCover,
        Shuffle,
        Mute,
        Repeat,
        Playlist,
        PlayNow,
        Scrobble,
        Lyrics,
        Rating,
        PlayerStatus
    }
    
    public static String getActionString(PlayerAction action, String actionContent)
    {
        String result = "";
        switch (action) {
            case PlayPause:
                result = PLAYPAUSE;
                break;
            case Previous:
                result = PREVIOUS;
                break;
            case Next:
                result = NEXT;
                break;
            case Stop:
                result = STOP;
                break;
            case PlayState:
                result = PLAYSTATE;
                break;
            case Volume:
                result = VOLUME_OPEN + actionContent + VOLUME_CLOSED;
                break;
            case SongChangedStatus:
                result = SONGCHANGED;
                break;
            case SongInformation:
                result = SONGINFO;
                break;
            case SongCover:
                result = SONGCOVER;
                break;
            case Shuffle:
                result = SHUFFLE_OPEN + actionContent + SHUFFLE_CLOSE;
                break;
            case Mute:
                result = MUTE_OPEN + actionContent + MUTE_CLOSE;
                break;
            case Repeat:
                result = REPEAT_OPEN + actionContent + REPEAT_CLOSE;
                break;
            case Playlist:
                result = PLAYLIST;
                break;
            case PlayNow:
                result = PLAYNOW_OPEN + actionContent + PLAYNOW_CLOSE;
                break;
            case Scrobble:
                result = SCROBBLE_OPEN + actionContent + SCROBBLE_CLOSE;
                break;
            case Lyrics:
                result = LYRICS;
                break;
            case Rating:
                result = RATING_OPEN + actionContent + RATING_CLOSE;
                break;
            case PlayerStatus:
                result = PLAYER_STATUS;
                break;
        }
        return result;
    }
}