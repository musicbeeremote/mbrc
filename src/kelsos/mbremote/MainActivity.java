package kelsos.mbremote;

import android.app.Activity;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import kelsos.mbremote.Messaging.AppNotificationManager;
import kelsos.mbremote.Messaging.ClickSource;
import kelsos.mbremote.Messaging.Communicator;
import kelsos.mbremote.Network.ConnectivityHandler;
import kelsos.mbremote.Network.ReplyHandler;
import kelsos.mbremote.Others.Const;

public class MainActivity extends Activity {
    private static final String BY = "\nby ";
    private static final String LYRICS_FOR = "Lyrics for ";
    private boolean userChangingVolume;

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // Saving Persisting values
        outState.putCharSequence("Title",getTextViewById(R.id.titleLabel).getText());
        outState.putCharSequence("Artist",getTextViewById(R.id.artistLabel).getText());
        outState.putCharSequence("Album",getTextViewById(R.id.albumLabel).getText());
        outState.putCharSequence("Year",getTextViewById(R.id.yearLabel).getText());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        RegisterListeners();
        AppNotificationManager.getInstance().setContext(getBaseContext());
        startService(new Intent(MainActivity.this, ConnectivityHandler.class));
        registerIntentFilters();
        userChangingVolume = false;
        // Restore Persisting values
        if(savedInstanceState!=null) {
            getTextViewById(R.id.titleLabel).setText(savedInstanceState.getCharSequence("Title"));
            getTextViewById(R.id.artistLabel).setText(savedInstanceState.getCharSequence("Artist"));
            getTextViewById(R.id.albumLabel).setText(savedInstanceState.getCharSequence("Album"));
            getTextViewById(R.id.yearLabel).setText(savedInstanceState.getCharSequence("Year"));
        }
        Communicator.getInstance().activityButtonClicked(ClickSource.Refresh);
        Communicator.getInstance().activityButtonClicked(ClickSource.Initialize);
        Communicator.getInstance().requestConnectionStatus();
        SetTextViewTypeface();
    }

    private void registerIntentFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.VOLUME_DATA);
        filter.addAction(Const.SONG_DATA);
        filter.addAction(Const.SONG_COVER);
        filter.addAction(Const.PLAY_STATE);
        filter.addAction(Const.MUTE_STATE);
        filter.addAction(Const.SCROBBLER_STATE);
        filter.addAction(Const.REPEAT_STATE);
        filter.addAction(Const.SHUFFLE_STATE);
        filter.addAction(Const.LYRICS_DATA);
        filter.addAction(Const.CONNECTION_STATUS);
        registerReceiver(mReceiver, filter);
    }

    /**
     * Sets the typeface of the textviews in the main activity to roboto.
     */
    private void SetTextViewTypeface()
    {
        /* Marquee Hack */
        getTextViewById(R.id.artistLabel).setSelected(true);
        getTextViewById(R.id.titleLabel).setSelected(true);
        getTextViewById(R.id.albumLabel).setSelected(true);
        getTextViewById(R.id.yearLabel).setSelected(true);

        if(Build.VERSION.SDK_INT>=14) return;
        Typeface font = Typeface.createFromAsset(getAssets(),"Roboto-Light.ttf");
        getTextViewById(R.id.artistLabel).setTypeface(font);
        getTextViewById(R.id.titleLabel).setTypeface(font);
        getTextViewById(R.id.albumLabel).setTypeface(font);
        getTextViewById(R.id.yearLabel).setTypeface(font);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_settings:
                Intent settingsIntent = new Intent(MainActivity.this, AppPreferenceActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.main_menu_playlist:
                Intent playlistIntent = new Intent(MainActivity.this, PlaylistActivity.class);
                startActivity(playlistIntent);
            case R.id.main_menu_lyrics:
                Communicator.getInstance().activityButtonClicked(ClickSource.Lyrics);
            default:
                return super.onMenuItemSelected(featureId, item);
        }
        return true;

    }

    private ImageButton getImageButtonById(int id) {
        ImageButton button = (ImageButton) findViewById(id);
        return button;
    }

    /**
     * Finds and returns a SeekBar identified by an id.
     *
     * @param id Represents the id of a SeekBar.
     * @return The SeekBar that matches the id.
     */
    private SeekBar getSeekBarById(int id) {
        SeekBar seekBar = (SeekBar) findViewById(id);
        return seekBar;
    }

    /**
     * Finds and returns a TextView identified by an id.
     *
     * @param id Represents a TextView
     * @return The TextView that matched the id.
     */
    private TextView getTextViewById(int id) {
        TextView textView = (TextView) findViewById(id);
        return textView;
    }

    /**
     * Finds and returns an ImageView identified by an id.
     *
     * @param id Represents an ImageView.
     * @return The ImageView that matches the id provided.
     */
    private ImageView getImageViewById(int id) {
        ImageView imageView = (ImageView) findViewById(id);
        return imageView;
    }

    /**
     * Registers the listeners for the interface elements used for interaction.
     */
    private void RegisterListeners() {
        getImageButtonById(R.id.playPauseButton).setOnClickListener(playButtonListener);
        getImageButtonById(R.id.previousButton).setOnClickListener(previousButtonListener);
        getImageButtonById(R.id.nextButton).setOnClickListener(nextButtonListener);
        getSeekBarById(R.id.volumeSlider).setOnSeekBarChangeListener(volumeChangeListener);
        getImageButtonById(R.id.stopButton).setOnClickListener(stopButtonListener);
        getImageButtonById(R.id.stopButton).setEnabled(false);
        getImageButtonById(R.id.muteButton).setOnClickListener(muteButtonListener);
        getImageButtonById(R.id.scrobbleButton).setOnClickListener(scrobbleButtonListener);
        getImageButtonById(R.id.shuffleButton).setOnClickListener(shuffleButtonListener);
        getImageButtonById(R.id.repeatButton).setOnClickListener(repeatButtonListener);
        getImageButtonById(R.id.connectivityIndicator).setOnClickListener(connectivityIndicatorListener);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(Const.VOLUME_DATA)) {
                handleVolumeData(intent);
            } else if (action.equals(Const.CONNECTION_STATUS)) {
                handleConnectionStatus(intent);
            } else if (action.equals(Const.SONG_DATA)) {
                handleSongInfo(intent);
            } else if (action.equals(Const.SONG_COVER)) {
                new ImageDecodeTask().execute();
            } else if (action.equals(Const.PLAY_STATE)) {
                handlePlayState(intent);
            } else if (action.equals(Const.MUTE_STATE)) {
                handleMuteState(intent);
            } else if (action.equals(Const.REPEAT_STATE)) {
                handleRepeatState(intent);
            } else if (action.equals(Const.SHUFFLE_STATE)) {
                handleShuffleState(intent);
            } else if (action.equals(Const.SCROBBLER_STATE)) {
                handleScrobblerState(intent);
            } else if (action.equals(Const.LYRICS_DATA)) {
                processLyricsData();
            }
        }

        private void handleScrobblerState(Intent intent) {
            if (intent.getExtras().getString(Const.STATE).equalsIgnoreCase(Const.TRUE)) {
                getImageButtonById(R.id.scrobbleButton).setImageResource(R.drawable.ic_media_scrobble_red);
            } else {
                getImageButtonById(R.id.scrobbleButton).setImageResource(R.drawable.ic_media_scrobble_off);
            }
        }

        private void handleShuffleState(Intent intent) {
            if (intent.getExtras().getString(Const.STATE).equalsIgnoreCase(Const.TRUE)) {
                getImageButtonById(R.id.shuffleButton).setImageResource(R.drawable.ic_media_shuffle);
            } else {
                getImageButtonById(R.id.shuffleButton).setImageResource(R.drawable.ic_media_shuffle_off);
            }
        }

        private void handleRepeatState(Intent intent) {
            if (intent.getExtras().getString(Const.STATE).equalsIgnoreCase(Const.ALL)) {
                getImageButtonById(R.id.repeatButton).setImageResource(R.drawable.ic_media_repeat);
            } else {
                getImageButtonById(R.id.repeatButton).setImageResource(R.drawable.ic_media_repeat_off);
            }
        }

        private void handleMuteState(Intent intent) {
            if (intent.getExtras().getString(Const.STATE).equalsIgnoreCase(Const.TRUE)) {
                getImageButtonById(R.id.muteButton).setImageResource(R.drawable.ic_media_mute_active);
            } else {
                getImageButtonById(R.id.muteButton).setImageResource(R.drawable.ic_media_mute_full);
            }
        }

        private void handleVolumeData(Intent intent) {
            if (!userChangingVolume)
                getSeekBarById(R.id.volumeSlider).setProgress(intent.getExtras().getInt(Const.DATA));
        }

        private void handlePlayState(Intent intent) {
            if (intent.getExtras().getString(Const.STATE).equals(Const.PLAYING)) {
                getImageButtonById(R.id.playPauseButton).setImageResource(R.drawable.ic_media_pause);
                getImageButtonById(R.id.stopButton).setImageResource(R.drawable.ic_media_stop);
                getImageButtonById(R.id.stopButton).setEnabled(true);
            } else if (intent.getExtras().getString(Const.STATE).equals(Const.PAUSED)) {
                getImageButtonById(R.id.playPauseButton).setImageResource(R.drawable.ic_media_play);
                getImageButtonById(R.id.stopButton).setEnabled(true);
            } else if (intent.getExtras().getString(Const.STATE).equals(Const.STOPPED)) {
                getImageButtonById(R.id.playPauseButton).setImageResource(R.drawable.ic_media_play);
                getImageButtonById(R.id.stopButton).setImageResource(R.drawable.ic_media_stop_pressed);
                getImageButtonById(R.id.stopButton).setEnabled(false);
            }
        }

        private void handleSongInfo(Intent intent) {
            getTextViewById(R.id.artistLabel).setText(intent.getExtras().getString(Const.ARTIST));
            getTextViewById(R.id.titleLabel).setText(intent.getExtras().getString(Const.TITLE));
            getTextViewById(R.id.albumLabel).setText(intent.getExtras().getString(Const.ALBUM));
            getTextViewById(R.id.yearLabel).setText(intent.getExtras().getString(Const.YEAR));
        }

        private void handleConnectionStatus(Intent intent) {
            boolean status = intent.getBooleanExtra(Const.STATUS, false);
            Log.d("ConIn:", String.valueOf(status));
            if (status) {
                getImageViewById(R.id.connectivityIndicator).setImageResource(R.drawable.ic_icon_indicator_green);
            } else {
                getImageViewById(R.id.connectivityIndicator).setImageResource(R.drawable.ic_icon_indicator_red);
            }
        }
    };

    private class ImageDecodeTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            if (ReplyHandler.getInstance().getCoverData().equals("")) {
                return BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_image_no_cover);
            }
            byte[] decodedImage = Base64.decode(ReplyHandler.getInstance().getCoverData(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            getImageViewById(R.id.albumCover).setImageBitmap(result);
            ReplyHandler.getInstance().clearCoverData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }



    /**
     *  When this function is called is either displaying a "No Lyrics found" toast
     *  message or it displays a popup with the lyrics of the track.
     *
     */
    private void processLyricsData() {
        if (ReplyHandler.getInstance().getSongLyrics()=="") {
            AppNotificationManager.getInstance().showToastMessage(R.string.no_lyrics_found);
            return;
        }
        LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int windowWidth = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getWidth();
        int windowHeight = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();

        final PopupWindow lyricsPopup = new PopupWindow(layoutInflater.inflate(
                R.layout.popup_lyrics, null, false), windowWidth,
                windowHeight - 30, true);
        lyricsPopup.setOutsideTouchable(true);

        CharSequence artist = getTextViewById(R.id.artistLabel).getText();
        CharSequence title = getTextViewById(R.id.titleLabel).getText();

        ((TextView) lyricsPopup.getContentView().findViewById(R.id.lyricsLabel))
                .setText(LYRICS_FOR + title + BY + artist);

        ((TextView) lyricsPopup.getContentView().findViewById(R.id.lyricsText))
                .setText(ReplyHandler.getInstance().getSongLyrics());
        lyricsPopup.getContentView().findViewById(R.id.popup_close_button)
                .setOnClickListener(new OnClickListener() {

                    public void onClick(View v) {
                        lyricsPopup.dismiss();

                    }
                });
        lyricsPopup.showAtLocation(findViewById(R.id.mainLinearLayout),
                Gravity.CENTER, 0, 0);
        ReplyHandler.getInstance().clearLyrics();
    }

    private OnClickListener playButtonListener = new OnClickListener() {

        public void onClick(View v) {
            Communicator.getInstance().activityButtonClicked(ClickSource.PlayPause);
                                                                                             }
    };

    private OnClickListener previousButtonListener = new OnClickListener() {

        public void onClick(View v) {
            Communicator.getInstance().activityButtonClicked(ClickSource.Previous);
        }
    };

    private OnClickListener nextButtonListener = new OnClickListener() {

        public void onClick(View v) {
            Communicator.getInstance().activityButtonClicked(ClickSource.Next);
        }
    };

    private OnClickListener stopButtonListener = new OnClickListener() {

        public void onClick(View v) {
            Communicator.getInstance().activityButtonClicked(ClickSource.Stop);
        }
    };

    private OnClickListener muteButtonListener = new OnClickListener() {

        public void onClick(View v) {
            Communicator.getInstance().activityButtonClicked(ClickSource.Mute);
        }
    };

    private OnClickListener scrobbleButtonListener = new OnClickListener() {

        public void onClick(View v) {
            Communicator.getInstance().activityButtonClicked(ClickSource.Scrobble);
        }
    };

    private OnClickListener shuffleButtonListener = new OnClickListener() {

        public void onClick(View v) {
            Communicator.getInstance().activityButtonClicked(ClickSource.Shuffle);
        }
    };

    private OnClickListener repeatButtonListener = new OnClickListener() {

        public void onClick(View v) {
            Communicator.getInstance().activityButtonClicked(ClickSource.Repeat);
        }
    };
    private OnClickListener connectivityIndicatorListener = new OnClickListener() {

        public void onClick(View v) {
            Communicator.getInstance().activityButtonClicked(ClickSource.Repeat);
        }
    };

    private OnSeekBarChangeListener volumeChangeListener = new OnSeekBarChangeListener() {

        public void onStopTrackingTouch(SeekBar seekBar) {
            userChangingVolume = false;

        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            userChangingVolume = true;

        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser)
            Communicator.getInstance().seekBarChanged(seekBar.getProgress());
        }
    };

}