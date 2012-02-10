package kelsos.mbremote.Data;

public class MusicTrack {
    private String _title;
    private String _artist;

    public MusicTrack() {
        super();
    }

    public MusicTrack(String artist, String title) {
        super();
        this._title = title;
        this._artist = artist;
    }

    public String getArtist() {
        return _artist;
    }

    public String getTitle() {
        return _title;
    }

    public void setArtist(String _artist) {
        this._artist = _artist;
    }

    public void setTitle(String _title) {
        this._title = _title;
    }

}
