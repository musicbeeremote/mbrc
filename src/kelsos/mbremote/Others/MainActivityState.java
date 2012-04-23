package kelsos.mbremote.Others;

public class MainActivityState {
     private boolean _isConnected;
    private boolean _isRepeatAllActive;
    private boolean _isPlayerShuffling;
    private boolean _isScrobblerActive;
    private boolean _isPlayerMuted;
    private String _playState;

    public String getPlayState() {
        return _playState;
    }

    public void setPlayState(String playState) {
        this._playState = playState;
    }



    public boolean isConnected() {
        return _isConnected;
    }

    public void setIsConnected(boolean _isConnected) {
        this._isConnected = _isConnected;
    }

    public boolean isRepeatAllActive() {
        return _isRepeatAllActive;
    }

    public void setIsRepeatAllActive(boolean _isRepeatAllActive) {
        this._isRepeatAllActive = _isRepeatAllActive;
    }

    public boolean is_isPlayerShuffling() {
        return _isPlayerShuffling;
    }

    public void setIsPlayerShuffling(boolean _isPlayerShuffling) {
        this._isPlayerShuffling = _isPlayerShuffling;
    }

    public boolean is_isScrobblerActive() {
        return _isScrobblerActive;
    }

    public void setIsScrobblerActive(boolean _isScrobblerActive) {
        this._isScrobblerActive = _isScrobblerActive;
    }

    public boolean is_isPlayerMuted() {
        return _isPlayerMuted;
    }

    public void setIsPlayerMuted(boolean _isPlayerMuted) {
        this._isPlayerMuted = _isPlayerMuted;
    }

}
