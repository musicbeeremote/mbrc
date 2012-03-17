package kelsos.mbremote.Network;

public class ProtocolHandler {

    private static String PrepareXml(String name, String value) {
        return "<" + name + ">" + value + "</" + name + ">";
    }

    public enum PlayerAction {
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
        PlayerStatus,
        Protocol,
        Player
    }

    public static String getActionString(PlayerAction action, String value) {
        switch (action) {
            case PlayPause:
                return PrepareXml(Protocol.PLAYPAUSE, value);
            case Previous:
                return PrepareXml(Protocol.PREVIOUS, value);
            case Next:
                return PrepareXml(Protocol.NEXT, value);
            case Stop:
                return PrepareXml(Protocol.STOP, value);
            case PlayState:
                return PrepareXml(Protocol.PLAYSTATE, value);
            case Volume:
                return PrepareXml(Protocol.VOLUME, value);
            case SongChangedStatus:
                return PrepareXml(Protocol.SONGCHANGED, value);
            case SongInformation:
                return PrepareXml(Protocol.SONGINFO, value);
            case SongCover:
                return PrepareXml(Protocol.SONGCOVER, value);
            case Shuffle:
                return PrepareXml(Protocol.SHUFFLE, value);
            case Mute:
                return PrepareXml(Protocol.MUTE, value);
            case Repeat:
                return PrepareXml(Protocol.REPEAT, value);
            case Playlist:
                return PrepareXml(Protocol.PLAYLIST, value);
            case PlayNow:
                return PrepareXml(Protocol.PLAYNOW, value);
            case Scrobble:
                return PrepareXml(Protocol.SCROBBLE, value);
            case Lyrics:
                return PrepareXml(Protocol.LYRICS, value);
            case Rating:
                return PrepareXml(Protocol.RATING, value);
            case PlayerStatus:
                return PrepareXml(Protocol.PLAYER_STATUS, value);
            case Protocol:
                return PrepareXml(Protocol.PROTOCOL, value);
            case Player:
                return PrepareXml(Protocol.PLAYER,value);
            default:
                return PrepareXml(Protocol.ERROR, "Invalid Request");
        }
    }
}