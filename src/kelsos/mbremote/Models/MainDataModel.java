package kelsos.mbremote.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import kelsos.mbremote.Events.ProtocolDataType;
import kelsos.mbremote.Events.ModelDataEvent;
import kelsos.mbremote.Events.ModelDataEventListener;
import kelsos.mbremote.Events.ModelDataEventSource;
import kelsos.mbremote.Others.Const;
public class MainDataModel {

    private static MainDataModel _instance;
    private ModelDataEventSource _source;

    private MainDataModel(){
        _source = new ModelDataEventSource();
        _title="";
        _artist="";
        _album="";
        _year = "";
        _volume = 100;

        _isConnectionActive=false;
        _isRepeatButtonActive=false;
        _isShuffleButtonActive=false;
        _isScrobbleButtonActive=false;
        _isMuteButtonActive=false;
        _isDeviceOnline=false;
        _playState = PlayState.Stopped;
    }

    public void addEventListener(ModelDataEventListener listener)
    {
        _source.addEventListener(listener);
    }

    public void removeEventListener(ModelDataEventListener listener)
    {
        _source.removeEventListener(listener);
    }

    public static synchronized MainDataModel getInstance()
    {
        if(_instance==null)
            _instance = new MainDataModel();
        return _instance;
    }

    private String _title;
    private String _artist;
    private String _album;
    private String _year;
    private int _volume;
    private Bitmap _albumCover;

    private boolean _isConnectionActive;
    private boolean _isRepeatButtonActive;
    private boolean _isShuffleButtonActive;
    private boolean _isScrobbleButtonActive;
    private boolean _isMuteButtonActive;
    private boolean _isDeviceOnline;


    private PlayState _playState;

    public void setTitle(String title)
    {
        if(title.equals(_title)) return;
        _title=title;
        _source.dispatchEvent(new ModelDataEvent(this, ProtocolDataType.Title));
    }

    public String getTitle()
    {
        return _title;
    }

    public void setAlbum(String album)
    {
        if(album.equals(_album)) return;
        _album = album;
        _source.dispatchEvent(new ModelDataEvent(this, ProtocolDataType.Album));
    }

    public String getAlbum()
    {
        return _album;
    }

    public void setArtist(String artist)
    {
        if(artist.equals(_artist)) return;
        _artist = artist;
        _source.dispatchEvent(new ModelDataEvent(this, ProtocolDataType.Artist));
    }

    public String getArtist()
    {
        return _artist;
    }

    public void setYear(String year)
    {
        if(year.equals(_year)) return;
        _year=year;
        _source.dispatchEvent(new ModelDataEvent(this, ProtocolDataType.Year));
    }

    public String getYear()
    {
        return _year;
    }

    public void setVolume(String volume)
    {
        int newVolume = Integer.parseInt(volume);
        if(newVolume==_volume) return;
        _volume = newVolume;
        _source.dispatchEvent(new ModelDataEvent(this, ProtocolDataType.Volume));
    }

    public int getVolume()
    {
        return _volume;
    }

    public void setAlbumCover(String base64format)
    {
        new ImageDecodeTask().execute(base64format);
    }

    private void setAlbumCover(Bitmap cover)
    {
        _albumCover = cover;
        _source.dispatchEvent(new ModelDataEvent(this, ProtocolDataType.AlbumCover));
    }

    public Bitmap getAlbumCover()
    {
        return _albumCover;
    }

    public void setConnectionState(String connectionActive)
    {
        boolean newStatus = Boolean.parseBoolean(connectionActive);
        if(newStatus==_isConnectionActive) return;
        _isConnectionActive=newStatus;
        _source.dispatchEvent(new ModelDataEvent(this, ProtocolDataType.ConnectionState));
    }

    public boolean getIsConnectionActive()
    {
       return _isConnectionActive;
    }

    public void setRepeatState(String repeatButtonActive)
    {
        boolean newStatus = (repeatButtonActive.equals("All"));
        if(newStatus== _isRepeatButtonActive) return;
        _isRepeatButtonActive = newStatus;
        _source.dispatchEvent(new ModelDataEvent(this, ProtocolDataType.RepeatState));
    }

    public boolean getIsRepeatButtonActive()
    {
        return _isRepeatButtonActive;
    }

    public void setShuffleState(String shuffleButtonActive)
    {
        boolean newStatus = Boolean.parseBoolean(shuffleButtonActive);
        if(newStatus == _isShuffleButtonActive) return;
        _isShuffleButtonActive = newStatus;
        _source.dispatchEvent(new ModelDataEvent(this, ProtocolDataType.ShuffleState));
    }

    public boolean getIsShuffleButtonActive()
    {
        return _isShuffleButtonActive;
    }

    public void setScrobbleState(String scrobbleButtonActive)
    {
        boolean newStatus = Boolean.parseBoolean(scrobbleButtonActive);
        if(newStatus == _isScrobbleButtonActive) return;
        _isScrobbleButtonActive = newStatus;
        _source.dispatchEvent(new ModelDataEvent(this, ProtocolDataType.ScrobbleState));
    }

    public boolean getIsScrobbleButtonActive()
    {
        return _isScrobbleButtonActive;
    }

    public void setMuteState(String muteButtonActive)
    {
        boolean newStatus = Boolean.parseBoolean(muteButtonActive);
        if(newStatus == _isMuteButtonActive) return;
        _isMuteButtonActive = newStatus;
        _source.dispatchEvent(new ModelDataEvent(this, ProtocolDataType.MuteState));
    }

    public boolean getIsMuteButtonActive()
    {
        return _isMuteButtonActive;
    }

    public void setPlayState(String playState)
    {
        PlayState newState = PlayState.Undefined;
        if(playState.equalsIgnoreCase(Const.PLAYING)) newState = PlayState.Playing;
        else if (playState.equalsIgnoreCase(Const.STOPPED)) newState = PlayState.Stopped;
        else if (playState.equalsIgnoreCase(Const.PAUSED)) newState = PlayState.Paused;
        if(_playState==newState) return;
        _playState = newState;
        _source.dispatchEvent(new ModelDataEvent(this, ProtocolDataType.PlayState));
    }

    public PlayState getPlayState()
    {
        return _playState;
    }

    public void setIsDeviceOnline(boolean value)
    {
        if(value==_isDeviceOnline) return;
        _isDeviceOnline = value;
        _source.dispatchEvent(new ModelDataEvent(this, ProtocolDataType.OnlineStatus));
    }

    public boolean getIsDeviceOnline()
    {
        return _isDeviceOnline;
    }

    private class ImageDecodeTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            byte[] decodedImage = Base64.decode(params[0] , Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            setAlbumCover(result);
        }
    }

}

