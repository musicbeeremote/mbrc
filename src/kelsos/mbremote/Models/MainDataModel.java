package kelsos.mbremote.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import kelsos.mbremote.Events.DataType;
import kelsos.mbremote.Events.NewModelDataEvent;
import kelsos.mbremote.Events.NewModelDataEventListener;
import kelsos.mbremote.Events.NewModelDataEventSource;
import kelsos.mbremote.Others.Const;

public class MainDataModel {

    private static MainDataModel _instance;
    private NewModelDataEventSource _source;

    private MainDataModel(){
        _source = new NewModelDataEventSource();
    }

    public void addEventListener(NewModelDataEventListener listener)
    {
        _source.addEventListener(listener);
    }

    public void removeEventListener(NewModelDataEventListener listener)
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

    private Boolean _isConnectionActive;
    private Boolean _isRepeatButtonActive;
    private Boolean _isShuffleButtonActive;
    private Boolean _isScrobbleButtonActive;
    private Boolean _isMuteButtonActive;
    private PlayState _playState;

    public void setTitle(String title)
    {
        if(_title.equals(title)) return;
        _title=title;
        _source.fireEvent(new NewModelDataEvent(this, DataType.Title));
    }

    public String getTitle()
    {
        return _title;
    }

    public void setAlbum(String album)
    {
        if(_album.equals(album)) return;
        _album = album;
        _source.fireEvent(new NewModelDataEvent(this,DataType.Album));
    }

    public String getAlbum()
    {
        return _album;
    }

    public void setArtist(String artist)
    {
        if(_artist.equals(artist)) return;
        _artist = artist;
        _source.fireEvent(new NewModelDataEvent(this,DataType.Artist));
    }

    public String getArtist()
    {
        return _album;
    }

    public void setYear(String year)
    {
        if(_year.equals(year)) return;
        _year=year;
        _source.fireEvent(new NewModelDataEvent(this,DataType.Year));
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
        _source.fireEvent(new NewModelDataEvent(this, DataType.Volume));
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
        _source.fireEvent(new NewModelDataEvent(this,DataType.Bitmap));
    }

    public Bitmap getAlbumCover()
    {
        return _albumCover;
    }

    public void setIsConnectionActive(String connectionActive)
    {
        boolean newStatus = Boolean.parseBoolean(connectionActive);
        if(newStatus==_isConnectionActive) return;
        _isConnectionActive=newStatus;
        _source.fireEvent(new NewModelDataEvent(this,DataType.ConnectionState));
    }

    public boolean getIsConnectionActive()
    {
       return _isConnectionActive;
    }

    public void setIsRepeatButtonActive(String repeatButtonActive)
    {
        boolean newStatus = Boolean.parseBoolean(repeatButtonActive);
        if(newStatus== _isRepeatButtonActive) return;
        _isRepeatButtonActive = newStatus;
        _source.fireEvent(new NewModelDataEvent(this, DataType.RepeatState));
    }

    public boolean getIsRepeatButtonActive()
    {
        return _isRepeatButtonActive;
    }

    public void setIsShuffleButtonActive(String shuffleButtonActive)
    {
        boolean newStatus = Boolean.parseBoolean(shuffleButtonActive);
        if(newStatus == _isShuffleButtonActive) return;
        _isShuffleButtonActive = newStatus;
        _source.fireEvent(new NewModelDataEvent(this, DataType.ShuffleState));
    }

    public boolean getIsShuffleButtonActive()
    {
        return _isShuffleButtonActive;
    }

    public void setIsScrobbleButtonActive(String scrobbleButtonActive)
    {
        boolean newStatus = Boolean.parseBoolean(scrobbleButtonActive);
        if(newStatus == _isScrobbleButtonActive) return;
        _isScrobbleButtonActive = newStatus;
        _source.fireEvent(new NewModelDataEvent(this, DataType.ScrobbleState));
    }

    public boolean getIsScrobbleButtonActive()
    {
        return _isScrobbleButtonActive;
    }

    public void setIsMuteButtonActive(String muteButtonActive)
    {
        boolean newStatus = Boolean.parseBoolean(muteButtonActive);
        if(newStatus == _isMuteButtonActive) return;
        _isMuteButtonActive = newStatus;
        _source.fireEvent(new NewModelDataEvent(this, DataType.MuteState));
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
        _source.fireEvent(new NewModelDataEvent(this, DataType.PlayState));
    }

    public PlayState getPlayState()
    {
        return _playState;
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

